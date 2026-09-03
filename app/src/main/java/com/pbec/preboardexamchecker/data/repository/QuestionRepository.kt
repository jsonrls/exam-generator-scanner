package com.pbec.preboardexamchecker.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pbec.preboardexamchecker.data.models.Question
import com.pbec.preboardexamchecker.data.models.TrashedBank
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val QUESTION_BANKS_COLLECTION = "question_banks"
        private const val QUESTIONS_SUBCOLLECTION = "questions"
        private const val DEFAULT_BANK_PREFIX = "default_"
        private val QUESTION_WHITESPACE = Regex("\\s+")

        fun defaultQuestionBankId(subject: String): String = DEFAULT_BANK_PREFIX +
            subject.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')

        fun isDefaultQuestionBankId(questionBankId: String): Boolean =
            questionBankId.startsWith(DEFAULT_BANK_PREFIX)
    }

    suspend fun insertQuestions(questions: List<Question>): Int {
        if (questions.isEmpty()) return 0
        require(questions.none { it.isDefault }) { "Default questions are read-only." }
        val owner = currentOwner()
        var totalWritten = 0

        questions.groupBy { it.subject }.forEach { (subject, groupedQuestions) ->
            // All default and teacher-authored questions share one canonical parent per subject.
            // Ownership belongs to each question document, never to the parent bank.
            val bankId = defaultQuestionBankId(subject)
            val questionsRef = firestore.collection(QUESTION_BANKS_COLLECTION)
                .document(bankId)
                .collection(QUESTIONS_SUBCOLLECTION)

            val defaultSignatures = questionsRef
                .whereEqualTo("isDefault", true)
                .get()
                .await()
                .documents
                .mapNotNull { snapshot ->
                    normalizeQuestionSignature(snapshot.getString("questionText").orEmpty())
                        .takeIf { it.isNotEmpty() }
                }
                .toSet()
            val ownedTargets = questionsRef
                .whereEqualTo("uploadedByUid", owner.uid)
                .get()
                .await()
                .documents
                .mapNotNull { snapshot ->
                    val signature = normalizeQuestionSignature(
                        snapshot.getString("questionText").orEmpty(),
                    )
                    if (signature.isEmpty()) return@mapNotNull null
                    val existingId = snapshot.getLong("id")
                        ?: snapshot.id.toLongOrNull()
                        ?: return@mapNotNull null
                    signature to (snapshot.reference to existingId)
                }
                .toMap()
                .toMutableMap()

            val normalizedQuestions = groupedQuestions.map { question ->
                question.copy(
                    id = if (question.id == 0L) generateId() else question.id,
                    questionBankId = bankId,
                )
            }

            // Chunked at 450; Firestore caps a batch at 500 ops.
            var batch = firestore.batch()
            var ops = 0
            val seenIncoming = mutableSetOf<String>()
            for (normalizedQuestion in normalizedQuestions) {
                val signature = normalizeQuestionSignature(normalizedQuestion.questionText)
                if (signature.isEmpty() || !seenIncoming.add(signature) || signature in defaultSignatures) {
                    continue
                }
                val existingTarget = ownedTargets[signature]
                val targetId = existingTarget?.second ?: normalizedQuestion.id
                val targetRef = existingTarget?.first ?: questionsRef.document(targetId.toString())
                val questionToWrite = normalizedQuestion.copy(id = targetId)
                batch.set(
                    targetRef,
                    mapOf(
                        "id" to questionToWrite.id,
                        "subject" to questionToWrite.subject,
                        "fileName" to questionToWrite.fileName,
                        "category" to questionToWrite.category,
                        "topic" to questionToWrite.topic,
                        "questionNumber" to questionToWrite.questionNumber,
                        "questionText" to questionToWrite.questionText,
                        "optionA" to questionToWrite.optionA,
                        "optionB" to questionToWrite.optionB,
                        "optionC" to questionToWrite.optionC,
                        "optionD" to questionToWrite.optionD,
                        "correctAnswer" to questionToWrite.correctAnswer,
                        "questionBankId" to questionToWrite.questionBankId,
                        "importSessionId" to questionToWrite.importSessionId,
                        "customSessionName" to questionToWrite.customSessionName,
                        "sourceFileName" to questionToWrite.fileName,
                        "isDefault" to false,
                        "uploadedByUid" to owner.uid,
                        "uploadedByTeacherId" to owner.teacherId,
                        "syncedAt" to com.google.firebase.Timestamp.now()
                    ),
                    SetOptions.merge(),
                )
                ownedTargets[signature] = targetRef to targetId
                totalWritten++
                if (++ops >= 450) { batch.commit().await(); batch = firestore.batch(); ops = 0 }
            }
            if (ops > 0) batch.commit().await()
        }
        return totalWritten
    }

    fun getQuestionsBySubject(subject: String): Flow<List<Question>> {
        return callbackFlow {
            val owner = runCatching { currentOwner() }.getOrElse {
                trySend(emptyList())
                awaitClose { }
                return@callbackFlow
            }

            var ownQuestionDocs = emptyList<com.google.firebase.firestore.DocumentSnapshot>()
            var defaultQuestionDocs = emptyList<com.google.firebase.firestore.DocumentSnapshot>()
            val bankId = defaultQuestionBankId(subject)
            val questions = firestore.collection(QUESTION_BANKS_COLLECTION)
                .document(bankId)
                .collection(QUESTIONS_SUBCOLLECTION)

            fun publishQuestions() {
                val visible = (defaultQuestionDocs + ownQuestionDocs)
                    .distinctBy { it.id }
                    .mapNotNull { it.toQuestion(bankId, true) }
                    .filter { it.subject == subject }
                    .sortedBy { it.questionNumber }
                trySend(visible)
            }

            val ownedListener = questions
                .whereEqualTo("uploadedByUid", owner.uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    ownQuestionDocs = snapshot?.documents.orEmpty()
                    publishQuestions()
                }
            val defaultListener = questions
                .whereEqualTo("isDefault", true)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    defaultQuestionDocs = snapshot?.documents.orEmpty()
                    publishQuestions()
                }

            awaitClose {
                ownedListener.remove()
                defaultListener.remove()
            }
        }
    }

    suspend fun getAllQuestionsForSubjectOnce(subject: String): List<Question> {
        val owner = currentOwner()
        val bank = AccessibleBank(defaultQuestionBankId(subject), true)
        return loadQuestionsFromBanks(owner.uid, listOf(bank))
            .filter { it.subject == subject }
            .sortedWith(compareBy<Question> { it.questionBankId }.thenBy { it.questionNumber })
    }

    suspend fun deleteQuestion(question: Question): Int {
        if (question.isDefault) return 0
        val owner = currentOwner()
        val directDocRef = firestore.collection(QUESTION_BANKS_COLLECTION)
            .document(question.questionBankId)
            .collection(QUESTIONS_SUBCOLLECTION)
            .document(question.id.toString())
        val directDoc = directDocRef.get().await()
        if (directDoc.exists() && directDoc.getString("uploadedByUid") == owner.uid) {
            directDocRef.delete().await()
            return 1
        }

        // A migrated legacy question may still carry its former bank id in local Room state.
        val bankIds = canonicalQuestionBankIds()
        var deleted = 0
        bankIds.forEach { bankId ->
            val docRef = firestore.collection(QUESTION_BANKS_COLLECTION)
                .document(bankId)
                .collection(QUESTIONS_SUBCOLLECTION)
                .document(question.id.toString())
            val snapshot = docRef.get().await()
            if (snapshot.exists() && snapshot.getString("uploadedByUid") == owner.uid) {
                docRef.delete().await()
                deleted++
            }
        }
        return deleted
    }

    suspend fun deleteQuestionsByImportSessionId(importSessionId: Long): Int {
        val owner = currentOwner()
        var deletedCount = 0
        canonicalQuestionBankIds().forEach { bankId ->
            val col = firestore.collection(QUESTION_BANKS_COLLECTION)
                .document(bankId)
                .collection(QUESTIONS_SUBCOLLECTION)
            val matchingDocs = findByImportSessionId(col, owner.uid, importSessionId)
            if (matchingDocs.isNotEmpty()) {
                batchDelete(matchingDocs.map { it.reference })
            }
            deletedCount += matchingDocs.size
        }
        return deletedCount
    }

    suspend fun deleteQuestionsByQuestionBankId(questionBankId: String): Int {
        val owner = currentOwner()
        val directBankId = questionBankId.takeIf(::isDefaultQuestionBankId)
        val bankDocs = directBankId?.let { bankId -> firestore.collection(QUESTION_BANKS_COLLECTION)
            .document(bankId)
            .collection(QUESTIONS_SUBCOLLECTION)
            .whereEqualTo("uploadedByUid", owner.uid)
            .get()
            .await()
            .documents }.orEmpty()
        if (bankDocs.isNotEmpty()) {
            batchDelete(bankDocs.map { it.reference })
            return bankDocs.size
        }

        // Legacy fallback: locate migrated docs by their former metadata value.
        var deletedCount = 0
        canonicalQuestionBankIds().forEach { bankId ->
            val docs = firestore.collection(QUESTION_BANKS_COLLECTION)
                .document(bankId)
                .collection(QUESTIONS_SUBCOLLECTION)
                .whereEqualTo("uploadedByUid", owner.uid)
                .get()
                .await()
                .documents
            val matchingDocs = docs.filter { doc ->
                val docBankId = doc.getString("questionBankId")
                val matchesBankId = docBankId == questionBankId
                val legacyImportSession = doc.getLong("importSessionId")
                    ?: doc.getString("importSessionId")?.toLongOrNull()
                val matchesLegacyBank = questionBankId.startsWith("legacy_") &&
                    legacyImportSession?.let { "legacy_$it" == questionBankId } == true
                matchesBankId || matchesLegacyBank
            }
            if (matchingDocs.isNotEmpty()) {
                batchDelete(matchingDocs.map { it.reference })
            }
            deletedCount += matchingDocs.size
        }
        return deletedCount
    }

    suspend fun getQuestionsByImportSessionIds(subject: String, importSessionIds: List<Long>): List<Question> {
        if (importSessionIds.isEmpty()) return emptyList()
        return getAllQuestionsForSubjectOnce(subject)
            .filter { importSessionIds.contains(it.importSessionId) }
    }

    suspend fun getQuestionsByImportSessionIdsOnly(importSessionIds: List<Long>): List<Question> {
        if (importSessionIds.isEmpty()) return emptyList()
        val owner = currentOwner()
        val docs = firestore.collectionGroup(QUESTIONS_SUBCOLLECTION)
            .whereEqualTo("uploadedByUid", owner.uid)
            .get()
            .await()
        return docs.documents
            .mapNotNull { it.toQuestion() }
            .filter { importSessionIds.contains(it.importSessionId) }
            .sortedWith(compareBy<Question> { it.questionBankId }.thenBy { it.questionNumber })
    }

    suspend fun getQuestionsByQuestionBankIdsOnly(questionBankIds: List<String>): List<Question> {
        if (questionBankIds.isEmpty()) return emptyList()
        val owner = currentOwner()
        val activeBanks = questionBankIds
            .filter(::isDefaultQuestionBankId)
            .distinct()
            .map { AccessibleBank(it, true) }
        val activeBankIds = activeBanks.map { it.id }.toSet()
        return loadQuestionsFromBanks(owner.uid, activeBanks)
            .filter { it.questionBankId in activeBankIds }
            .sortedWith(compareBy<Question> { it.questionBankId }.thenBy { it.questionNumber })
    }

    suspend fun updateCustomSessionName(importSessionId: Long, newName: String): Int {
        val owner = currentOwner()
        // Per-bank queries: a collectionGroup filter on importSessionId would need a
        // collection-group index.
        val matchingDocs = canonicalQuestionBankIds().flatMap { bankId ->
            val col = firestore.collection(QUESTION_BANKS_COLLECTION)
                .document(bankId)
                .collection(QUESTIONS_SUBCOLLECTION)
            findByImportSessionId(col, owner.uid, importSessionId)
        }.distinctBy { it.reference.path }
        batchUpdate(matchingDocs.map { it.reference }, mapOf("customSessionName" to newName))
        return matchingDocs.size
    }

    suspend fun updateCustomSessionNameByQuestionBankId(questionBankId: String, newName: String): Int {
        val owner = currentOwner()
        // legacy_<sessionId> banks: old docs may carry only the session id, so match both ways.
        val legacySessionId = questionBankId.takeIf { it.startsWith("legacy_") }
            ?.removePrefix("legacy_")?.toLongOrNull()
        val matchingDocs = canonicalQuestionBankIds().flatMap { bankId ->
            val col = firestore.collection(QUESTION_BANKS_COLLECTION)
                .document(bankId)
                .collection(QUESTIONS_SUBCOLLECTION)
            val byBankField = col.whereEqualTo("uploadedByUid", owner.uid)
                .whereEqualTo("questionBankId", questionBankId)
                .get()
                .await()
                .documents
            val byLegacySession = legacySessionId?.let { findByImportSessionId(col, owner.uid, it) }.orEmpty()
            byBankField + byLegacySession
        }.distinctBy { it.reference.path }
        batchUpdate(matchingDocs.map { it.reference }, mapOf("customSessionName" to newName))
        return matchingDocs.size
    }

    /** Soft-delete a bank: its questions stop being available for exam generation, restorable for
     *  30 days. Fire-and-forget (offline-durable). */
    suspend fun softDeleteQuestionBank(questionBankId: String) {
        val owner = currentOwner()
        updateBankIfOwned(
            questionBankId,
            owner.teacherId,
            mapOf("deletedAt" to System.currentTimeMillis()),
        )
    }

    fun getTrashedBanks(): Flow<List<TrashedBank>> = callbackFlow {
        val owner = runCatching { currentOwner() }.getOrElse {
            trySend(emptyList()); awaitClose { }; return@callbackFlow
        }
        val listener = firestore.collection(QUESTION_BANKS_COLLECTION)
            .whereEqualTo("uploadedByTeacherId", owner.teacherId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { trySend(emptyList()); return@addSnapshotListener }
                val trashed = snapshot?.documents.orEmpty()
                    .filter { it.getLong("deletedAt") != null }
                    .map { doc ->
                        TrashedBank(
                            bankId = doc.id,
                            displayName = doc.getString("displayName")
                                ?: doc.getString("sourceFileName") ?: "Imported Bank",
                            subject = doc.getString("subject") ?: "",
                            questionCount = doc.getLong("questionCount")?.toInt() ?: 0,
                            deletedAt = doc.getLong("deletedAt") ?: 0L,
                        )
                    }
                    .sortedByDescending { it.deletedAt }
                trySend(trashed)
            }
        awaitClose { listener.remove() }
    }

    suspend fun restoreQuestionBank(questionBankId: String) {
        val owner = currentOwner()
        updateBankIfOwned(questionBankId, owner.teacherId, mapOf("deletedAt" to null))
    }

    /** Permanently delete a bank and all of its questions. */
    suspend fun purgeQuestionBank(questionBankId: String) {
        val owner = currentOwner()
        val questionDocs = firestore.collection(QUESTION_BANKS_COLLECTION)
            .document(questionBankId)
            .collection(QUESTIONS_SUBCOLLECTION)
            .whereEqualTo("uploadedByUid", owner.uid)
            .get()
            .await()
            .documents
        questionDocs.forEach { it.reference.delete() }
        deleteBankIfOwned(questionBankId, owner.teacherId)
    }

    /** Ids of trashed banks past the 30-day window — the caller purges each (and its linked exams). */
    suspend fun getExpiredBankIds(): List<String> {
        val owner = currentOwner()
        val threshold = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
        return firestore.collection(QUESTION_BANKS_COLLECTION)
            .whereEqualTo("uploadedByTeacherId", owner.teacherId)
            .get()
            .await()
            .documents
            .filter { val d = it.getLong("deletedAt"); d != null && d < threshold }
            .map { it.id }
    }

    private suspend fun ensureFirebaseUserUid(): String {
        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.uid?.let { return it }
        val authResult = auth.signInAnonymously().await()
        return authResult.user?.uid ?: throw IllegalStateException("Firebase sign-in failed: missing user.")
    }

    private suspend fun currentOwner(): QuestionOwner {
        val uid = ensureFirebaseUserUid()
        val teacherId = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getString("teacher_id", null)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: uid
        return QuestionOwner(uid = uid, teacherId = teacherId)
    }

    // Question docs are keyed by id.toString(): a collision overwrites another question.
    private fun generateId(): Long = com.pbec.preboardexamchecker.utils.IdGenerator.newId()

    private suspend fun loadQuestionsFromBanks(
        uid: String,
        banks: List<AccessibleBank>,
    ): List<Question> {
        if (banks.isEmpty()) return emptyList()
        // One round-trip per bank, fetched concurrently.
        return coroutineScope {
            banks.map { bank ->
                async {
                    val query = firestore.collection(QUESTION_BANKS_COLLECTION)
                        .document(bank.id)
                        .collection(QUESTIONS_SUBCOLLECTION)
                    val docs = if (bank.isDefault) {
                        val defaultDocs = async {
                            query.whereEqualTo("isDefault", true).get().await().documents
                        }
                        val teacherDocs = async {
                            query.whereEqualTo("uploadedByUid", uid).get().await().documents
                        }
                        (defaultDocs.await() + teacherDocs.await()).distinctBy { it.id }
                    } else {
                        query.whereEqualTo("uploadedByUid", uid).get().await().documents
                    }
                    docs.mapNotNull { it.toQuestion(bank.id, bank.isDefault) }
                }
            }.awaitAll()
        }.flatten()
    }

    private fun canonicalQuestionBankIds(): List<String> = listOf(
        defaultQuestionBankId("Mathematics"),
        defaultQuestionBankId("ESAS"),
        defaultQuestionBankId("Professional EE"),
    )

    private fun normalizeQuestionSignature(questionText: String): String =
        QUESTION_WHITESPACE.replace(questionText.trim().lowercase(Locale.ROOT), " ")

    /** importSessionId is stored as Long or String depending on document age. */
    private suspend fun findByImportSessionId(
        col: com.google.firebase.firestore.CollectionReference,
        uid: String,
        importSessionId: Long,
    ): List<com.google.firebase.firestore.DocumentSnapshot> {
        return col.whereEqualTo("uploadedByUid", uid).get().await().documents.filter { doc ->
            doc.getLong("importSessionId") == importSessionId ||
                doc.getString("importSessionId")?.toLongOrNull() == importSessionId
        }
    }

    /** Chunked at 450; Firestore caps a batch at 500 ops. */
    private suspend fun batchDelete(refs: List<com.google.firebase.firestore.DocumentReference>) {
        var batch = firestore.batch()
        var ops = 0
        for (ref in refs) {
            batch.delete(ref)
            if (++ops >= 450) { batch.commit().await(); batch = firestore.batch(); ops = 0 }
        }
        if (ops > 0) batch.commit().await()
    }

    /** Same [updates] to every doc; chunked at 450 (batch cap 500). */
    private suspend fun batchUpdate(
        refs: List<com.google.firebase.firestore.DocumentReference>,
        updates: Map<String, Any>,
    ) {
        var batch = firestore.batch()
        var ops = 0
        for (ref in refs) {
            batch.update(ref, updates)
            if (++ops >= 450) { batch.commit().await(); batch = firestore.batch(); ops = 0 }
        }
        if (ops > 0) batch.commit().await()
    }

    private suspend fun updateBankIfOwned(
        questionBankId: String,
        teacherId: String,
        updates: Map<String, Any?>,
    ) {
        if (isDefaultQuestionBankId(questionBankId)) return
        val bankRef = firestore.collection(QUESTION_BANKS_COLLECTION).document(questionBankId)
        val bank = bankRef.get().await()
        if (bank.getBoolean("isDefault") != true && bank.getString("uploadedByTeacherId") == teacherId) {
            bankRef.set(updates, SetOptions.merge()).await()
        }
    }

    private suspend fun deleteBankIfOwned(questionBankId: String, teacherId: String) {
        if (isDefaultQuestionBankId(questionBankId)) return
        val bankRef = firestore.collection(QUESTION_BANKS_COLLECTION).document(questionBankId)
        val bank = bankRef.get().await()
        if (bank.getBoolean("isDefault") != true && bank.getString("uploadedByTeacherId") == teacherId) {
            bankRef.delete().await()
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toQuestion(
        containingBankId: String = reference.parent.parent?.id.orEmpty(),
        isDefaultBank: Boolean = false,
    ): Question? {
        val subject = getString("subject") ?: return null
        val fileName = getString("fileName")
            ?: getString("sourceFileName")
            ?: "Imported Bank"
        val questionText = getString("questionText") ?: return null
        val optionA = getString("optionA").orEmpty()
        val optionB = getString("optionB").orEmpty()
        val optionC = getString("optionC").orEmpty()
        val optionD = getString("optionD").orEmpty()

        val fallbackId = id.hashCode().toLong().let { if (it < 0) -it else it }
        val normalizedId = getLong("id") ?: fallbackId

        val parsedImportSessionId = getLong("importSessionId")
            ?: getString("importSessionId")?.toLongOrNull()
            ?: 0L
        val sourceFileName = getString("sourceFileName")
        val parsedQuestionBankId = getString("questionBankId")
            ?: containingBankId.takeIf { it.isNotBlank() }
            ?: when {
                parsedImportSessionId != 0L -> "legacy_$parsedImportSessionId"
                !sourceFileName.isNullOrBlank() -> "legacy_file_${sourceFileName.trim().lowercase()}"
                else -> "manual"
            }

        return Question(
            id = normalizedId,
            subject = subject,
            fileName = fileName,
            category = getString("category"),
            topic = getString("topic"),
            questionNumber = getLong("questionNumber")?.toInt()
                ?: getDouble("questionNumber")?.toInt()
                ?: getString("questionNumber")?.toIntOrNull()
                ?: 0,
            questionText = questionText,
            optionA = optionA,
            optionB = optionB,
            optionC = optionC,
            optionD = optionD,
            correctAnswer = getString("correctAnswer"),
            questionBankId = parsedQuestionBankId,
            importSessionId = parsedImportSessionId,
            customSessionName = getString("customSessionName"),
            isDefault = getBoolean("isDefault") == true ||
                (isDefaultBank && getString("uploadedByUid").isNullOrBlank() &&
                    getString("uploadedByTeacherId").isNullOrBlank()),
            uploadedByUid = getString("uploadedByUid").orEmpty(),
            uploadedByTeacherId = getString("uploadedByTeacherId").orEmpty(),
        )
    }

    private data class QuestionOwner(val uid: String, val teacherId: String)
    private data class AccessibleBank(val id: String, val isDefault: Boolean)
}

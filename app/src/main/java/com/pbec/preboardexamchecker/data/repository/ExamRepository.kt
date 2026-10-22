package com.pbec.preboardexamchecker.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pbec.preboardexamchecker.data.models.ArchivedExam
import com.pbec.preboardexamchecker.data.models.ArchivedQuestion
import com.pbec.preboardexamchecker.data.models.Exam
import com.pbec.preboardexamchecker.data.models.ExamData
import com.pbec.preboardexamchecker.data.models.Question
import com.pbec.preboardexamchecker.data.models.TrashedExam
import java.util.concurrent.TimeUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : IExamRepository {
    suspend fun insertExam(
        exam: Exam,
        selectedImportSessionIds: List<Long> = emptyList(),
        selectedQuestionBankIds: List<String> = emptyList(),
        generatedQuestionCount: Int? = null,
        usedBlueprint: Boolean? = null,
        usedRandomFallback: Boolean? = null
    ): Long {
        val uid = ensureFirebaseUserUid()
        val teacherId = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getString("teacher_id", null)
            ?: uid
        val normalizedExam = if (exam.id == 0L) exam.copy(id = generateId()) else exam
        val existingDocs = findExamDocsById(uid, normalizedExam.id)
        val targetDoc = existingDocs.firstOrNull()?.reference
            ?: firestore.collection("exams").document()

        val payload = mutableMapOf<String, Any>(
            "id" to normalizedExam.id,
            "examName" to normalizedExam.examName,
            "subject" to normalizedExam.subject,
            "setAQuestionIds" to normalizedExam.setAQuestionIds,
            "setBQuestionIds" to normalizedExam.setBQuestionIds,
            "questionIds" to normalizedExam.questionIds,
            "createdAt" to normalizedExam.createdAt,
            "uploadedByUid" to uid,
            "uploadedByTeacherId" to teacherId,
            "syncedAt" to com.google.firebase.Timestamp.now()
        )
        if (selectedImportSessionIds.isNotEmpty()) payload["selectedImportSessionIds"] = selectedImportSessionIds
        val bankIds = selectedQuestionBankIds.ifEmpty { exam.selectedQuestionBankIds }
        if (bankIds.isNotEmpty()) payload["selectedQuestionBankIds"] = bankIds
        if (generatedQuestionCount != null) payload["generatedQuestionCount"] = generatedQuestionCount
        if (usedBlueprint != null) payload["usedBlueprint"] = usedBlueprint
        if (usedRandomFallback != null) payload["usedRandomFallback"] = usedRandomFallback

        targetDoc.set(payload, SetOptions.merge()).await()

        return normalizedExam.id
    }

    fun getAllExams(): Flow<List<Exam>> {
        return callbackFlow {
            val uid = try { ensureFirebaseUserUid() } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                close(error)
                return@callbackFlow
            }

            var parsing: Job? = null
            val listener = firestore.collection("exams")
                .whereEqualTo("uploadedByUid", uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("ExamRepository", "Exam list listener failed", error)
                        close(error)
                        return@addSnapshotListener
                    }
                    parsing?.cancel()
                    parsing = launch {
                        try {
                            val exams = snapshot?.documents
                                ?.filter { it.get("deletedAt") == null }
                                ?.mapNotNull { it.toExam() }
                                ?.sortedByDescending { it.createdAt }
                                .orEmpty()
                            trySend(exams)
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            Log.e("ExamRepository", "Exam list could not be parsed", e)
                            close(e)
                        }
                    }
                }
            awaitClose { parsing?.cancel(); listener.remove() }
        }
    }

    fun getExamsBySubject(subject: String): Flow<List<Exam>> {
        return callbackFlow {
            val uid = try { ensureFirebaseUserUid() } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                close(error)
                return@callbackFlow
            }

            var parsing: Job? = null
            val listener = firestore.collection("exams")
                .whereEqualTo("uploadedByUid", uid)
                .whereEqualTo("subject", subject)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("ExamRepository", "Subject exam listener failed", error)
                        close(error)
                        return@addSnapshotListener
                    }
                    parsing?.cancel()
                    parsing = launch {
                        try {
                            val exams = snapshot?.documents
                                ?.filter { it.get("deletedAt") == null }
                                ?.mapNotNull { it.toExam() }
                                ?.sortedByDescending { it.createdAt }
                                .orEmpty()
                            trySend(exams)
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            Log.e("ExamRepository", "Subject exam list could not be parsed", e)
                            close(e)
                        }
                    }
                }
            awaitClose { parsing?.cancel(); listener.remove() }
        }
    }

    override suspend fun getExamsBySubjectOnce(subject: String): List<Exam> {
        return loadExams(subject, com.google.firebase.firestore.Source.DEFAULT)
    }

    override suspend fun getExamsForOfflinePreparation(subject: String): List<Exam> =
        loadExams(subject, com.google.firebase.firestore.Source.SERVER)

    private suspend fun loadExams(subject: String, source: com.google.firebase.firestore.Source): List<Exam> {
        val uid = if (source == com.google.firebase.firestore.Source.SERVER)
            FirebaseAuth.getInstance().currentUser?.takeUnless { it.isAnonymous }?.uid
                ?: error("Sign in before preparing a session.")
            else ensureFirebaseUserUid()
        val snapshot = firestore.collection("exams")
            .whereEqualTo("uploadedByUid", uid)
            .whereEqualTo("subject", subject)
            .get(source)
            .await()
        return snapshot.documents
            .filter { it.get("deletedAt") == null }
            .mapNotNull { it.toExam(source) }
            .sortedByDescending { it.createdAt }
    }

    suspend fun getExamById(examId: Long): Exam? {
        val uid = ensureFirebaseUserUid()
        return findExamDocsById(uid, examId)
            .firstOrNull { it.get("deletedAt") == null }
            ?.toExam()
    }

    private suspend fun findExamDocsById(
        uid: String,
        examId: Long,
    ): List<com.google.firebase.firestore.DocumentSnapshot> {
        val documents = firestore.collection("exams")
            .whereEqualTo("uploadedByUid", uid).get().await().documents
            .filter { ExamData.examId(it.id, it.get("id")) == examId }
        require(documents.size <= 1) { "Ambiguous exam ID $examId." }
        return documents
    }

    // Soft-delete (deletedAt stamped, restorable 30 days). Writes are fire-and-forget so they
    // succeed offline; the lookup get() resolves from the snapshot listener's cache.
    suspend fun deleteExam(exam: Exam): Int {
        val uid = ensureFirebaseUserUid()
        val docs = findExamDocs(uid, exam)
        val now = System.currentTimeMillis()
        docs.forEach {
            it.reference.set(mapOf("deletedAt" to now), SetOptions.merge())
                .addOnFailureListener { e -> Log.e("ExamRepository", "Soft-delete write failed for exam ${exam.id}", e) }
        }
        return docs.size
    }

    /** Locate an exam's Firestore doc(s): by id, falling back to subject/name/createdAt. */
    private suspend fun findExamDocs(
        uid: String,
        exam: Exam,
    ): List<com.google.firebase.firestore.DocumentSnapshot> {
        val byId = findExamDocsById(uid, exam.id)
        if (byId.isNotEmpty()) return byId

        return firestore.collection("exams")
            .whereEqualTo("uploadedByUid", uid)
            .whereEqualTo("subject", exam.subject)
            .whereEqualTo("examName", exam.examName)
            .whereEqualTo("createdAt", exam.createdAt)
            .get()
            .await()
            .documents
    }

    /** Standalone-trashed exams only (excludes those swept in with a deleted bank). */
    fun getTrashedExams(): Flow<List<TrashedExam>> = callbackFlow {
        val uid = try { ensureFirebaseUserUid() } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            close(error)
            return@callbackFlow
        }
        val listener = firestore.collection("exams")
            .whereEqualTo("uploadedByUid", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                publishSnapshot {
                    snapshot?.documents.orEmpty()
                        .filter { it.get("deletedAt") != null && it.getString("trashedByBankId") == null }
                        .map { doc ->
                            TrashedExam(ExamData.examId(doc.id, doc.get("id")),
                                doc.getString("examName") ?: error("Deleted exam ${doc.id} has no name."),
                                doc.getString("subject") ?: error("Deleted exam ${doc.id} has no subject."),
                                ExamData.timestampMillis(doc.get("deletedAt")))
                        }
                        .sortedByDescending { it.deletedAt }
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun restoreExam(examId: Long) {
        val uid = ensureFirebaseUserUid()
        findExamDocsById(uid, examId)
            .forEach { it.reference.set(mapOf("deletedAt" to null, "trashedByBankId" to null), SetOptions.merge()) }
    }

    suspend fun purgeExam(examId: Long) {
        val uid = ensureFirebaseUserUid()
        findExamDocsById(uid, examId)
            .forEach { it.reference.delete() }
    }

    /** Permanently remove standalone-trashed exams whose retention window has elapsed. */
    suspend fun purgeExpiredExams() {
        val uid = ensureFirebaseUserUid()
        val threshold = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
        firestore.collection("exams")
            .whereEqualTo("uploadedByUid", uid)
            .get()
            .await()
            .documents
            .filter { doc ->
                val deletedAt = doc.getLong("deletedAt")
                deletedAt != null && deletedAt < threshold && doc.getString("trashedByBankId") == null
            }
            .forEach { it.reference.delete() }
    }

    suspend fun restoreExamsTrashedByBank(bankId: String) {
        val uid = ensureFirebaseUserUid()
        firestore.collection("exams")
            .whereEqualTo("uploadedByUid", uid)
            .whereEqualTo("trashedByBankId", bankId)
            .get()
            .await()
            .documents
            .forEach { it.reference.set(mapOf("deletedAt" to null, "trashedByBankId" to null), SetOptions.merge()) }
    }

    suspend fun purgeExamsTrashedByBank(bankId: String) {
        val uid = ensureFirebaseUserUid()
        firestore.collection("exams")
            .whereEqualTo("uploadedByUid", uid)
            .whereEqualTo("trashedByBankId", bankId)
            .get()
            .await()
            .documents
            .forEach { it.reference.delete() }
    }

    /**
     * Archive [exam] and its referenced questions into `deleted_exams` so Exam Stats survive deletion.
     * One doc per (user, examId) via deterministic id, so re-archiving overwrites. Fire-and-forget
     * (no server-ack await) to stay offline-durable. Call before [deleteExam].
     */
    suspend fun archiveExam(exam: Exam, subjectQuestions: List<Question>) {
        val uid = ensureFirebaseUserUid()
        // First-occurrence-per-id dedup, matching scanner/Exam Stats resolution so the rebuilt key
        // matches what was scored.
        val referencedIds = (exam.setAQuestionIds + exam.setBQuestionIds).toSet()
        val seen = HashSet<Long>()
        val questionSnapshot = subjectQuestions.mapNotNull { q ->
            if (q.id in referencedIds && seen.add(q.id)) {
                mapOf(
                    "id" to q.id,
                    "correctAnswer" to q.correctAnswer,
                    "questionText" to q.questionText,
                    "optionA" to q.optionA,
                    "optionB" to q.optionB,
                    "optionC" to q.optionC,
                    "optionD" to q.optionD,
                )
            } else null
        }

        val payload = mapOf(
            "examId" to exam.id,
            "examName" to exam.examName,
            "subject" to exam.subject,
            "setAQuestionIds" to exam.setAQuestionIds,
            "setBQuestionIds" to exam.setBQuestionIds,
            "questions" to questionSnapshot,
            "uploadedByUid" to uid,
            "archivedAt" to System.currentTimeMillis(),
        )

        // Fire-and-forget: queued durably by Firestore offline persistence.
        firestore.collection("deleted_exams").document(archivedDocId(uid, exam.id)).set(payload)
            .addOnFailureListener { e -> Log.e("ExamRepository", "Archive write failed for exam ${exam.id}", e) }
    }

    override suspend fun getArchivedExamById(examId: Long): ArchivedExam? {
        val uid = ensureFirebaseUserUid()
        // get() falls back to cache offline, so a just-written archive is still readable.
        val snapshot = firestore.collection("deleted_exams")
            .document(archivedDocId(uid, examId))
            .get()
            .await()
        return snapshot.takeIf { it.exists() }?.toArchivedExam()
    }

    override suspend fun deleteArchivedExam(examId: Long) {
        val uid = ensureFirebaseUserUid()
        // Fire-and-forget delete (offline-durable). No-op if no archive exists.
        firestore.collection("deleted_exams").document(archivedDocId(uid, examId)).delete()
            .addOnFailureListener { e -> Log.e("ExamRepository", "Archive delete failed for exam $examId", e) }
    }

    /** Deterministic archive document id so writes/reads/deletes are idempotent and offline-safe. */
    private fun archivedDocId(uid: String, examId: Long): String = "${uid}_$examId"

    suspend fun deleteExams(exams: List<Exam>): Int {
        if (exams.isEmpty()) return 0
        var deleted = 0
        exams.forEach { exam ->
            deleted += deleteExam(exam)
        }
        return deleted
    }

    suspend fun updateExam(exam: Exam): Int {
        insertExam(exam)
        return 1
    }

    suspend fun deleteExamsLinkedToImportSession(
        subject: String,
        importSessionId: Long,
        questionIdsInSession: Set<Long>
    ): Int {
        val uid = ensureFirebaseUserUid()
        val snapshot = firestore.collection("exams")
            .whereEqualTo("uploadedByUid", uid)
            .whereEqualTo("subject", subject)
            .get()
            .await()

        val docsToDelete = snapshot.documents.filter { doc ->
            val examQuestionIds = extractQuestionIds(doc).toSet()
            val linkedByQuestions = questionIdsInSession.isNotEmpty() && examQuestionIds.any(questionIdsInSession::contains)
            val selectedImportSessionIds = (doc.get("selectedImportSessionIds") as? List<*>)?.mapNotNull { value ->
                when (value) {
                    is Number -> value.toLong()
                    is String -> value.toLongOrNull()
                    else -> null
                }
            }.orEmpty()
            val linkedBySessionMetadata = selectedImportSessionIds.contains(importSessionId)
            linkedByQuestions || linkedBySessionMetadata
        }

        docsToDelete.forEach { it.reference.delete().await() }
        return docsToDelete.size
    }

    /**
     * Soft-delete exams generated from [questionBankId], stamping `trashedByBankId` so they
     * restore/purge with the bank. Returns the moved exams so the caller can archive them for Exam
     * Stats. Fire-and-forget (offline-durable).
     */
    suspend fun deleteExamsLinkedToQuestionBank(
        subject: String,
        questionBankId: String,
        questionIdsInBank: Set<Long>
    ): List<Exam> {
        val uid = ensureFirebaseUserUid()
        val snapshot = firestore.collection("exams")
            .whereEqualTo("uploadedByUid", uid)
            .whereEqualTo("subject", subject)
            .get()
            .await()

        val docsToTrash = snapshot.documents.filter { doc ->
            if (doc.getLong("deletedAt") != null) return@filter false  // already trashed
            val examQuestionIds = extractQuestionIds(doc).toSet()
            val linkedByQuestions = questionIdsInBank.isNotEmpty() && examQuestionIds.any(questionIdsInBank::contains)
            val selectedQuestionBankIds = (doc.get("selectedQuestionBankIds") as? List<*>)?.mapNotNull { value ->
                value as? String
            }.orEmpty()
            val linkedByBankMetadata = selectedQuestionBankIds.contains(questionBankId)
            linkedByQuestions || linkedByBankMetadata
        }

        val trashedExams = docsToTrash.mapNotNull { it.toExam() }
        val now = System.currentTimeMillis()
        docsToTrash.forEach {
            it.reference.set(mapOf("deletedAt" to now, "trashedByBankId" to questionBankId), SetOptions.merge())
        }
        return trashedExams
    }

    private suspend fun ensureFirebaseUserUid(): String {
        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.uid?.let { return it }
        val authResult = auth.signInAnonymously().await()
        return authResult.user?.uid ?: throw IllegalStateException("Firebase sign-in failed: missing user.")
    }

    // Non-monotonic is fine: ordering comes from createdAt, never from id.
    private fun generateId(): Long = com.pbec.preboardexamchecker.utils.IdGenerator.newId()

    private suspend fun com.google.firebase.firestore.DocumentSnapshot.toExam(
        source: com.google.firebase.firestore.Source = com.google.firebase.firestore.Source.DEFAULT,
    ): Exam? {
        val examName = getString("examName") ?: error("Exam $id has no name.")
        val subject = getString("subject") ?: error("Exam $id has no subject.")
        val setAReferences = ExamData.references(get("setAQuestionIds") ?: get("questionIds"))
        val setBReferences = ExamData.references(get("setBQuestionIds"))
        val rawBanks = get("selectedQuestionBankIds") ?: get("sessionIds")
        val bankIds = ExamData.bankIds(rawBanks, subject)
        val questions = if ((setAReferences + setBReferences).any { it is String }) {
            val uid = ensureFirebaseUserUid()
            bankIds.flatMap { bankId ->
                val collection = firestore.collection("question_banks").document(bankId).collection("questions")
                val defaults = collection.whereEqualTo("isDefault", true).get(source).await().documents
                val owned = collection.whereEqualTo("uploadedByUid", uid).get(source).await().documents
                (defaults + owned).distinctBy { it.id }.filter { it.getString("subject") == subject }.map { question ->
                    val numericId = if (question.get("id") == null) question.id.toLongOrNull() ?: ExamData.legacyId(question.id)
                        else ExamData.storedLong(question.get("id")) ?: error("Invalid question ID ${question.id}.")
                    ExamData.QuestionReference(question.id, bankId, numericId)
                }
            }
        } else emptyList()
        val setA = ExamData.resolveIds(setAReferences, questions)
        val setB = ExamData.resolveIds(setBReferences, questions)
        val normalizedId = ExamData.examId(id, get("id"))
        return Exam(
            id = normalizedId,
            examName = examName,
            subject = subject,
            setAQuestionIds = setA,
            setBQuestionIds = setB,
            createdAt = ExamData.timestampMillis(get("createdAt"))
        ).also { it.selectedQuestionBankIds = bankIds }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toArchivedExam(): ArchivedExam? {
        val examId = getLong("examId") ?: return null
        val examName = getString("examName") ?: return null
        val subject = getString("subject") ?: return null
        val setA = (get("setAQuestionIds") as? List<*>)?.mapNotNull { (it as? Number)?.toLong() }.orEmpty()
        val setB = (get("setBQuestionIds") as? List<*>)?.mapNotNull { (it as? Number)?.toLong() }.orEmpty()
        val questions = (get("questions") as? List<*>)?.mapNotNull { item ->
            val m = item as? Map<*, *> ?: return@mapNotNull null
            val id = (m["id"] as? Number)?.toLong() ?: return@mapNotNull null
            ArchivedQuestion(
                id = id,
                correctAnswer = m["correctAnswer"] as? String,
                questionText = m["questionText"] as? String ?: "",
                optionA = m["optionA"] as? String ?: "",
                optionB = m["optionB"] as? String ?: "",
                optionC = m["optionC"] as? String ?: "",
                optionD = m["optionD"] as? String ?: "",
            )
        }.orEmpty()
        return ArchivedExam(
            examId = examId,
            examName = examName,
            subject = subject,
            setAQuestionIds = setA,
            setBQuestionIds = setB,
            questions = questions,
            archivedAt = getLong("archivedAt") ?: 0L,
        )
    }

    private suspend fun extractQuestionIds(document: com.google.firebase.firestore.DocumentSnapshot): List<Long> {
        val exam = document.toExam() ?: return emptyList()
        return exam.setAQuestionIds + exam.setBQuestionIds
    }
}

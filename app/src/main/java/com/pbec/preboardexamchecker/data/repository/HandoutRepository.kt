package com.pbec.preboardexamchecker.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pbec.preboardexamchecker.data.models.Handout
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HandoutRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    /**
     * Observe the web portal's mobile-facing catalog. Handouts are admin-owned shared resources,
     * so this never filters by the signed-in teacher account. A subject filter is optional.
     */
    fun observeHandouts(subject: String? = null): Flow<List<Handout>> = callbackFlow {
        val collection = firestore.collection(HANDOUTS_COLLECTION)
        val query: Query = subject
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?.let { collection.whereEqualTo("subject", it) }
            ?: collection

        val listener = query
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val handouts = snapshot?.documents.orEmpty()
                    .filter { doc ->
                        val status = doc.getString("status").orEmpty()
                        val itemType = doc.getString("itemType").orEmpty()
                        (status.isBlank() || status.equals("active", ignoreCase = true)) &&
                            (itemType.isBlank() || itemType.equals("handout", ignoreCase = true))
                    }
                    .mapNotNull { it.toHandout() }
                    .sortedWith(compareByDescending<Handout> { it.timestampMillis }.thenBy { it.title.lowercase() })

                trySend(handouts)
            }

        awaitClose { listener.remove() }
    }

    private fun DocumentSnapshot.toHandout(): Handout? {
        val title = getString("title")?.trim().orEmpty()
        val handoutSubject = getString("subject")?.trim().orEmpty()
        if (title.isBlank() || handoutSubject.isBlank()) return null

        return Handout(
            id = getString("handoutId")?.takeIf { it.isNotBlank() } ?: id,
            bankId = getString("bankId").orEmpty(),
            title = title,
            subject = handoutSubject,
            sessionName = firstNonBlankString("sessionName", "session", "sourceSession"),
            handoutType = getString("handoutType").orEmpty(),
            notes = getString("notes").orEmpty(),
            fileName = firstNonBlankString("fileName", "file", "sourceFileName"),
            fileUrl = firstNonBlankString("fileUrl", "downloadUrl"),
            fileSize = getLong("fileSize")
                ?: getDouble("fileSize")?.toLong()
                ?: 0L,
            mimeType = getString("mimeType").orEmpty(),
            createdAt = getTimestamp("createdAt"),
            updatedAt = getTimestamp("updatedAt"),
        )
    }

    private fun DocumentSnapshot.firstNonBlankString(vararg fieldNames: String): String =
        fieldNames.firstNotNullOfOrNull { fieldName ->
            getString(fieldName)?.trim()?.takeIf { it.isNotEmpty() }
        }.orEmpty()

    private companion object {
        const val HANDOUTS_COLLECTION = "handouts"
    }
}

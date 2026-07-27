package com.pbec.preboardexamchecker.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pbec.preboardexamchecker.data.models.Student
import com.pbec.preboardexamchecker.data.models.toStudentCompat
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : IStudentRepository {
    override suspend fun getAllStudents(): List<Student> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return emptyList()
        return try {
            firestore.collection("students")
                .get()
                .await()
                .documents
                .mapNotNull { it.toStudentCompat() }
                .filter {
                    (it.uploadedByUid.isBlank() || it.uploadedByUid == uid) &&
                        it.deletedAt == null &&
                        !it.isArchived
                }  // trashed/archived rosters must not resolve while scanning
        } catch (e: Exception) {
            emptyList()
        }
    }
}

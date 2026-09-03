package com.pbec.preboardexamchecker.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ServerTimestamp

val STUDENT_YEAR_LEVELS = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")

fun canonicalYearLevel(value: String): String {
    val normalized = value.trim().lowercase()
    return when (normalized) {
        "1", "1st", "1st year", "first", "first year" -> "1st Year"
        "2", "2nd", "2nd year", "second", "second year" -> "2nd Year"
        "3", "3rd", "3rd year", "third", "third year" -> "3rd Year"
        "4", "4th", "4th year", "fourth", "fourth year" -> "4th Year"
        else -> value.trim()
    }
}

data class Student(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val studentId: String = "",
    val program: String = "",
    val yearLevel: String = "",
    val block: String = "",
    // Web-created roster rows use "section"; mobile uses "block". Keep both readable.
    val section: String = "",
    // Roster-only fields. block is looked up by scanned Student ID (never on the answer sheet);
    // schoolYear distinguishes roster imports from different academic years.
    val gender: String = "",
    // Optional contact email, used to send the student their result slip directly.
    val email: String = "",
    val schoolYear: String = "",
    val instructor: String = "",
    // Owner uid; the scanner's StudentRepository filters by it, so imports/adds must set it.
    val uploadedByUid: String = "",
    // Import batch: lets the user delete a whole roster at once; importLabel is a friendly group name.
    val importId: Long = 0,
    val importLabel: String = "",
    // Soft-delete (30-day trash), mirroring papers/exams. null = active.
    val deletedAt: Long? = null,
    // Set to the importId when trashed as a whole import; null when deleted individually. Splits the
    // Trash > Rosters tab into "Imports" vs "Individual students".
    val deletedBatch: Long? = null,
    val isArchived: Boolean = false,
    @ServerTimestamp
    val createdAt: Timestamp? = null
)

fun DocumentSnapshot.toStudentCompat(): Student? {
    val student = toObject(Student::class.java) ?: return null
    return student.copy(
        block = student.block.ifBlank { student.section },
        yearLevel = canonicalYearLevel(student.yearLevel),
    )
}

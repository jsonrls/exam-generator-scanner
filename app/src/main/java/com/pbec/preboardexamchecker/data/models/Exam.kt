package com.pbec.preboardexamchecker.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Ignore

@Entity(tableName = "exams")
data class Exam(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examName: String,
    val subject: String,
    val setAQuestionIds: List<Long>,
    val setBQuestionIds: List<Long>, // same questions as set A, reordered
    val createdAt: Long
) {
    @Ignore
    var selectedQuestionBankIds: List<String> = emptyList()

    fun renamed(name: String): Exam = copy(examName = name).also {
        it.selectedQuestionBankIds = selectedQuestionBankIds
    }

    // Backward-compat single-set view for the current UI.
    val questionIds: List<Long>
        get() = if (setAQuestionIds.isNotEmpty()) setAQuestionIds else setBQuestionIds
}

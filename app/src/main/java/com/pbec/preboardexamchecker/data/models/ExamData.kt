package com.pbec.preboardexamchecker.data.models

import com.google.firebase.Timestamp
import java.util.Date

/** Compatibility at the Firestore boundary; the Room and scanner IDs stay numeric. */
internal object ExamData {
    fun storedLong(value: Any?): Long? = when (value) {
        is Long -> value
        is Int -> value.toLong()
        is Number -> value.toDouble().takeIf { it.isFinite() && it % 1.0 == 0.0 && it >= Long.MIN_VALUE.toDouble() && it < Long.MAX_VALUE.toDouble() }?.toLong()
        is String -> value.toLongOrNull()
        else -> null
    }

    fun timestampMillis(value: Any?): Long = when (value) {
        null -> 0L
        is Timestamp -> value.toDate().time
        is Date -> value.time
        else -> storedLong(value) ?: error("Invalid exam creation time.")
    }

    fun legacyId(documentId: String): Long = kotlin.math.abs(documentId.hashCode().toLong())

    fun examId(documentId: String, storedId: Any?): Long =
        if (storedId == null) legacyId(documentId) else storedLong(storedId) ?: error("Invalid exam ID.")

    fun bankIds(value: Any?, subject: String): List<String> {
        if (value == null) return listOf("default_" + subject.lowercase(java.util.Locale.ROOT)
            .replace(Regex("[^a-z0-9]+"), "_").trim('_'))
        require(value is List<*> && value.isNotEmpty() &&
            value.all { it is String && it.isNotBlank() && !it.contains('/') }) {
            "Invalid exam question bank IDs."
        }
        return value.filterIsInstance<String>().distinct()
    }

    fun orderedQuestions(exam: Exam, ids: List<Long>, questions: List<Question>): List<Question> {
        val banks = exam.selectedQuestionBankIds.ifEmpty { bankIds(null, exam.subject) }
        val scoped = questions.filter { it.subject == exam.subject && it.questionBankId in banks }
        return ids.mapIndexed { index, id ->
            val matches = scoped.filter { it.id == id }
            require(matches.size == 1) {
                "Question ${index + 1} is ${if (matches.isEmpty()) "missing" else "ambiguous"} in this exam's question banks."
            }
            matches.single()
        }
    }

    fun answerKey(exam: Exam, ids: List<Long>, questions: List<Question>): Map<Long, String> =
        orderedQuestions(exam, ids, questions).associate { question ->
            val answer = question.correctAnswer?.trim()?.uppercase(java.util.Locale.ROOT)
            require(answer in setOf("A", "B", "C", "D")) {
                "Question ${question.id} has a missing or invalid answer key. Correct it before scanning."
            }
            question.id to requireNotNull(answer)
        }

    fun references(value: Any?): List<Any> {
        if (value == null) return emptyList()
        require(value is List<*>) { "Exam question references must be an array." }
        return value.map { reference ->
            require(reference is String && reference.isNotBlank() || storedLong(reference) != null) {
                "Invalid exam question reference."
            }
            requireNotNull(reference)
        }
    }

    data class QuestionReference(val documentId: String, val bankId: String, val numericId: Long)

    fun resolveIds(references: List<Any>, questions: List<QuestionReference>): List<Long> =
        references.mapIndexed { index, reference ->
            if (reference is Number) return@mapIndexed storedLong(reference)
                ?: error("Invalid numeric question ID at position ${index + 1}.")
            val exact = questions.filter { it.documentId == reference }
            val matches = if (exact.isNotEmpty()) exact else questions.filter { it.numericId == storedLong(reference) }
            require(matches.size == 1) {
                "Question ${index + 1}: ${if (matches.isEmpty()) "missing" else "ambiguous"} reference $reference."
            }
            matches.single().numericId
        }
}

package com.pbec.preboardexamchecker.data.repository

import com.pbec.preboardexamchecker.data.models.Exam
import com.pbec.preboardexamchecker.data.models.ExamData
import com.pbec.preboardexamchecker.data.models.Question
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ExamQuestionResolutionTest {
    private fun exam(banks: List<String> = listOf("legacy-math")) =
        Exam(19, "Original", "Mathematics", listOf(41, 72), listOf(72, 41), 1000).also {
            it.selectedQuestionBankIds = banks
        }

    private fun question(id: Long, bank: String, answer: String) = Question(
        id = id, subject = "Mathematics", fileName = "questions.xlsx", questionNumber = 1,
        questionText = "Question $id", optionA = "A", optionB = "B", optionC = "C", optionD = "D",
        correctAnswer = answer, questionBankId = bank)

    @Test fun renameKeepsIdentityBothOrdersAndSelectedBanks() {
        val original = exam()
        val renamed = original.renamed("Renamed")
        assertEquals("Original", original.examName)
        assertEquals("Renamed", renamed.examName)
        assertEquals(original.id, renamed.id)
        assertEquals(original.createdAt, renamed.createdAt)
        assertEquals(original.setAQuestionIds, renamed.setAQuestionIds)
        assertEquals(original.setBQuestionIds, renamed.setBQuestionIds)
        assertEquals(original.selectedQuestionBankIds, renamed.selectedQuestionBankIds)
        assertEquals(renamed.selectedQuestionBankIds, ExamData.bankIds(renamed.selectedQuestionBankIds, renamed.subject))
    }

    @Test fun viewerAndScannerUseSelectedBankAnswerWhenCanonicalIdsCollide() {
        val exam = exam()
        val pool = listOf(question(41, "default_mathematics", "A"),
            question(72, "default_mathematics", "B"), question(41, "legacy-math", "D"),
            question(72, "legacy-math", "C"))
        assertEquals(listOf("D", "C"), ExamData.orderedQuestions(exam, exam.setAQuestionIds, pool).map { it.correctAnswer })
        assertEquals(listOf("C", "D"), ExamData.orderedQuestions(exam, exam.setBQuestionIds, pool).map { it.correctAnswer })
        assertEquals(mapOf(41L to "D", 72L to "C"), ExamData.answerKey(exam, exam.setAQuestionIds, pool))
        assertEquals(listOf("C", "D"), exam.setBQuestionIds.map { ExamData.answerKey(exam, exam.setBQuestionIds, pool)[it] })
    }

    @Test fun collisionsInsideSelectedBanksFailInsteadOfChoosingFirstOrLast() {
        val exam = exam(listOf("legacy-math", "other-math"))
        val pool = listOf(question(41, "legacy-math", "A"), question(41, "other-math", "D"))
        assertThrows(IllegalArgumentException::class.java) { ExamData.answerKey(exam, listOf(41), pool) }
        assertThrows(IllegalArgumentException::class.java) { ExamData.orderedQuestions(exam, listOf(41), pool) }
    }

    @Test fun missingSelectedQuestionCannotUseCanonicalReplacement() {
        val pool = listOf(question(41, "default_mathematics", "A"), question(72, "legacy-math", "D"))
        assertThrows(IllegalArgumentException::class.java) { ExamData.answerKey(exam(), listOf(41, 72), pool) }
    }

    @Test fun absentBankMetadataUsesCanonicalBankAndInvalidMetadataFails() {
        assertEquals(listOf("default_mathematics"), ExamData.bankIds(null, "Mathematics"))
        assertEquals(listOf("default_professional_ee"), ExamData.bankIds(null, "Professional EE"))
        assertThrows(IllegalArgumentException::class.java) { ExamData.bankIds(listOf("bad/path"), "Mathematics") }
        assertThrows(IllegalArgumentException::class.java) { ExamData.bankIds(emptyList<String>(), "Mathematics") }
    }

    @Test fun readRestoreAndPurgeUseSameNumericTextAndLegacyIdentity() {
        assertEquals(41L, ExamData.examId("doc", 41L))
        assertEquals(41L, ExamData.examId("doc", "41"))
        assertEquals(ExamData.legacyId("legacy-doc"), ExamData.examId("legacy-doc", null))
        assertThrows(IllegalStateException::class.java) { ExamData.examId("doc", "invalid") }
    }
}

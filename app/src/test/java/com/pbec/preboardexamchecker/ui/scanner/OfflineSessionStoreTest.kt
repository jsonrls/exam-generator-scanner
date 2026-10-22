package com.pbec.preboardexamchecker.ui.scanner

import com.pbec.preboardexamchecker.data.models.ExamCluster
import com.pbec.preboardexamchecker.data.models.Student
import com.pbec.preboardexamchecker.data.models.Exam
import com.pbec.preboardexamchecker.data.models.ExamData
import com.pbec.preboardexamchecker.data.models.Question
import com.pbec.preboardexamchecker.ui.scanner.processor.ScanContext
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class OfflineSessionStoreTest {
    @get:Rule val temporary = TemporaryFolder()

    private fun complete(owner: String = "teacher-one"): SavedScanSession {
        val roster = listOf(Student(id = "student-doc", name = "Test Student", studentId = "S001234",
            uploadedByUid = owner, program = "BSEE", block = "A", email = "student@example.test"))
        val cluster = ExamCluster(42, "Preboard 1", OfflineSessionStore.subjects.withIndex()
            .associate { it.value to it.index.toLong() + 1 }, "2026-2027")
        return SavedScanSession(owner, cluster, 1_800_000_000_000, OfflineSessionStore.subjects.map {
            ScanContext(cluster.examIdsBySubject.getValue(it), "$it Exam", it,
                listOf(10, 11), listOf(11, 10), mapOf(10L to "A", 11L to "D"),
                mapOf(10L to "A", 11L to "D"), roster, cluster.id, cluster.name)
        })
    }

    @Test fun completeSessionSurvivesStoreRestartWithoutNetworkDependencies() {
        val folder = temporary.newFolder()
        val session = complete()
        OfflineSessionStore(folder).save(session)
        val restarted = OfflineSessionStore(folder)
        assertEquals(session, restarted.read(session.ownerUid, session.cluster.id))
        assertEquals(listOf(session), restarted.list(session.ownerUid))
    }

    @Test fun scopedAnswerKeysKeepTheSameAnswersAfterOfflineRestartDespiteCollidingCanonicalIds() {
        val session = complete()
        val contexts = session.contexts.map { context ->
            val exam = Exam(context.examId, context.examName, context.subject,
                context.setAQuestionIds, context.setBQuestionIds, session.savedAt).also {
                it.selectedQuestionBankIds = listOf("legacy-bank")
            }
            fun question(id: Long, bank: String, answer: String) = Question(
                id = id, subject = context.subject, fileName = "bank.xlsx", questionNumber = 1,
                questionText = "Question $id", optionA = "A", optionB = "B", optionC = "C", optionD = "D",
                correctAnswer = answer, questionBankId = bank)
            val pool = listOf(question(10, "default_mathematics", "B"), question(11, "default_mathematics", "C"),
                question(10, "legacy-bank", "A"), question(11, "legacy-bank", "D"))
            context.copy(setAKeyMap = ExamData.answerKey(exam, exam.setAQuestionIds, pool),
                setBKeyMap = ExamData.answerKey(exam, exam.setBQuestionIds, pool))
        }
        val folder = temporary.newFolder()
        OfflineSessionStore(folder).save(session.copy(contexts = contexts))
        val reopened = OfflineSessionStore(folder).read(session.ownerUid, session.cluster.id)!!
        reopened.contexts.forEach { context ->
            assertEquals(listOf("A", "D"), context.setAQuestionIds.map(context.setAKeyMap::get))
            assertEquals(listOf("D", "A"), context.setBQuestionIds.map(context.setBKeyMap::get))
        }
    }

    @Test fun ownerCannotReadAnotherOwnersPackage() {
        val store = OfflineSessionStore(temporary.newFolder())
        store.save(complete())
        assertNull(store.read("teacher-two", 42))
        assertTrue(store.list("teacher-two").isEmpty())
    }

    @Test fun partialClusterCannotReplaceCompleteSavedCopy() {
        val store = OfflineSessionStore(temporary.newFolder())
        val complete = complete()
        store.save(complete)
        assertThrows(IllegalArgumentException::class.java) {
            store.save(complete.copy(contexts = complete.contexts.dropLast(1)))
        }
        assertEquals(complete, store.read(complete.ownerUid, 42))
    }

    @Test fun missingAnswerKeyCannotReplaceCompleteSavedCopy() {
        val store = OfflineSessionStore(temporary.newFolder())
        val complete = complete()
        store.save(complete)
        val invalid = complete.copy(contexts = complete.contexts.map {
            it.copy(setBKeyMap = mapOf(10L to "A"))
        })
        assertThrows(IllegalArgumentException::class.java) { store.save(invalid) }
        assertEquals(complete, store.read(complete.ownerUid, 42))
    }

    @Test fun emptyRosterAndDuplicateNumericIdentitiesAreRejected() {
        val context = complete().contexts.first()
        assertThrows(IllegalArgumentException::class.java) {
            OfflineSessionStore.validateContext(context.copy(students = emptyList()))
        }
        assertThrows(IllegalArgumentException::class.java) {
            OfflineSessionStore.validateContext(context.copy(students = context.students +
                context.students.first().copy(id = "second-doc", studentId = "001234")))
        }
    }

    @Test fun unsupportedQuestionCountsAndKeysAreRejected() {
        val context = complete().contexts.first()
        for (invalid in listOf(context.copy(setAQuestionIds = emptyList()),
            context.copy(setAQuestionIds = (1L..101L).toList()),
            context.copy(setBKeyMap = mapOf(10L to "E", 11L to "D")))) {
            assertThrows(IllegalArgumentException::class.java) { OfflineSessionStore.validateContext(invalid) }
        }
    }

    @Test fun corruptedOrOldVersionPackageIsNeverListedAsReady() {
        val folder = temporary.newFolder()
        val store = OfflineSessionStore(folder)
        store.save(complete())
        val file = folder.walkTopDown().first { it.extension == "json" }
        val valid = file.readText()
        file.writeText("{broken")
        assertTrue(store.list("teacher-one").isEmpty())
        assertThrows(Exception::class.java) { store.read("teacher-one", 42) }
        file.writeText(valid.replace("\"version\":1", "\"version\":0"))
        assertTrue(store.list("teacher-one").isEmpty())
    }

    @Test fun foreignRosterAndClusterExamMismatchAreRejected() {
        val complete = complete()
        for (invalid in listOf(
            complete.copy(ownerUid = "teacher-two"),
            complete.copy(contexts = complete.contexts.map { it.copy(examId = 999) }),
        )) assertThrows(IllegalArgumentException::class.java) { OfflineSessionStore.validate(invalid) }
    }
}

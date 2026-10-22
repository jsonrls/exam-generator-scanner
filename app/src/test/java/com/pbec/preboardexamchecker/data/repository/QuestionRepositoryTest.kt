package com.pbec.preboardexamchecker.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class QuestionRepositoryTest {
    @Test
    fun numericAndTextSessionIdsMatchTheSameLegacySession() {
        val sessionId = 1726400000000L

        assertEquals(sessionId, QuestionRepository.parseStoredLong(sessionId))
        assertEquals(sessionId, QuestionRepository.parseStoredLong(sessionId.toString()))
    }

    @Test
    fun namedImportSessionsUseTheExistingFallbackWithoutThrowing() {
        assertNull(QuestionRepository.parseStoredLong("official_mathematics"))
        assertNull(QuestionRepository.parseStoredLong(""))
        assertNull(QuestionRepository.parseStoredLong(null))
        assertNull(QuestionRepository.parseStoredLong(true))
        assertNull(QuestionRepository.parseStoredLong("9223372036854775808"))
    }

    @Test
    fun questionNumbersAcceptFirestoreNumbersAndNumericText() {
        assertEquals(12L, QuestionRepository.parseStoredLong(12L))
        assertEquals(12L, QuestionRepository.parseStoredLong(12.0))
        assertEquals(12L, QuestionRepository.parseStoredLong("12"))
    }
}

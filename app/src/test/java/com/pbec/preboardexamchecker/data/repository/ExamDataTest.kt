package com.pbec.preboardexamchecker.data.repository

import com.google.firebase.Timestamp
import com.pbec.preboardexamchecker.data.models.ExamData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.util.Date

class ExamDataTest {
    @Test fun creationTimeAcceptsFirestoreTimestampAndLegacyEpoch() {
        assertEquals(1234000L, ExamData.timestampMillis(Timestamp(1234L, 0)))
        assertEquals(1234000L, ExamData.timestampMillis(1234000L))
        assertEquals(1234000L, ExamData.timestampMillis("1234000"))
        assertEquals(1234000L, ExamData.timestampMillis(Date(1234000L)))
        assertEquals(0L, ExamData.timestampMillis(null))
    }

    @Test fun stringDocumentReferencesResolveExistingNumericIdsInExactOrder() {
        val questions = listOf(ExamData.QuestionReference("auto-a", "bank", 41L),
            ExamData.QuestionReference("auto-b", "bank", 72L))
        assertEquals(listOf(72L, 41L, 72L), ExamData.resolveIds(listOf("auto-b", "auto-a", "auto-b"), questions))
        assertEquals(listOf(41L, 72L), ExamData.resolveIds(listOf("auto-a", "auto-b"), questions))
        assertEquals(listOf(72L, 41L), ExamData.resolveIds(listOf(72L, 41L), emptyList()))
    }

    @Test fun missingAmbiguousAndMalformedReferencesAreNotDropped() {
        assertThrows(IllegalArgumentException::class.java) { ExamData.resolveIds(listOf("missing"), emptyList()) }
        val duplicate = listOf(ExamData.QuestionReference("same", "bank1", 1L), ExamData.QuestionReference("same", "bank2", 2L))
        assertThrows(IllegalArgumentException::class.java) { ExamData.resolveIds(listOf("same"), duplicate) }
        assertThrows(IllegalArgumentException::class.java) { ExamData.references(listOf(null)) }
        assertNull(ExamData.storedLong(1.5))
        assertNull(ExamData.storedLong(Double.NaN))
        assertNull(ExamData.storedLong(true))
    }

    @Test fun legacyExamIdentityIsStableAndPreservesPriorHashFallback() {
        assertEquals(kotlin.math.abs("legacy-exam".hashCode().toLong()), ExamData.legacyId("legacy-exam"))
        assertEquals(9223372036854775807L, ExamData.storedLong("9223372036854775807"))
    }
}

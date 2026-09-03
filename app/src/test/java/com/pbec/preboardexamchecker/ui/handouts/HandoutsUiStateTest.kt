package com.pbec.preboardexamchecker.ui.handouts

import com.pbec.preboardexamchecker.data.models.Handout
import org.junit.Assert.assertEquals
import org.junit.Test

class HandoutsUiStateTest {
    @Test
    fun `subject filter keeps only matching shared handouts`() {
        val math = handout(id = "math", subject = "Mathematics")
        val esas = handout(id = "esas", subject = "ESAS")

        val visible = HandoutsUiState(
            allHandouts = listOf(math, esas),
            selectedSubject = "ESAS",
        ).visibleHandouts

        assertEquals(listOf(esas), visible)
    }

    @Test
    fun `search matches a title without regard to case`() {
        val practiceSet = handout(id = "practice", subject = "Professional EE")
        val reference = handout(id = "reference", subject = "Professional EE")

        val visible = HandoutsUiState(
            allHandouts = listOf(practiceSet, reference),
            query = "PRACTICE",
        ).visibleHandouts

        assertEquals(listOf(practiceSet), visible)
    }

    private fun handout(
        id: String,
        subject: String,
    ) = Handout(
        id = id,
        bankId = "bank-$id",
        title = "Review material $id",
        subject = subject,
        sessionName = "General Handouts",
        handoutType = "Study Guide",
        notes = "",
        fileName = "$id.pdf",
        fileUrl = "https://example.com/$id.pdf",
        fileSize = 1024L,
        mimeType = "application/pdf",
        createdAt = null,
        updatedAt = null,
    )
}

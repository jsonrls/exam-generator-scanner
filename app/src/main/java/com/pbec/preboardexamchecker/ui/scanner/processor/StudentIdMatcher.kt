package com.pbec.preboardexamchecker.ui.scanner.processor

import com.pbec.preboardexamchecker.data.models.Student

/** Matches the six printed digit columns without changing the roster's stored identity. */
object StudentIdMatcher {
    sealed class Match {
        data class Found(val student: Student) : Match()
        data object Invalid : Match()
        data object NotFound : Match()
        data object Ambiguous : Match()
    }

    fun resolve(scannedId: String, students: List<Student>): Match {
        if (!isSixDigits(scannedId)) return Match.Invalid

        var match: Student? = null
        for (student in students) {
            if (scanDigits(student.studentId) != scannedId) continue
            // Keep roster rows intact: even identical IDs in separate rows are ambiguous.
            if (match != null) return Match.Ambiguous
            match = student
        }
        return match?.let { Match.Found(it) } ?: Match.NotFound
    }

    /** Query both the stored identity and numeric IDs saved before prefix matching existed. */
    fun duplicateQueryIds(studentId: String, students: List<Student>): Set<String> {
        val digits = scanDigits(studentId) ?: return emptySet()
        return when (val match = resolve(digits, students)) {
            is Match.Found -> setOf(match.student.studentId, digits)
            Match.NotFound -> setOf(studentId)
            Match.Invalid, Match.Ambiguous -> emptySet()
        }
    }

    /** Surrounding whitespace and an optional S prefix are ignored; zeroes remain significant. */
    fun scanDigits(studentId: String): String? {
        val trimmedId = studentId.trim()
        val digits = if (trimmedId.startsWith("S", ignoreCase = true)) {
            trimmedId.substring(1)
        } else {
            trimmedId
        }
        return digits.takeIf(::isSixDigits)
    }

    private fun isSixDigits(value: String): Boolean =
        value.length == 6 && value.all { it in '0'..'9' }
}

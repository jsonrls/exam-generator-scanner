package com.pbec.preboardexamchecker.ui.scanner.processor

import com.pbec.preboardexamchecker.data.models.Student
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StudentIdMatcherTest {
    @Test
    fun `numeric scan resolves optional prefix and preserves stored identity`() {
        for (storedId in listOf("119046", "S119046", "s119046", " S119046 ", "\t119046\n")) {
            val student = Student(id = "roster-row", studentId = storedId)
            assertEquals(StudentIdMatcher.Match.Found(student), StudentIdMatcher.resolve("119046", listOf(student)))
        }
    }

    @Test
    fun `leading zeroes are significant and never padded`() {
        val student = Student(studentId = "S001234")
        assertEquals(StudentIdMatcher.Match.Found(student), StudentIdMatcher.resolve("001234", listOf(student)))
        assertEquals(StudentIdMatcher.Match.Invalid, StudentIdMatcher.resolve("1234", listOf(student)))
        assertEquals(StudentIdMatcher.Match.NotFound, StudentIdMatcher.resolve("001234", listOf(Student(studentId = "S1234"))))
    }

    @Test
    fun `incomplete and malformed scans never resolve`() {
        for (scan in listOf("", "11904?", "??????", "11904", "1190467", "S119046", " 119046", "119046 ", "１１９０４６")) {
            assertEquals(scan, StudentIdMatcher.Match.Invalid, StudentIdMatcher.resolve(scan, listOf(Student(studentId = scan))))
        }
    }

    @Test
    fun `unsupported prefixes and formatting never alias`() {
        for (storedId in listOf("A119046", "SS119046", "S-119046", "S 119046", "119046.0")) {
            assertNull(storedId, StudentIdMatcher.scanDigits(storedId))
            assertEquals(StudentIdMatcher.Match.NotFound, StudentIdMatcher.resolve("119046", listOf(Student(studentId = storedId))))
        }
    }

    @Test
    fun `numeric and prefixed collisions are rejected regardless of order`() {
        val students = listOf(Student(id = "one", studentId = "119046"), Student(id = "two", studentId = "S119046"))
        assertEquals(StudentIdMatcher.Match.Ambiguous, StudentIdMatcher.resolve("119046", students))
        assertEquals(StudentIdMatcher.Match.Ambiguous, StudentIdMatcher.resolve("119046", students.reversed()))
    }

    @Test
    fun `duplicate exact roster IDs remain ambiguous`() {
        val students = listOf(Student(id = "one", studentId = "S119046"), Student(id = "two", studentId = "S119046"))
        assertEquals(StudentIdMatcher.Match.Ambiguous, StudentIdMatcher.resolve("119046", students))
    }

    @Test
    fun `case variant aliases remain ambiguous`() {
        val students = listOf(Student(studentId = "S119046"), Student(studentId = "s119046"))
        assertEquals(StudentIdMatcher.Match.Ambiguous, StudentIdMatcher.resolve("119046", students))
    }

    @Test
    fun `unknown or empty roster returns not found`() {
        assertEquals(StudentIdMatcher.Match.NotFound, StudentIdMatcher.resolve("119046", emptyList()))
        assertEquals(StudentIdMatcher.Match.NotFound, StudentIdMatcher.resolve("119046", listOf(Student(studentId = "S119047"))))
    }

    @Test
    fun `duplicate queries cover canonical stored ID and legacy numeric alias`() {
        for (storedId in listOf("S001234", "s001234", " S001234 ", "001234")) {
            val students = listOf(Student(studentId = storedId))
            for (input in listOf(storedId, "001234")) {
                assertEquals(setOf(storedId, "001234"), StudentIdMatcher.duplicateQueryIds(input, students))
            }
        }
    }

    @Test
    fun `unresolved duplicate queries keep only the exact input identity`() {
        for (input in listOf("001234", "S001234", "s001234", " S001234 ")) {
            assertEquals(setOf(input), StudentIdMatcher.duplicateQueryIds(input, emptyList()))
        }
    }

    @Test
    fun `ambiguous duplicate queries never choose an alias`() {
        val students = listOf(Student(studentId = "S001234"), Student(studentId = " 001234 "))
        for (input in listOf("001234", "S001234")) {
            assertEquals(emptySet<String>(), StudentIdMatcher.duplicateQueryIds(input, students))
        }
    }

    @Test
    fun `incomplete or malformed duplicate queries are rejected`() {
        for (input in listOf("", "00123?", "00123", "S00123?", "A001234", "S 001234")) {
            assertEquals(emptySet<String>(), StudentIdMatcher.duplicateQueryIds(input, emptyList()))
        }
    }
}

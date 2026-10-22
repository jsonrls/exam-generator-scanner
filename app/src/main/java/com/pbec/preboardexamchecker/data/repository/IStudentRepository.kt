package com.pbec.preboardexamchecker.data.repository

import com.pbec.preboardexamchecker.data.models.Student

interface IStudentRepository {
    suspend fun getAllStudents(): List<Student>
    suspend fun getStudentsForOfflinePreparation(): List<Student> =
        throw UnsupportedOperationException("Server preparation is unavailable")
}

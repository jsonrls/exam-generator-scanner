package com.pbec.preboardexamchecker.data.models

import com.google.firebase.Timestamp

/** A shared handout uploaded from the web admin portal and mirrored to `handouts/{id}`. */
data class Handout(
    val id: String,
    val bankId: String,
    val title: String,
    val subject: String,
    val sessionName: String,
    val handoutType: String,
    val notes: String,
    val fileName: String,
    val fileUrl: String,
    val fileSize: Long,
    val mimeType: String,
    val createdAt: Timestamp?,
    val updatedAt: Timestamp?,
) {
    val timestampMillis: Long
        get() = (updatedAt ?: createdAt)?.toDate()?.time ?: 0L
}

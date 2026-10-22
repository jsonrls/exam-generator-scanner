package com.pbec.preboardexamchecker.ui.scanner

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.pbec.preboardexamchecker.data.models.ExamCluster
import com.pbec.preboardexamchecker.data.models.Student
import com.pbec.preboardexamchecker.ui.scanner.processor.ScanContext
import com.pbec.preboardexamchecker.ui.scanner.processor.StudentIdMatcher
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest

data class SavedScanSession(
    val ownerUid: String,
    val cluster: ExamCluster,
    val savedAt: Long,
    val contexts: List<ScanContext>,
)

/** A versioned, complete snapshot; never depends on Firestore's opportunistic cache. */
class OfflineSessionStore(private val directory: File) {
    fun list(ownerUid: String): List<SavedScanSession> =
        ownerDirectory(ownerUid).listFiles().orEmpty().filter { it.extension == "json" }
            .mapNotNull { file -> runCatching { decode(file.readText(), ownerUid) }.getOrNull() }
            .sortedByDescending { it.savedAt }

    fun read(ownerUid: String, clusterId: Long): SavedScanSession? {
        val file = File(ownerDirectory(ownerUid), "$clusterId.json")
        if (!file.exists()) return null
        return try { decode(file.readText(), ownerUid) } catch (e: Exception) {
            throw IllegalStateException("This saved session is unreadable or out of date. Connect and prepare it again.", e)
        }
    }

    @Synchronized
    fun save(session: SavedScanSession) {
        validate(session)
        val folder = ownerDirectory(session.ownerUid)
        check(folder.isDirectory || folder.mkdirs()) { "Cannot save this session on the device." }
        val target = File(folder, "${session.cluster.id}.json")
        val temporary = File.createTempFile("session-", ".tmp", folder)
        try {
            FileOutputStream(temporary).use { stream ->
                stream.write(encode(session).toByteArray(Charsets.UTF_8))
                stream.fd.sync()
            }
            Files.move(temporary.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING)
        } finally {
            temporary.delete()
        }
    }

    fun remove(ownerUid: String, clusterId: Long) {
        File(ownerDirectory(ownerUid), "$clusterId.json").delete()
    }

    private fun ownerDirectory(ownerUid: String): File {
        require(ownerUid.isNotBlank()) { "Sign in to access saved sessions." }
        val key = MessageDigest.getInstance("SHA-256").digest(ownerUid.toByteArray())
            .joinToString("") { "%02x".format(it) }
        return File(directory, key)
    }

    companion object {
        val subjects = listOf("Mathematics", "ESAS", "Professional EE")

        fun validateContext(context: ScanContext) {
            require(context.subject in subjects) { "Unknown subject in saved session." }
            require(context.examId != 0L && context.examName.isNotBlank()) { "The exam is incomplete." }
            for ((ids, keys) in listOf(context.setAQuestionIds to context.setAKeyMap,
                context.setBQuestionIds to context.setBKeyMap)) {
                require(ids.size in 1..100 && ids.distinct().size == ids.size) {
                    "${context.subject} needs between 1 and 100 unique questions in each set."
                }
                require(ids.all { keys[it]?.trim()?.uppercase() in setOf("A", "B", "C", "D") }) {
                    "${context.subject} has missing or invalid answer keys. Correct them before scanning."
                }
            }
            require(context.students.isNotEmpty() && context.students.all {
                it.name.isNotBlank() && StudentIdMatcher.scanDigits(it.studentId) != null &&
                    it.deletedAt == null && !it.isArchived
            }) { "Import a usable roster with six-digit student IDs before saving this session." }
            require(context.students.map { StudentIdMatcher.scanDigits(it.studentId) }.distinct().size ==
                context.students.size) { "Resolve duplicate student IDs in the roster before saving this session." }
        }

        fun validate(session: SavedScanSession) {
            require(session.ownerUid.isNotBlank() && session.savedAt > 0 && session.cluster.id != 0L &&
                session.cluster.name.isNotBlank()) { "Saved session is incomplete. Prepare it again." }
            require(session.cluster.examIdsBySubject.keys == subjects.toSet() &&
                session.contexts.map { it.subject }.toSet() == subjects.toSet() && session.contexts.size == 3) {
                "The cluster needs Mathematics, ESAS, and Professional EE before it can be saved."
            }
            session.contexts.forEach { context ->
                validateContext(context)
                require(context.examId == session.cluster.examIdsBySubject[context.subject] &&
                    context.clusterId == session.cluster.id && context.clusterName == session.cluster.name) {
                    "Saved exam does not match its cluster. Prepare it again."
                }
                require(context.students == session.contexts.first().students &&
                    context.students.all { it.uploadedByUid.isBlank() || it.uploadedByUid == session.ownerUid }) {
                    "Saved roster does not belong to this session. Prepare it again."
                }
            }
        }

        private fun encode(session: SavedScanSession): String = JsonObject().apply {
            addProperty("version", 1)
            addProperty("ownerUid", session.ownerUid)
            addProperty("savedAt", session.savedAt)
            addProperty("clusterId", session.cluster.id)
            addProperty("clusterName", session.cluster.name)
            addProperty("schoolYear", session.cluster.schoolYear)
            add("contexts", JsonArray().apply { session.contexts.forEach { ctx ->
                add(JsonObject().apply {
                    addProperty("subject", ctx.subject)
                    addProperty("examId", ctx.examId)
                    addProperty("examName", ctx.examName)
                    add("setA", encodeSet(ctx.setAQuestionIds, ctx.setAKeyMap))
                    add("setB", encodeSet(ctx.setBQuestionIds, ctx.setBKeyMap))
                })
            } })
            add("students", JsonArray().apply { session.contexts.first().students.forEach { student ->
                add(JsonObject().apply {
                    mapOf("id" to student.id, "name" to student.name, "studentId" to student.studentId,
                        "program" to student.program, "yearLevel" to student.yearLevel, "block" to student.block,
                        "section" to student.section, "gender" to student.gender, "email" to student.email,
                        "schoolYear" to student.schoolYear, "instructor" to student.instructor,
                        "uploadedByUid" to student.uploadedByUid, "importLabel" to student.importLabel)
                        .forEach { (key, value) -> addProperty(key, value) }
                    addProperty("importId", student.importId)
                })
            } })
        }.toString()

        private fun encodeSet(ids: List<Long>, keys: Map<Long, String?>) = JsonArray().apply {
            ids.forEach { id -> add(JsonObject().apply {
                addProperty("id", id); addProperty("answer", keys.getValue(id)?.trim()?.uppercase())
            }) }
        }

        private fun decode(json: String, ownerUid: String): SavedScanSession {
            val root = JsonParser.parseString(json).asJsonObject
            require(root["version"].asInt == 1 && root["ownerUid"].asString == ownerUid) {
                "Saved session is incompatible. Prepare it again."
            }
            val clusterId = root["clusterId"].asLong
            val clusterName = root["clusterName"].asString
            val students = root.getAsJsonArray("students").map { element ->
                val student = element.asJsonObject
                fun text(key: String) = student[key]?.takeUnless { it.isJsonNull }?.asString.orEmpty()
                Student(id = text("id"), name = text("name"), studentId = text("studentId"),
                    program = text("program"), yearLevel = text("yearLevel"), block = text("block"),
                    section = text("section"), gender = text("gender"), email = text("email"),
                    schoolYear = text("schoolYear"), instructor = text("instructor"),
                    uploadedByUid = text("uploadedByUid"), importLabel = text("importLabel"),
                    importId = student["importId"].asLong)
            }
            val contexts = root.getAsJsonArray("contexts").map { element ->
                val ctx = element.asJsonObject
                fun entries(key: String) = ctx.getAsJsonArray(key).map {
                    it.asJsonObject["id"].asLong to it.asJsonObject["answer"].asString
                }
                val a = entries("setA"); val b = entries("setB")
                ScanContext(ctx["examId"].asLong, ctx["examName"].asString, ctx["subject"].asString,
                    a.map { it.first }, b.map { it.first }, a.toMap(), b.toMap(), students,
                    clusterId, clusterName)
            }
            return SavedScanSession(ownerUid, ExamCluster(clusterId, clusterName,
                contexts.associate { it.subject to it.examId },
                root["schoolYear"]?.takeUnless { it.isJsonNull }?.asString), root["savedAt"].asLong, contexts)
                .also(::validate)
        }
    }
}

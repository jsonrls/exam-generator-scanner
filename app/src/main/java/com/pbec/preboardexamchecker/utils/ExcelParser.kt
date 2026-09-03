package com.pbec.preboardexamchecker.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.pbec.preboardexamchecker.data.models.Question
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.FormulaEvaluator
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFRichTextString
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.util.Locale
import javax.inject.Inject

class ExcelParser @Inject constructor() {

    companion object {
        private val HEADERS = listOf(
            "Question Number", "Question Text", "Option A", "Option B",
            "Option C", "Option D", "Correct Answer", "Category", "Topic"
        )

        fun getFileName(context: Context, uri: Uri): String {
            var result: String? = null
            if (uri.scheme == "content") {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (displayNameIndex != -1) {
                            result = it.getString(displayNameIndex)
                        }
                    }
                }
            }
            if (result == null) {
                val path = uri.path
                if (path != null) {
                    val cut = path.lastIndexOf('/')
                    result = if (cut != -1) path.substring(cut + 1) else path
                }
            }
            return result ?: "unknown_file"
        }
    }

    fun readQuestionsFromExcel(context: Context, uri: Uri, selectedSubject: String, originalFileName: String): List<Question> {
        val questions = mutableListOf<Question>()
        val fileNameWithoutExtension = originalFileName.substringBeforeLast(".")

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            XSSFWorkbook(inputStream).use { workbook ->
            val dataFormatter = DataFormatter()
            val formulaEvaluator = workbook.creationHelper.createFormulaEvaluator()

            for (sheetIndex in 0 until workbook.numberOfSheets) {
                val sheet = workbook.getSheetAt(sheetIndex)
                val sheetName = sheet.sheetName.trim()
                
                if (isSheetForDifferentSubject(sheetName, selectedSubject)) {
                    continue
                }

                val headerRow = sheet.getRow(0) ?: continue
                val currentHeaders = mutableMapOf<String, Int>()
                for (cellIndex in 0 until headerRow.lastCellNum.toInt()) {
                    val cell = headerRow.getCell(cellIndex) ?: continue
                    val cellValue = formattedCellValue(cell, dataFormatter, formulaEvaluator)
                        .trim()
                        .lowercase(Locale.ROOT)
                    if (cellValue.isNotEmpty()) {
                        currentHeaders[cellValue] = cellIndex
                    }
                }
                
                val colMapping = findColumnIndices(currentHeaders)
                
                if (isHeaderValid(colMapping)) {
                    for (i in 1..sheet.lastRowNum) {
                        val row = sheet.getRow(i) ?: continue
                        val qTextIdx = colMapping["question text"]!!
                        val questionText = formattedCellText(row.getCell(qTextIdx), dataFormatter, formulaEvaluator).trim()
                        if (questionText.isBlank()) continue

                        val questionNumber = try {
                            if (colMapping.containsKey("number")) {
                                val cell = row.getCell(colMapping["number"]!!)
                                formattedCellValue(cell, dataFormatter, formulaEvaluator).toDouble().toInt()
                            } else {
                                i
                            }
                        } catch (_: Exception) { i }

                        var rawTopic = if (colMapping.containsKey("topic")) {
                            formattedCellValue(row.getCell(colMapping["topic"]!!), dataFormatter, formulaEvaluator).trim()
                        } else {
                            ""
                        }

                        if (rawTopic.isBlank()) {
                            // Fallback for older templates where topic is represented by sheet name.
                            rawTopic = sheetName
                        }

                        var rawCategory = if (colMapping.containsKey("category")) {
                            formattedCellValue(row.getCell(colMapping["category"]!!), dataFormatter, formulaEvaluator).trim()
                        } else {
                            null
                        }

                        // Recover from swapped CSV/Excel columns where topic contains category labels.
                        if (rawTopic.equals("Computation", true) || rawTopic.equals("Objective", true)) {
                            if (rawCategory.isNullOrBlank()) rawCategory = rawTopic
                            rawTopic = sheetName
                        }

                        questions.add(
                            Question(
                                subject = selectedSubject,
                                fileName = fileNameWithoutExtension,
                                category = rawCategory,
                                topic = rawTopic,
                                questionNumber = questionNumber,
                                questionText = questionText,
                                optionA = formattedCellText(row.getCell(colMapping["option a"]!!), dataFormatter, formulaEvaluator).trim(),
                                optionB = formattedCellText(row.getCell(colMapping["option b"]!!), dataFormatter, formulaEvaluator).trim(),
                                optionC = formattedCellText(row.getCell(colMapping["option c"]!!), dataFormatter, formulaEvaluator).trim(),
                                optionD = formattedCellText(row.getCell(colMapping["option d"]!!), dataFormatter, formulaEvaluator).trim(),
                                correctAnswer = formattedCellValue(
                                    row.getCell(colMapping["correct answer"]!!),
                                    dataFormatter,
                                    formulaEvaluator,
                                ).trim().uppercase(Locale.ROOT)
                            )
                        )
                    }
                }
            }
            }
        } ?: throw IllegalArgumentException("Could not open input stream")

        return questions
    }

    private fun isSheetForDifferentSubject(sheetName: String, selectedSubject: String): Boolean {
        val lowerSheet = sheetName.lowercase(Locale.ROOT)
        val subjectsMap = mapOf(
            "Mathematics" to listOf("math"),
            "ESAS" to listOf("esas"),
            "Professional EE" to listOf("ee", "profee", "professional")
        )
        val otherSubjects = subjectsMap.filterKeys { it != selectedSubject }
        for ((name, identifiers) in otherSubjects) {
            if (lowerSheet.contains(name.lowercase(Locale.ROOT))) {
                if (!lowerSheet.contains(selectedSubject.lowercase(Locale.ROOT))) return true
            }
            if (identifiers.any { id -> 
                if (id.length <= 2) {
                    val regex = Regex("\\b${Regex.escape(id)}\\b")
                    regex.containsMatchIn(lowerSheet)
                } else {
                    lowerSheet.contains(id) 
                }
            }) {
                val selectedIdentifiers = subjectsMap[selectedSubject] ?: emptyList()
                if (selectedIdentifiers.none { lowerSheet.contains(it) }) return true
            }
        }
        return false
    }

    internal fun formattedCellText(
        cell: Cell?,
        dataFormatter: DataFormatter,
        formulaEvaluator: FormulaEvaluator? = null,
    ): String {
        if (cell == null) return ""
        val formatted = formattedCellValue(cell, dataFormatter, formulaEvaluator)
        if (cell.cellType != CellType.STRING) return formatted

        val richText = cell.richStringCellValue as? XSSFRichTextString ?: return formatted
        if (!richText.hasFormatting()) return formatted

        val plainText = richText.string
        if (plainText.isEmpty()) return formatted

        val output = StringBuilder()
        var index = 0
        while (index < plainText.length) {
            val typeOffset = richText.getFontAtIndex(index)?.typeOffset ?: Font.SS_NONE
            val start = index
            while (
                index < plainText.length &&
                (richText.getFontAtIndex(index)?.typeOffset ?: Font.SS_NONE) == typeOffset
            ) {
                index++
            }

            val segment = plainText.substring(start, index)
            output.append(
                when (typeOffset) {
                    Font.SS_SUPER -> toSuperscriptText(segment)
                    Font.SS_SUB -> toSubscriptText(segment)
                    else -> segment
                }
            )
        }

        return output.toString().ifBlank { formatted }
    }

    private fun formattedCellValue(
        cell: Cell?,
        dataFormatter: DataFormatter,
        formulaEvaluator: FormulaEvaluator?,
    ): String {
        if (cell == null) return ""
        return runCatching {
            if (formulaEvaluator == null) dataFormatter.formatCellValue(cell)
            else dataFormatter.formatCellValue(cell, formulaEvaluator)
        }.getOrElse {
            // Some imported workbooks use functions Apache POI cannot evaluate. Preserve the
            // cached/displayed value (or formula text) instead of dropping the cell entirely.
            dataFormatter.formatCellValue(cell)
        }
    }

    private fun toSuperscriptText(text: String): String {
        val converted = text.map { ch ->
            when (ch) {
                '0' -> '⁰'
                '1' -> '¹'
                '2' -> '²'
                '3' -> '³'
                '4' -> '⁴'
                '5' -> '⁵'
                '6' -> '⁶'
                '7' -> '⁷'
                '8' -> '⁸'
                '9' -> '⁹'
                '+' -> '⁺'
                '-' -> '⁻'
                '=' -> '⁼'
                '(' -> '⁽'
                ')' -> '⁾'
                'n' -> 'ⁿ'
                'i' -> 'ⁱ'
                else -> null
            }
        }
        return if (converted.all { it != null }) {
            converted.joinToString("")
        } else {
            "^{$text}"
        }
    }

    private fun toSubscriptText(text: String): String {
        val converted = text.map { ch ->
            when (ch) {
                '0' -> '₀'
                '1' -> '₁'
                '2' -> '₂'
                '3' -> '₃'
                '4' -> '₄'
                '5' -> '₅'
                '6' -> '₆'
                '7' -> '₇'
                '8' -> '₈'
                '9' -> '₉'
                '+' -> '₊'
                '-' -> '₋'
                '=' -> '₌'
                '(' -> '₍'
                ')' -> '₎'
                'a' -> 'ₐ'
                'e' -> 'ₑ'
                'h' -> 'ₕ'
                'i' -> 'ᵢ'
                'j' -> 'ⱼ'
                'k' -> 'ₖ'
                'l' -> 'ₗ'
                'm' -> 'ₘ'
                'n' -> 'ₙ'
                'o' -> 'ₒ'
                'p' -> 'ₚ'
                'r' -> 'ᵣ'
                's' -> 'ₛ'
                't' -> 'ₜ'
                'u' -> 'ᵤ'
                'v' -> 'ᵥ'
                'x' -> 'ₓ'
                else -> null
            }
        }
        return if (converted.all { it != null }) {
            converted.joinToString("")
        } else {
            "_{$text}"
        }
    }

    private fun findColumnIndices(headers: Map<String, Int>): Map<String, Int> {
        val mapping = mutableMapOf<String, Int>()
        val searchPatterns = mapOf(
            "question text" to listOf("question text", "question", "q text", "item"),
            "option a" to listOf("option a", "a", "choice a", "opt a"),
            "option b" to listOf("option b", "b", "choice b", "opt b"),
            "option c" to listOf("option c", "c", "choice c", "opt c"),
            "option d" to listOf("option d", "d", "choice d", "opt d"),
            "correct answer" to listOf("correct answer", "answer", "key", "correct", "ans"),
            "number" to listOf("number", "no", "q#", "#", "id"),
            "category" to listOf("category", "type", "set", "description"), // Added 'description' here
            "topic" to listOf("topic", "subject matter", "subject") // Removed 'description' from here
        )
        searchPatterns.forEach { (key, patterns) ->
            headers.forEach { (header, index) ->
                if (patterns.any { it == header }) mapping[key] = index
            }
        }
        searchPatterns.forEach { (key, patterns) ->
            if (!mapping.containsKey(key)) {
                headers.forEach { (header, index) ->
                    if (key.startsWith("option")) {
                        if (header.length <= 2 && patterns.any { header == it }) {
                            mapping[key] = index
                        } else if (patterns.any { header.contains(it) && !header.contains("question") && !header.contains("answer") }) {
                            mapping[key] = index
                        }
                    } else {
                        if (patterns.any { header.contains(it) }) mapping[key] = index
                    }
                }
            }
        }
        return mapping
    }

    private fun isHeaderValid(mapping: Map<String, Int>): Boolean {
        val required = listOf("question text", "option a", "option b", "option c", "option d", "correct answer")
        return required.all { mapping.containsKey(it) }
    }

    fun readQuestionsFromCsv(context: Context, uri: Uri, selectedSubject: String, originalFileName: String): List<Question> {
        // ... (keeping CSV logic consistent with Excel fixes)
        val questions = mutableListOf<Question>()
        val fileNameWithoutExtension = originalFileName.substringBeforeLast(".")
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                val headerLine = reader.readLine() ?: throw IllegalArgumentException("CSV file is empty")
                val headersList = parseCsvLine(headerLine).map { it.trim().lowercase(Locale.ROOT) }
                val headersMap = headersList.withIndex().associate { it.value to it.index }
                val colMapping = findColumnIndices(headersMap)
                if (!isHeaderValid(colMapping)) throw IllegalArgumentException("CSV missing required columns.")
                var line: String?
                var rowNum = 1
                while (reader.readLine().also { line = it } != null) {
                    val values = parseCsvLine(line!!)
                    if (values.all { it.isBlank() }) continue
                    val questionText = values.getOrNull(colMapping["question text"]!!) ?: ""
                    if (questionText.isBlank()) continue
                    
                    var rawTopic = if (colMapping.containsKey("topic")) values.getOrNull(colMapping["topic"]!!)?.trim() else ""
                    var rawCategory = if (colMapping.containsKey("category")) values.getOrNull(colMapping["category"]!!)?.trim() else null

                    if (rawTopic.isNullOrBlank()) {
                        rawTopic = fileNameWithoutExtension
                    }
                    
                    if (rawTopic?.equals("Computation", true) == true || rawTopic?.equals("Objective", true) == true) {
                        if (rawCategory == null) rawCategory = rawTopic
                        rawTopic = fileNameWithoutExtension
                    }

                    questions.add(Question(
                        subject = selectedSubject, fileName = fileNameWithoutExtension,
                        category = rawCategory, topic = rawTopic,
                        questionNumber = values.getOrNull(colMapping["number"] ?: -1)?.toDoubleOrNull()?.toInt() ?: rowNum++,
                        questionText = questionText,
                        optionA = values.getOrNull(colMapping["option a"]!!) ?: "",
                        optionB = values.getOrNull(colMapping["option b"]!!) ?: "",
                        optionC = values.getOrNull(colMapping["option c"]!!) ?: "",
                        optionD = values.getOrNull(colMapping["option d"]!!) ?: "",
                        correctAnswer = values.getOrNull(colMapping["correct answer"]!!)?.trim()?.uppercase(Locale.ROOT)
                    ))
                }
            }
        }
        return questions
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var curVal = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val ch = line[i]
            if (inQuotes) {
                if (ch == '\"') {
                    if (i + 1 < line.length && line[i + 1] == '\"') { curVal.append('\"'); i++ } else { inQuotes = false }
                } else { curVal.append(ch) }
            } else {
                when (ch) {
                    '\"' -> { inQuotes = true }
                    ',' -> { result.add(curVal.toString().trim()); curVal = StringBuilder() }
                    else -> { curVal.append(ch) }
                }
            }
            i++
        }
        result.add(curVal.toString().trim())
        return result
    }

    fun writeExcelTemplate(outputStream: OutputStream) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Questions Template")
        val headerRow = sheet.createRow(0)
        HEADERS.forEachIndexed { index, header -> headerRow.createCell(index).setCellValue(header) }
        workbook.write(outputStream)
    }

    fun suggestSubjectFromFileName(fileName: String): String? {
        val lower = fileName.lowercase(Locale.ROOT)
        return when {
            lower.contains("math") -> "Mathematics"
            lower.contains("esas") -> "ESAS"
            lower.contains("ee") || lower.contains("profee") || lower.contains("professional") -> "Professional EE"
            else -> null
        }
    }
}

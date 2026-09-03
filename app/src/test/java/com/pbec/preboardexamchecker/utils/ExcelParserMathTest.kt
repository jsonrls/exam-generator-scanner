package com.pbec.preboardexamchecker.utils

import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.xssf.usermodel.XSSFRichTextString
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.Assert.assertEquals
import org.junit.Test

class ExcelParserMathTest {

    @Test
    fun richTextSuperscriptIsPreservedForMathRendering() {
        XSSFWorkbook().use { workbook ->
            val cell = workbook.createSheet("Questions").createRow(0).createCell(0)
            val richText = XSSFRichTextString("x2")
            val superscript = workbook.createFont().apply { typeOffset = Font.SS_SUPER }
            richText.applyFont(1, 2, superscript)
            cell.setCellValue(richText)

            val actual = ExcelParser().formattedCellText(
                cell,
                DataFormatter(),
                workbook.creationHelper.createFormulaEvaluator(),
            )

            assertEquals("x²", actual)
        }
    }

    @Test
    fun nativeExcelFormulaCellsUseTheirCalculatedDisplayValue() {
        XSSFWorkbook().use { workbook ->
            val cell = workbook.createSheet("Questions").createRow(0).createCell(0)
            cell.cellFormula = "1+2"

            val actual = ExcelParser().formattedCellText(
                cell,
                DataFormatter(),
                workbook.creationHelper.createFormulaEvaluator(),
            )

            assertEquals("3", actual)
        }
    }
}

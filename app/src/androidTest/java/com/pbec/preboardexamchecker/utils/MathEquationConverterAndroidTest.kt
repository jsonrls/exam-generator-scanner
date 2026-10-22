package com.pbec.preboardexamchecker.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/** Runs against Android's regex engine, whose syntax differs from desktop Java. */
@RunWith(AndroidJUnit4::class)
class MathEquationConverterAndroidTest {
    @Test
    fun rootNotationConvertsOnAndroid() {
        for (source in listOf("sqrt{7}", "sq. rt. {7}", "square root of {7}", "sqrt(7)", "√{7}", "\\sqrt{7}")) {
            assertEquals(source, "\\sqrt{7}", MathEquationConverter.convertEquationTextToLaTeX(source))
        }
    }

    @Test
    fun pdfMathSegmentsConvertWithoutRegexErrors() {
        val segments = MathEquationConverter.renderSegments("Find x if x² + 1 = 5.")
        assertEquals("x^{2} + 1 = 5", segments.single { it.isMath }.latex)
        assertEquals("\\frac{\\sqrt{7}}{4}", MathEquationConverter.convertEquationTextToLaTeX("√{7} / 4"))
    }
}

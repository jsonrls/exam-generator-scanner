package com.pbec.preboardexamchecker.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MathEquationConverterTest {

    @Test
    fun workbookFractionsStayTogetherInsideMixedProse() {
        val source = "Given: 1 / log₂ a + 1 / log₃ a + 1 / log₄ a = 1. Find a."

        val segments = MathEquationConverter.renderSegments(source)

        assertEquals(source, segments.joinToString("") { it.source })
        assertEquals(1, segments.count { it.isMath })
        assertEquals(
            "\\frac{1}{\\log_{2} a} + \\frac{1}{\\log_{3} a} + \\frac{1}{\\log_{4} a} = 1",
            segments.single { it.isMath }.latex,
        )
    }

    @Test
    fun nestedParenthesizedFractionIsConverted() {
        assertEquals(
            "\\frac{e^{x} + e^{-x}}{2-3e^{x}}",
            MathEquationConverter.convertEquationTextToLaTeX("(e^x + e^-x)/(2-3e^x)"),
        )
    }

    @Test
    fun squareRootFractionIsConverted() {
        assertEquals(
            "\\frac{\\sqrt{7}}{4}",
            MathEquationConverter.convertEquationTextToLaTeX("√{7} / 4"),
        )
    }

    @Test
    fun workbookLatexCommandsArePreserved() {
        val source = "y = e⁻⁵ˣ (c₁ \\cos 4x + c₂ \\sin 4x)"

        assertEquals(
            "y = e^{-5x} (c_{1} \\cos 4x + c_{2} \\sin 4x)",
            MathEquationConverter.convertEquationTextToLaTeX(source),
        )
    }

    @Test
    fun measurementRatesAreNotChangedIntoNumericFractions() {
        val math = MathEquationConverter.renderSegments("Volume increases at 8 ft³ / min.")
            .single { it.isMath }

        assertEquals("8 \\mathrm{ft^{3}}\\,/\\,\\mathrm{min}", math.latex)
    }

    @Test
    fun explicitLatexDelimitersAreAuthoritative() {
        val source = "Area is \\(\\pi r^2\\), while $$\\int_0^1 x^2 \\, dx$$ is displayed."
        val math = MathEquationConverter.renderSegments(source).filter { it.isMath }

        assertEquals(2, math.size)
        assertEquals("\\pi r^2", math[0].latex)
        assertFalse(math[0].displayMode)
        assertEquals("\\int_0^1 x^2 \\, dx", math[1].latex)
        assertTrue(math[1].displayMode)
    }

    @Test
    fun absoluteValuesAndVectorProductsAreDetected() {
        val source = "Find a · b if |a| = 26 and |b| = 17."
        val mathSources = MathEquationConverter.renderSegments(source)
            .filter { it.isMath }
            .map { it.source }

        assertTrue("a · b" in mathSources)
        assertTrue("|a| = 26" in mathSources)
        assertTrue("|b| = 17" in mathSources)
    }

    @Test
    fun proseCurrencyAndBlankLinesStayAsText() {
        assertFalse(MathEquationConverter.containsMathSyntax("The fee is P 1,250.00"))
        assertFalse(MathEquationConverter.containsMathSyntax("________"))
        assertFalse(MathEquationConverter.containsMathSyntax("Method of tangents"))
    }
}

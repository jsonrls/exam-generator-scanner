package com.pbec.preboardexamchecker.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PdfEquationLayoutTest {
    @Test
    fun fractionKeepsItsNaturalHeightAndTheSamePrintScale() {
        val simple = PdfEquationLayout.fromCss(120f, 48f, 1f)
        val fraction = PdfEquationLayout.fromCss(120f, 96f, 1f)
        assertEquals(30f, simple.size.width, 0f)
        assertEquals(simple.size.width, fraction.size.width, 0f)
        assertEquals(2f * simple.size.height, fraction.size.height, 0f)
        assertEquals(288f, simple.bitmapWidth / (simple.size.width / 72f), 0f)
    }

    @Test
    fun deviceDensityDoesNotChangeRasterResolutionOrPdfSize() {
        val lowDensity = PdfEquationLayout.fromCss(121.25f, 53.5f, 1f)
        val highDensity = PdfEquationLayout.fromCss(121.25f, 53.5f, 3.5f)
        assertEquals(lowDensity.size, highDensity.size)
        assertEquals(lowDensity.bitmapWidth, highDensity.bitmapWidth)
        assertEquals(lowDensity.bitmapHeight, highDensity.bitmapHeight)
        assertTrue(highDensity.viewWidth >= highDensity.bitmapWidth * 3.5f)
        assertTrue(highDensity.viewHeight >= highDensity.bitmapHeight * 3.5f)
    }

    @Test
    fun narrowFormulasKeepIntrinsicWidthWithoutUpscaling() {
        val size = PdfEquationLayout.fromCss(24f, 48f, 2f).size
        assertEquals(PdfEquationSize(6f, 12f), size.fitToWidth(500f))
    }

    @Test
    fun overlyWideFormulaFitsWidthWithoutDistortingAspectRatio() {
        assertEquals(PdfEquationSize(100f, 20f), PdfEquationSize(500f, 100f).fitToWidth(100f))
    }

    @Test
    fun optionsUseFewerColumnsWhenEquationsOrProseNeedRoom() {
        assertEquals(4, PdfEquationLayout.optionColumns(listOf(100f, 120f), 520f, 2f))
        assertEquals(2, PdfEquationLayout.optionColumns(listOf(100f, 200f), 520f, 2f))
        assertEquals(1, PdfEquationLayout.optionColumns(listOf(100f, 300f), 520f, 2f))
        assertEquals(1, PdfEquationLayout.optionColumns(listOf(700f), 520f, 2f))
    }

    @Test(expected = IllegalArgumentException::class)
    fun invalidDimensionsAreRejected() {
        PdfEquationLayout.fromCss(Float.NaN, 48f, 1f)
    }
}

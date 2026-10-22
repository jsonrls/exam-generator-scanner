package com.pbec.preboardexamchecker.utils

import kotlin.math.ceil
import kotlin.math.min

internal data class PdfEquationSize(val width: Float, val height: Float) {
    fun fitToWidth(maxWidth: Float): PdfEquationSize {
        require(maxWidth.isFinite() && maxWidth > 0f)
        val scale = min(1f, maxWidth / width)
        return PdfEquationSize(width * scale, height * scale)
    }
}

internal data class PdfEquationLayout(
    val bitmapWidth: Int,
    val bitmapHeight: Int,
    val viewWidth: Int,
    val viewHeight: Int,
    val nativePixelsPerCssPixel: Float,
    val size: PdfEquationSize
) {
    companion object {
        // Render at 288 DPI, independently of Android display density and PDF placement size.
        const val PIXELS_PER_POINT = 4f

        fun fromCss(width: Float, height: Float, nativePixelsPerCssPixel: Float): PdfEquationLayout {
            require(listOf(width, height, nativePixelsPerCssPixel).all { it.isFinite() && it > 0f })
            require(width <= 16384f && height <= 16384f && width * height <= 16_000_000f) {
                "Equation exceeds the PDF raster size limit"
            }
            val bitmapWidth = ceil(width).toInt()
            val bitmapHeight = ceil(height).toInt()
            return PdfEquationLayout(
                bitmapWidth, bitmapHeight,
                ceil(bitmapWidth * nativePixelsPerCssPixel).toInt(),
                ceil(bitmapHeight * nativePixelsPerCssPixel).toInt(),
                nativePixelsPerCssPixel,
                PdfEquationSize(bitmapWidth / PIXELS_PER_POINT, bitmapHeight / PIXELS_PER_POINT)
            )
        }

        fun optionColumns(widths: List<Float>, contentWidth: Float, padding: Float): Int =
            listOf(4, 2, 1).first { columns ->
                columns == 1 || widths.all { it <= contentWidth / columns - 2f * padding }
            }
    }
}

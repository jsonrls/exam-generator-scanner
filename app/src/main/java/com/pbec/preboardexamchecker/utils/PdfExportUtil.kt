package com.pbec.preboardexamchecker.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.io.image.ImageData
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.LineSeparator
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.itextpdf.layout.properties.VerticalAlignment
import com.pbec.preboardexamchecker.data.models.Question
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.Normalizer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class PdfExportUtil(private val context: Context) {

    private lateinit var document: Document
    private lateinit var pdfDocument: PdfDocument
    private lateinit var pdfWriter: PdfWriter
    private lateinit var boldDocumentFont: PdfFont
    private lateinit var normalDocumentFont: PdfFont
    private var textFallbackFonts: List<PdfFont> = emptyList()
    private var sharedWebView: WebView? = null
    private var equationRenderHost: PdfEquationRenderHost? = null
    private val defaultFontSize = 10f
    private val textLineSpacing = defaultFontSize * 1.1f
    private val minCellWidth = 10f
    private val equationSpacing = defaultFontSize * 0.15f
    private val optionHorizontalPadding = 2f
    private val optionVerticalPadding = 0.5f

    @SuppressLint("SetJavaScriptEnabled")
    private fun createWebView(context: Context): WebView? {
        return try {
            val webView = WebView(context)
            webView.settings.javaScriptEnabled = true
            webView.setBackgroundColor(Color.WHITE)
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            Log.d("PdfExportUtil", "WebView created successfully")
            webView
        } catch (e: Exception) {
            Log.e("PdfExportUtil", "Failed to create WebView: ${e.message}")
            null
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(webView: WebView) {
        try {
            webView.settings.javaScriptEnabled = true
            webView.settings.loadWithOverviewMode = false
            webView.settings.useWideViewPort = false
            webView.settings.builtInZoomControls = false
            webView.settings.displayZoomControls = false
            webView.settings.setSupportZoom(false)
            webView.settings.allowFileAccess = true
            webView.settings.allowUniversalAccessFromFileURLs = true
            webView.setBackgroundColor(Color.WHITE)
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            WebView.setWebContentsDebuggingEnabled(true)
            Log.d("PdfExportUtil", "WebView setup completed")
        } catch (e: Exception) {
            Log.e("PdfExportUtil", "Failed to setup WebView: ${e.message}")
        }
    }

    private fun loadHtmlTemplate(context: Context): String {
        return try {
            context.assets.open("katex/pdf_math_template.html").bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            Log.e("PdfExportUtil", "Failed to load pdf_math_template.html: ${e.message}")
            ""
        }
    }

    private fun loadKaTeXCss(context: Context): String {
        return try {
            context.assets.open("katex/katex.min.css").bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            Log.e("PdfExportUtil", "Failed to load katex.min.css: ${e.message}")
            ""
        }
    }

    private fun loadKaTeXJs(context: Context): String {
        return try {
            context.assets.open("katex/katex.min.js").bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            Log.e("PdfExportUtil", "Failed to load katex.min.js: ${e.message}")
            ""
        }
    }

    private fun validateAssets(context: Context): Boolean {
        try {
            val html = loadHtmlTemplate(context)
            val css = loadKaTeXCss(context)
            val js = loadKaTeXJs(context)
            Log.d("PdfExportUtil", "Asset validation: HTML=${html.length}, CSS=${css.length}, JS=${js.length}")
            if (html.isEmpty() || css.isEmpty() || js.isEmpty()) {
                Log.e("PdfExportUtil", "Asset validation failed: Empty asset detected")
                return false
            }
            if (!html.contains("window.androidCallback")) {
                Log.e("PdfExportUtil", "Asset validation failed: window.androidCallback not found in math_template.html")
                return false
            }
            return true
        } catch (e: IOException) {
            Log.e("PdfExportUtil", "Asset validation failed: ${e.message}")
            return false
        }
    }

    private suspend fun WebView.awaitPageLoad() {
        val cssContent = loadKaTeXCss(context)
        val jsContent = loadKaTeXJs(context)
        val finalHtml = loadHtmlTemplate(context)
            .replace("<link rel=\"stylesheet\" href=\"katex.min.css\">", "<style>$cssContent</style>")
            .replace("<script defer src=\"katex.min.js\"></script>", "<script>$jsContent</script>")

        return suspendCancellableCoroutine { continuation ->
            val client = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    Log.d("PdfExportUtil", "WebView page finished loading from data")
                    if (continuation.isActive) continuation.resume(Unit)
                }

                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    Log.e("PdfExportUtil", "WebView error: ${error?.description} (Code: ${error?.errorCode})")
                    if (continuation.isActive) continuation.resumeWithException(RuntimeException("WebView error: ${error?.description}"))
                }
            }
            this.webViewClient = client
            Log.d("PdfExportUtil", "Loading WebView with combined HTML data")
            this.loadDataWithBaseURL("file:///android_asset/katex/", finalHtml, "text/html", "UTF-8", null)
            continuation.invokeOnCancellation {
                CoroutineScope(Dispatchers.Main).launch {
                    this@awaitPageLoad.stopLoading()
                    this@awaitPageLoad.loadUrl("about:blank")
                    Log.d("PdfExportUtil", "WebView page load cancelled")
                }
            }
        }
    }

    private fun estimateTextWidth(text: String, font: PdfFont, fontSize: Float): Float {
        var width = 0f
        var index = 0
        while (index < text.length) {
            val codePoint = text.codePointAt(index)
            width += selectPdfFont(codePoint, font).getWidth(codePoint, fontSize)
            index += Character.charCount(codePoint)
        }
        return width
    }

    private fun appendPdfText(paragraph: Paragraph, text: String, font: PdfFont) {
        var index = 0
        val normal = StringBuilder()

        fun flushNormal() {
            if (normal.isNotEmpty()) {
                appendTextRun(paragraph, normal.toString(), font, defaultFontSize, 0f)
                normal.clear()
            }
        }

        while (index < text.length) {
            val marker = text[index]
            if ((marker == '^' || marker == '_') && index + 1 < text.length) {
                val script = readScriptText(text, index + 1)
                if (script != null) {
                    flushNormal()
                    val rise = if (marker == '^') defaultFontSize * 0.35f else -defaultFontSize * 0.18f
                    appendTextRun(paragraph, script.first, font, defaultFontSize * 0.72f, rise)
                    index = script.second
                    continue
                }
            }

            val codePoint = text.codePointAt(index)
            normal.appendCodePoint(codePoint)
            index += Character.charCount(codePoint)
        }

        flushNormal()
    }

    private fun appendTextRun(
        paragraph: Paragraph,
        text: String,
        preferredFont: PdfFont,
        fontSize: Float,
        textRise: Float
    ) {
        var index = 0
        var runFont: PdfFont? = null
        var run = StringBuilder()

        fun flushRun() {
            val font = runFont
            if (font != null && run.isNotEmpty()) {
                val textElement = Text(run.toString())
                    .setFont(font)
                    .setFontSize(fontSize)
                if (textRise != 0f) textElement.setTextRise(textRise)
                paragraph.add(textElement)
                run = StringBuilder()
            }
        }

        while (index < text.length) {
            val codePoint = text.codePointAt(index)
            val selectedFont = selectPdfFont(codePoint, preferredFont)
            if (runFont != null && runFont !== selectedFont) flushRun()
            runFont = selectedFont
            run.appendCodePoint(codePoint)
            index += Character.charCount(codePoint)
        }

        flushRun()
    }

    private fun selectPdfFont(codePoint: Int, preferredFont: PdfFont): PdfFont {
        if (preferredFont.containsGlyph(codePoint)) return preferredFont
        return textFallbackFonts.firstOrNull { it !== preferredFont && it.containsGlyph(codePoint) }
            ?: preferredFont
    }

    private fun readScriptText(text: String, startIndex: Int): Pair<String, Int>? {
        if (startIndex >= text.length) return null

        if (text[startIndex] == '{') {
            val endIndex = text.indexOf('}', startIndex + 1)
            if (endIndex > startIndex + 1) {
                return text.substring(startIndex + 1, endIndex) to (endIndex + 1)
            }
            return null
        }

        var endIndex = startIndex + 1
        if (text[startIndex].isDigit()) {
            while (endIndex < text.length && text[endIndex].isDigit()) {
                endIndex++
            }
        }
        return text.substring(startIndex, endIndex) to endIndex
    }

    private fun isSimpleScriptMarkup(text: String): Boolean =
        text.contains(Regex("[_^](?:\\{[^}]+\\}|[A-Za-z0-9+\\-=()])")) &&
            !text.contains("/") &&
            !text.contains("\\")

    private data class OptionPart(
        val text: String? = null,
        val imageData: ImageData? = null,
        val equationSize: PdfEquationSize? = null,
        val estimatedWidth: Float = 0f
    )

    private suspend fun renderEquationPart(latex: String): OptionPart {
        val rendered = renderPdfEquation(
            checkNotNull(sharedWebView) { "PDF equation WebView is unavailable" },
            latex,
            defaultFontSize
        )
        return try {
            OptionPart(
                imageData = ImageDataFactory.create(rendered.bitmap.toByteArray()),
                equationSize = rendered.size,
                estimatedWidth = rendered.size.width + 2f * equationSpacing
            )
        } finally {
            rendered.bitmap.recycle()
        }
    }

    private fun equationImage(part: OptionPart, availableWidth: Float): Image {
        val size = checkNotNull(part.equationSize).fitToWidth(availableWidth - 2f * equationSpacing)
        return Image(checkNotNull(part.imageData))
            .setWidth(size.width)
            .setHeight(size.height)
            .setMarginLeft(equationSpacing)
            .setMarginRight(equationSpacing)
    }

    suspend fun exportExamToPdf(outputUri: Uri, examTitle: String, subjectCluster: String, questions: List<Question>) {
        Log.d("PdfExportUtil", "Starting exportExamToPdf: $examTitle, ${questions.size} questions")

        if (examTitle.isBlank()) throw IllegalArgumentException("Exam title cannot be empty")
        if (questions.isEmpty()) throw IllegalArgumentException("Questions list cannot be empty")
        if (!validateAssets(context)) throw IllegalStateException("Asset validation failed")

        try {
            withContext(Dispatchers.Main) {
                val webView = createWebView(context) ?: error("Failed to initialize WebView")
                sharedWebView = webView
                setupWebView(webView)
                val host = PdfEquationRenderHost(context, webView)
                equationRenderHost = host
                host.awaitReady()
                webView.awaitPageLoad()
            }
            if (sharedWebView == null) throw IllegalStateException("Failed to initialize WebView")

            withContext(Dispatchers.IO) {
                val longBondPageSize = PageSize(8.5f * 72f, 13f * 72f)
                pdfWriter = PdfWriter(context.contentResolver.openOutputStream(outputUri) ?: throw IOException("Failed to open output stream for $outputUri"))
                pdfDocument = PdfDocument(pdfWriter)
                document = Document(pdfDocument, longBondPageSize)
                document.setMargins(28f, 28f, 28f, 28f)

                boldDocumentFont = createPreferredPdfFont(
                    candidates = listOf(
                        "/system/fonts/NotoSans-Bold.ttf",
                        "/system/fonts/Roboto-Bold.ttf",
                        "/system/fonts/DroidSans-Bold.ttf"
                    ),
                    fallback = StandardFonts.HELVETICA_BOLD
                )
                normalDocumentFont = createPreferredPdfFont(
                    candidates = listOf(
                        "/system/fonts/NotoSans-Regular.ttf",
                        "/system/fonts/Roboto-Regular.ttf",
                        "/system/fonts/DroidSans.ttf"
                    ),
                    fallback = StandardFonts.HELVETICA
                )
                textFallbackFonts = listOf(normalDocumentFont) + createAvailablePdfFonts(
                    candidates = listOf(
                        "/system/fonts/NotoSansMath-Regular.ttf",
                        "/system/fonts/NotoSansSymbols2-Regular.ttf",
                        "/system/fonts/NotoSansSymbols-Regular.ttf",
                        "/system/fonts/NotoSansSymbols-Regular-Subsetted.ttf",
                        "/system/fonts/NotoSerif-Regular.ttf",
                        "/system/fonts/DroidSansFallback.ttf",
                        "/System/Library/Fonts/Supplemental/STIXTwoMath.otf",
                        "/System/Library/Fonts/Supplemental/STIXGeneral.otf",
                        "/System/Library/Fonts/Apple Symbols.ttf",
                        "/System/Library/Fonts/Symbol.ttf"
                    )
                )

                document.add(
                    Paragraph(resolveClusterHeader(subjectCluster))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFont(boldDocumentFont)
                        .setBold()
                        .setFontSize(14f)
                        .setMarginBottom(5f)
                )
                document.add(
                    LineSeparator(SolidLine(0.8f))
                        .setMarginBottom(5f)
                )
                document.add(
                    Paragraph()
                        .add(Text("Instruction: ").setFont(boldDocumentFont).setBold())
                        .add(
                            Text(
                                "Select the correct answer for each of the following questions. " +
                                    "Mark only one answer for each item by shading the box corresponding to the letter " +
                                    "of your choice on the answer sheet provided."
                            ).setFont(normalDocumentFont)
                        )
                        .setTextAlignment(TextAlignment.JUSTIFIED)
                        .setFontSize(defaultFontSize)
                        .setFixedLeading(textLineSpacing)
                        .setMarginBottom(5f)
                )
                document.add(
                    LineSeparator(SolidLine(0.8f))
                        .setMarginBottom(4f)
                )
                document.add(
                    Paragraph("Multiple Choice")
                        .setFont(boldDocumentFont)
                        .setBold()
                        .setFontSize(defaultFontSize)
                        .setMarginBottom(2f)
                )

                val pageEffectiveWidth = document.getPageEffectiveArea(longBondPageSize).width

                for (index in questions.indices) {
                    val question = questions[index]
                    if (question.questionText.isBlank()) {
                        Log.w("PdfExportUtil", "Skipping empty question at index $index")
                        continue
                    }

                    val questionLayoutTable = Table(UnitValue.createPercentArray(floatArrayOf(5f, 95f)))
                    questionLayoutTable.setWidth(UnitValue.createPercentValue(100f))
                    questionLayoutTable.setMarginBottom(0f)
                    questionLayoutTable.setKeepTogether(true)

                    val questionNumberCell = Cell().add(
                        Paragraph("${index + 1}.")
                            .setFont(boldDocumentFont)
                            .setFontSize(defaultFontSize)
                            .setMargin(0f)
                            .setFixedLeading(textLineSpacing)
                    ).setBorder(null).setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.TOP)
                    questionLayoutTable.addCell(questionNumberCell)

                    val questionContentCell = Cell().setBorder(null)
                    val originalQuestionText = sanitizePdfText(question.questionText)
                    val questionIsCurrency = MathEquationConverter.isCurrencyValue(originalQuestionText)
                    val questionHasMath = MathEquationConverter.containsMathSyntax(originalQuestionText) || MathEquationConverter.isChemicalFormula(originalQuestionText)

                    val questionTextParagraph = Paragraph()
                        .setMargin(0f)
                        .setFontSize(defaultFontSize)

                    if (questionIsCurrency || !questionHasMath) {
                        appendPdfText(questionTextParagraph, originalQuestionText, normalDocumentFont)
                        questionTextParagraph.setFixedLeading(textLineSpacing)
                    } else {
                        val parts = MathEquationConverter.splitMathParts(originalQuestionText)
                        var hasEquationInParagraph = false
                        for ((part, isMath) in parts) {
                            if (!isMath || MathEquationConverter.getExcludedFractionUnits().contains(part.lowercase().replace(Regex("[^a-z0-9/]"), ""))) {
                                if (part.isNotEmpty()) appendPdfText(questionTextParagraph, part, normalDocumentFont)
                            } else {
                                try {
                                    val simpleMathText = convertSimpleMathToText(part)
                                    if (simpleMathText != null) {
                                        appendPdfText(questionTextParagraph, simpleMathText, normalDocumentFont)
                                        continue
                                    }
                                    if (isSimpleScriptMarkup(part)) {
                                        appendPdfText(questionTextParagraph, part, normalDocumentFont)
                                        continue
                                    }
                                    val convertedPart = MathEquationConverter.convertEquationTextToLaTeX(part)
                                    if (MathEquationConverter.containsMathSyntax(part) || MathEquationConverter.isChemicalFormula(part)) {
                                        val equation = renderEquationPart(convertedPart)
                                        questionTextParagraph.add(equationImage(equation, pageEffectiveWidth * 0.95f - 4f))
                                        hasEquationInParagraph = true
                                    } else {
                                        appendPdfText(questionTextParagraph, part, normalDocumentFont)
                                    }
                                } catch (e: Exception) {
                                    if (e is CancellationException) throw e
                                    Log.e("PdfExportUtil", "Error rendering LaTeX '$part': ${e.message}")
                                    appendPdfText(questionTextParagraph, part, normalDocumentFont)
                                }
                            }
                        }
                        if (hasEquationInParagraph) {
                            questionTextParagraph.setMultipliedLeading(1.1f)
                        } else {
                            questionTextParagraph.setFixedLeading(textLineSpacing)
                        }
                    }
                    questionContentCell.add(questionTextParagraph)

                    val options = listOf(question.optionA, question.optionB, question.optionC, question.optionD)
                        .map { sanitizePdfText(it) }
                        .filter { it.isNotBlank() }
                    val prefixes = listOf("A. ", "B. ", "C. ", "D. ").take(options.size)
                    val renderedOptionParts = mutableListOf<List<OptionPart>>()
                    val estimatedOptionWidths = mutableListOf<Float>()

                    for (optionText in options) {
                        val optionIsCurrency = MathEquationConverter.isCurrencyValue(optionText)
                        val optionHasMath = MathEquationConverter.containsMathSyntax(optionText) ||
                            MathEquationConverter.isChemicalFormula(optionText)
                        val parts = if (optionIsCurrency || !optionHasMath) {
                            listOf(optionText to false)
                        } else {
                            MathEquationConverter.splitMathParts(optionText)
                        }
                        val renderedParts = mutableListOf<OptionPart>()
                        for ((part, isMath) in parts) {
                            val excludedUnit = MathEquationConverter.getExcludedFractionUnits()
                                .contains(part.lowercase().replace(Regex("[^a-z0-9/]"), ""))
                            val simpleMath = if (isMath) convertSimpleMathToText(part) else null
                            if (!isMath || excludedUnit || simpleMath != null || isSimpleScriptMarkup(part)) {
                                val text = simpleMath ?: part
                                renderedParts.add(OptionPart(text = text, estimatedWidth = estimateTextWidth(text, normalDocumentFont, defaultFontSize)))
                            } else {
                                try {
                                    renderedParts.add(renderEquationPart(MathEquationConverter.convertEquationTextToLaTeX(part)))
                                } catch (error: Exception) {
                                    if (error is CancellationException) throw error
                                    Log.w("PdfExportUtil", "Option equation rendering failed; preserving source text", error)
                                    renderedParts.add(OptionPart(text = part, estimatedWidth = estimateTextWidth(part, normalDocumentFont, defaultFontSize)))
                                }
                            }
                        }
                        renderedOptionParts.add(renderedParts)
                        val prefixWidth = estimateTextWidth(prefixes[renderedOptionParts.lastIndex], normalDocumentFont, defaultFontSize)
                        estimatedOptionWidths.add(prefixWidth + renderedParts.sumOf { it.estimatedWidth.toDouble() }.toFloat())
                    }

                    // Account for the question cell's default left/right padding.
                    val contentCellWidth = pageEffectiveWidth * 0.95f - 4f
                    val columns = PdfEquationLayout.optionColumns(estimatedOptionWidths, contentCellWidth, optionHorizontalPadding)
                    val optionWidth = contentCellWidth / columns - 2f * optionHorizontalPadding
                    val optionsTable = Table(UnitValue.createPercentArray(FloatArray(columns) { 1f }))
                        .useAllAvailableWidth()
                        .setKeepTogether(true)
                    for (j in options.indices) {
                        val parts = renderedOptionParts[j]
                        val optionParagraph = Paragraph()
                            .setMargin(0f)
                            .setFontSize(defaultFontSize)
                        if (parts.any { it.imageData != null }) {
                            optionParagraph.setMultipliedLeading(1.1f)
                        } else {
                            optionParagraph.setFixedLeading(textLineSpacing)
                        }
                        optionParagraph.add(Text(prefixes[j]).setFont(normalDocumentFont))
                        val prefixWidth = estimateTextWidth(prefixes[j], normalDocumentFont, defaultFontSize)
                        for (part in parts) {
                            if (part.text != null) {
                                appendPdfText(optionParagraph, part.text, normalDocumentFont)
                            } else {
                                optionParagraph.add(equationImage(part, optionWidth - prefixWidth))
                            }
                        }
                        optionsTable.addCell(
                            Cell().add(optionParagraph)
                                .setPaddingLeft(optionHorizontalPadding)
                                .setPaddingRight(optionHorizontalPadding)
                                .setPaddingTop(optionVerticalPadding)
                                .setPaddingBottom(optionVerticalPadding)
                                .setBorder(null)
                                .setVerticalAlignment(VerticalAlignment.TOP)
                                .setMinWidth(minCellWidth)
                        )
                    }

                    questionContentCell.add(optionsTable)
                    questionLayoutTable.addCell(questionContentCell)
                    document.add(questionLayoutTable)
                    Log.d("PdfExportUtil", "Completed question ${index + 1}")
                }

                document.add(
                    Paragraph("\n*** END ***")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFont(normalDocumentFont)
                        .setFontSize(defaultFontSize)
                        .setMarginTop(10f)
                        .setMarginBottom(8f)
                )
                document.add(
                    Paragraph(
                        "SUBMIT THIS TEST QUESTIONS SET TOGETHER WITH THE ANSWER SHEET TO YOUR WATCHERS. " +
                            "BRINGING THE TEST QUESTIONS SET OUT OF THE ROOM WILL BE A GROUND FOR DISCIPLINARY ACTION"
                    )
                        .setTextAlignment(TextAlignment.JUSTIFIED)
                        .setFont(normalDocumentFont)
                        .setFontSize(defaultFontSize)
                        .setFixedLeading(textLineSpacing)
                        .setMarginBottom(0f)
                )

                document.close()
                pdfDocument.close()
                pdfWriter.close()
                Log.d("PdfExportUtil", "PDF export completed")
            }
        } catch (e: Exception) {
            Log.e("PdfExportUtil", "Fatal error in exportExamToPdf: ${e.message}")
            throw e
        } finally {
            withContext(NonCancellable + Dispatchers.Main) {
                try {
                    equationRenderHost?.close()
                } finally {
                    equationRenderHost = null
                    sharedWebView?.stopLoading()
                    sharedWebView?.destroy()
                    sharedWebView = null
                    Log.d("PdfExportUtil", "Shared WebView destroyed")
                }
            }
        }
    }

    private fun Bitmap.toByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private fun resolveClusterHeader(subjectCluster: String): String {
        return when (subjectCluster.trim().lowercase()) {
            "mathematics" -> "MATHEMATICS"
            "esas", "engineering sciences and allied subjects" -> "ENGINEERING SCIENCES AND ALLIED SUBJECTS"
            "professional ee", "professional electrical engineering subjects", "professionla electrical engineering subjects" ->
                "PROFESSIONAL ELECTRICAL ENGINEERING SUBJECTS"
            else -> subjectCluster.uppercase()
        }
    }

    private fun createPreferredPdfFont(candidates: List<String>, fallback: String): PdfFont {
        for (path in candidates) {
            try {
                if (File(path).exists()) {
                    return PdfFontFactory.createFont(path, PdfEncodings.IDENTITY_H)
                }
            } catch (_: Exception) {
                // Try next candidate font.
            }
        }
        return PdfFontFactory.createFont(fallback)
    }

    private fun createAvailablePdfFonts(candidates: List<String>): List<PdfFont> =
        candidates.mapNotNull { path ->
            try {
                if (File(path).exists()) {
                    PdfFontFactory.createFont(path, PdfEncodings.IDENTITY_H)
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.w("PdfExportUtil", "Skipping PDF fallback font '$path': ${e.message}")
                null
            }
        }

    private fun sanitizePdfText(raw: String): String {
        if (raw.isBlank()) return raw

        val normalized = Normalizer.normalize(raw, Normalizer.Form.NFC)
        return normalized
            .replace("\u00A0", " ")
            .replace("\u200B", "")
            .replace("\u200C", "")
            .replace("\u200D", "")
            .replace("\uFEFF", "")
            .replace("“", "\"")
            .replace("”", "\"")
            .replace("‘", "'")
            .replace("’", "'")
            .replace("–", "-")
            .replace("—", "-")
            .replace("•", "*")
            .replace("…", "...")
            .replace(Regex("[\\p{Cntrl}&&[^\n\t]]"), "")
    }

    private fun convertSimpleMathToText(part: String): String? {
        val cleaned = sanitizePdfText(part.trim())

        // Keep simple ratio/fraction tokens as plain text to avoid raster artifacts (e.g., 1/L, V/I, kg/m).
        if (cleaned.matches(Regex("^[A-Za-z0-9().+\\-]+\\s*/\\s*[A-Za-z0-9().+\\-]+$"))) {
            return cleaned.replace(Regex("\\s*/\\s*"), "/")
        }

        val match = Regex("^([A-Za-z0-9()+\\-*/.]+)\\^([0-9+\\-]+)$").find(cleaned) ?: return null
        val base = match.groupValues[1]
        val exp = match.groupValues[2]
        val superscript = buildString {
            exp.forEach { ch ->
                append(
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
                        else -> return null
                    }
                )
            }
        }
        return base + superscript
    }
}

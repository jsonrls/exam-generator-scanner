package com.pbec.preboardexamchecker.ui.exams

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.pbec.preboardexamchecker.utils.MathEquationConverter
import com.pbec.preboardexamchecker.utils.MathRenderSegment
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import kotlin.math.abs

private const val MIN_MATH_VIEW_HEIGHT_DP = 32f
private const val MAX_MATH_VIEW_HEIGHT_DP = 1_200f
private const val HEIGHT_CHANGE_THRESHOLD_DP = 0.5f

private class MathRenderBridge(
    private val onHeightChanged: (Float) -> Unit,
) {
    private val mainHandler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun reportHeight(heightCssPixels: Double) {
        if (!heightCssPixels.isFinite() || heightCssPixels <= 0.0) return
        mainHandler.post { onHeightChanged(heightCssPixels.toFloat()) }
    }
}

@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun MathTextView(
    text: String,
    modifier: Modifier = Modifier,
) {
    val segments = remember(text) { MathEquationConverter.renderSegments(text) }
    if (segments.none { it.isMath }) {
        Text(
            text = text,
            modifier = modifier,
            style = MaterialTheme.typography.bodyLarge,
        )
        return
    }

    val bodyStyle = MaterialTheme.typography.bodyLarge
    val fontScale = LocalDensity.current.fontScale
    val fontSizeCssPixels = (bodyStyle.fontSize.value * fontScale).coerceAtLeast(12f)
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val cssColor = remember(textColor) {
        String.format(Locale.US, "#%06X", textColor and 0x00FFFFFF)
    }
    val segmentsJson = remember(segments) { segments.toJson() }
    val renderScript = remember(segmentsJson, cssColor, fontSizeCssPixels) {
        val quotedSegments = JSONObject.quote(segmentsJson)
        val quotedColor = JSONObject.quote(cssColor)
        val fontSize = String.format(Locale.US, "%.2f", fontSizeCssPixels)
        """
            if (window.mathRenderer) {
                window.mathRenderer.render($quotedSegments, $quotedColor, $fontSize);
            }
        """.trimIndent()
    }

    var contentHeightDp by remember(text) { mutableFloatStateOf(MIN_MATH_VIEW_HEIGHT_DP) }

    AndroidView(
        modifier = modifier.height(contentHeightDp.dp),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.allowFileAccess = true
                settings.allowContentAccess = false
                settings.blockNetworkLoads = true
                settings.domStorageEnabled = false
                settings.javaScriptCanOpenWindowsAutomatically = false
                settings.setSupportMultipleWindows(false)

                setBackgroundColor(Color.TRANSPARENT)
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                overScrollMode = View.OVER_SCROLL_NEVER
                contentDescription = text

                addJavascriptInterface(
                    MathRenderBridge { reportedHeight ->
                        val nextHeight = reportedHeight
                            .plus(2f)
                            .coerceIn(MIN_MATH_VIEW_HEIGHT_DP, MAX_MATH_VIEW_HEIGHT_DP)
                        if (abs(contentHeightDp - nextHeight) > HEIGHT_CHANGE_THRESHOLD_DP) {
                            contentHeightDp = nextHeight
                        }
                    },
                    "AndroidMathBridge",
                )

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        (view?.tag as? String)?.let { view.evaluateJavascript(it, null) }
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                        val message = consoleMessage?.message().orEmpty()
                        if (message.startsWith("Math render error:")) {
                            Log.w("MathTextView", message)
                        }
                        return true
                    }
                }

                tag = renderScript
                loadUrl("file:///android_asset/katex/math_template.html")
            }
        },
        update = { webView ->
            webView.contentDescription = text
            webView.tag = renderScript
            webView.evaluateJavascript(renderScript, null)
        },
    )
}

private fun List<MathRenderSegment>.toJson(): String = JSONArray().apply {
    this@toJson.forEach { segment ->
        put(
            JSONObject().apply {
                put("source", segment.source)
                put("latex", segment.latex ?: JSONObject.NULL)
                put("displayMode", segment.displayMode)
            }
        )
    }
}.toString()

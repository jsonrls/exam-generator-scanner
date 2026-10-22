package com.pbec.preboardexamchecker.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import androidx.core.view.doOnLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal data class PdfEquationBitmap(val bitmap: Bitmap, val size: PdfEquationSize)

internal suspend fun renderPdfEquation(
    webView: WebView,
    latex: String,
    fontSize: Float,
    timeoutMillis: Long = 5_000L
): PdfEquationBitmap =
    withContext(Dispatchers.Main) {
        check(webView.isAttachedToWindow && webView.visibility == View.VISIBLE) {
            "PDF equation rendering requires an attached, visible WebView"
        }
        withTimeoutOrNull(timeoutMillis) {
            suspendCancellableCoroutine<PdfEquationBitmap> { continuation ->
                val request = UUID.randomUUID().toString()
                webView.webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(message: ConsoleMessage): Boolean {
                        val uri = message.message().toUri()
                        if (uri.scheme != "js-callback" || uri.getQueryParameter("request") != request) return false
                        if (!continuation.isActive) return true
                        try {
                            if (uri.host == "renderError") {
                                error("KaTeX rendering failed: ${uri.getQueryParameter("message")}")
                            }
                            if (uri.host != "renderComplete") return false
                            val width = requireNotNull(uri.getQueryParameter("width")?.toFloatOrNull())
                            val height = requireNotNull(uri.getQueryParameter("height")?.toFloatOrNull())
                            val viewportWidth = requireNotNull(uri.getQueryParameter("viewportWidth")?.toFloatOrNull())
                            val layout = PdfEquationLayout.fromCss(width, height, webView.width / viewportWidth)
                            webView.layoutParams = FrameLayout.LayoutParams(layout.viewWidth, layout.viewHeight)
                            webView.doOnLayout {
                                if (!continuation.isActive) return@doOnLayout
                                // The attached hierarchy and completed layout satisfy WebView's first-frame contract.
                                webView.postVisualStateCallback(0L, object : WebView.VisualStateCallback() {
                                override fun onComplete(requestId: Long) {
                                    if (!continuation.isActive) return
                                    var bitmap: Bitmap? = null
                                    try {
                                        check(webView.isAttachedToWindow) { "PDF rendering display was detached" }
                                        bitmap = createBitmap(layout.bitmapWidth, layout.bitmapHeight)
                                        val canvas = Canvas(bitmap)
                                        canvas.drawColor(Color.WHITE)
                                        val scale = 1f / layout.nativePixelsPerCssPixel
                                        canvas.scale(scale, scale)
                                        webView.draw(canvas)
                                        continuation.resume(PdfEquationBitmap(bitmap, layout.size))
                                    } catch (error: Exception) {
                                        bitmap?.recycle()
                                        continuation.resumeWithException(error)
                                    }
                                }
                                })
                            }
                        } catch (error: Exception) {
                            if (continuation.isActive) continuation.resumeWithException(error)
                        }
                        return true
                    }
                }
                // A previous render may have resized the viewport. Reset it before each measurement.
                webView.layoutParams = FrameLayout.LayoutParams(2048, 1024)
                webView.doOnLayout {
                    if (!continuation.isActive) return@doOnLayout
                    webView.evaluateJavascript(
                        "window.androidCallback.render(${JSONObject.quote(latex)}, " +
                            "${fontSize * PdfEquationLayout.PIXELS_PER_POINT}, ${JSONObject.quote(request)});",
                        null
                    )
                }
            }
        } ?: error("PDF equation rendering timed out")
    }

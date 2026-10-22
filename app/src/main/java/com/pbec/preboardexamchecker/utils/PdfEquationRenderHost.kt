package com.pbec.preboardexamchecker.utils

import android.app.Presentation
import android.content.Context
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.core.view.doOnAttach
import androidx.core.view.doOnLayout
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/** Provides an attached, visible hierarchy without requiring an Activity or showing export UI. */
internal class PdfEquationRenderHost(context: Context, private val webView: WebView) : AutoCloseable {
    private val reader = ImageReader.newInstance(2048, 1024, PixelFormat.RGBA_8888, 2)
    private val display = try {
        checkNotNull(context.getSystemService(DisplayManager::class.java).createVirtualDisplay(
            "PDF equations", 2048, 1024, context.resources.displayMetrics.densityDpi,
            reader.surface,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION or DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
        )) { "Unable to create the private PDF rendering display" }
    } catch (error: Exception) {
        reader.close()
        throw error
    }
    private val presentation = Presentation(context, display.display, android.R.style.Theme_Material_Light_NoActionBar)

    init {
        try {
            // Drain the unused display output so its producer never blocks on a full buffer queue.
            reader.setOnImageAvailableListener({ source -> source.acquireLatestImage()?.close() }, Handler(Looper.getMainLooper()))
            val content = FrameLayout(presentation.context).apply {
                clipChildren = false
                clipToPadding = false
                addView(webView, FrameLayout.LayoutParams(2048, 1024))
            }
            presentation.setContentView(content)
            presentation.window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            presentation.show()
        } catch (error: Exception) {
            close()
            throw error
        }
    }

    suspend fun awaitReady() {
        val ready = withTimeoutOrNull(5_000L) {
            suspendCancellableCoroutine { continuation ->
                webView.doOnAttach {
                    webView.doOnLayout {
                        if (continuation.isActive) continuation.resume(true)
                    }
                }
            }
        }
        check(ready == true) { "PDF rendering display did not attach in time" }
    }

    override fun close() {
        try {
            (webView.parent as? ViewGroup)?.removeView(webView)
            presentation.dismiss()
        } finally {
            display.release()
            reader.close()
        }
    }
}

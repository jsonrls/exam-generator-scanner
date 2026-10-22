package com.pbec.preboardexamchecker.ui.scanner

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapUtilsAndroidTest {
    @Test
    fun captureRotationPreservesPixelOrientationForEverySensorRotation() {
        val source = Bitmap.createBitmap(2, 3, Bitmap.Config.ARGB_8888)
        source.eraseColor(Color.BLACK)
        source.setPixel(0, 0, Color.RED)
        try {
            assertSame(source, rotateCaptureBitmap(source, 0))
            for ((degrees, location) in listOf(
                90 to (2 to 0), 180 to (1 to 2), 270 to (0 to 1)
            )) {
                val rotated = rotateCaptureBitmap(source, degrees)
                try {
                    assertEquals(if (degrees == 180) 2 else 3, rotated.width)
                    assertEquals(if (degrees == 180) 3 else 2, rotated.height)
                    assertEquals(Color.RED, rotated.getPixel(location.first, location.second))
                } finally {
                    rotated.recycle()
                }
            }
        } finally {
            source.recycle()
        }
    }
}

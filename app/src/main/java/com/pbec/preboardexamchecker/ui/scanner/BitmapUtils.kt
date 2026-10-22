package com.pbec.preboardexamchecker.ui.scanner

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayInputStream

/** CameraX toBitmap leaves sensor orientation intact; apply imageInfo rotation once. */
internal fun rotateCaptureBitmap(src: Bitmap, rotationDegrees: Int): Bitmap {
    if (rotationDegrees == 0) return src
    val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
    return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
}

fun applyExifRotation(jpegBytes: ByteArray, src: Bitmap): Bitmap {
    val exif = ExifInterface(ByteArrayInputStream(jpegBytes))
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )
    val degrees = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90  -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else                                 -> 0f
    }
    if (degrees == 0f) return src
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
}

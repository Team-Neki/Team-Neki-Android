package com.neki.android.core.common.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.URL

private const val DEFAULT_QUALITY = 80

fun Uri.toByteArray(
    context: Context,
    quality: Int = DEFAULT_QUALITY,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
): ByteArray? {
    val orientation = getOrientation(context.contentResolver)

    val bytes = context.contentResolver.openInputStream(this)?.use { it.readBytes() } ?: return null
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
    val rotatedBitmap = bitmap.applyOrientation(orientation)

    return ByteArrayOutputStream().use { outputStream ->
        rotatedBitmap.compress(format, quality, outputStream)
        if (rotatedBitmap !== bitmap) bitmap.recycle()
        rotatedBitmap.recycle()
        outputStream.toByteArray()
    }
}

suspend fun String.urlToByteArray(
    quality: Int = DEFAULT_QUALITY,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
): ByteArray = withContext(Dispatchers.IO) {
    val bytes = URL(this@urlToByteArray).openStream().use { it.readBytes() }
    bytes.compress(quality, format)
}

private fun Bitmap.applyOrientation(orientation: Int): Bitmap {
    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
        ExifInterface.ORIENTATION_TRANSPOSE -> {
            matrix.postRotate(90f)
            matrix.postScale(-1f, 1f)
        }

        ExifInterface.ORIENTATION_TRANSVERSE -> {
            matrix.postRotate(270f)
            matrix.postScale(-1f, 1f)
        }

        else -> return this
    }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

private fun ByteArray.compress(
    quality: Int,
    format: Bitmap.CompressFormat,
): ByteArray {
    val bitmap = BitmapFactory.decodeByteArray(this, 0, this.size)
    return ByteArrayOutputStream().use { outputStream ->
        bitmap.compress(format, quality, outputStream)
        bitmap.recycle()
        outputStream.toByteArray()
    }
}

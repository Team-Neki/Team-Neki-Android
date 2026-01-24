package com.neki.android.core.common.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
    val bytes = context.contentResolver.openInputStream(this)?.use { it.readBytes() } ?: return null
    return bytes.compress(quality, format)
}

suspend fun String.urlToByteArray(
    quality: Int = DEFAULT_QUALITY,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
): ByteArray = withContext(Dispatchers.IO) {
    val bytes = URL(this@urlToByteArray).openStream().use { it.readBytes() }
    bytes.compress(quality, format)
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

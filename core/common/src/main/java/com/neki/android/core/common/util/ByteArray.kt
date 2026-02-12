package com.neki.android.core.common.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.neki.android.core.model.ContentType
import com.neki.android.core.model.MediaMetaData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.URL

private const val DEFAULT_QUALITY = 80

fun Uri.toByteArrayWithSize(
    context: Context,
    quality: Int = DEFAULT_QUALITY,
    contentType: ContentType = ContentType.JPEG,
): MediaMetaData? {
    val resolver = context.contentResolver
    val orientation = getOrientation(context.contentResolver)

    val bytes = resolver.openInputStream(this)?.use { it.readBytes() } ?: return null
    val originalBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
    val rotatedBitmap = originalBitmap.applyOrientation(orientation)

    val byteArray = ByteArrayOutputStream().use { outputStream ->
        rotatedBitmap.compress(contentType.toCompressFormat(), quality, outputStream)

        outputStream.toByteArray()
    } ?: error("Failed to compress bitmap")

    val width = rotatedBitmap.width
    val height = rotatedBitmap.height

    if (rotatedBitmap !== originalBitmap) originalBitmap.recycle()
    rotatedBitmap.recycle()

    return MediaMetaData(
        width = width,
        height = height,
        imageBytes = byteArray,
    )
}

private fun Uri.getOrientation(resolver: ContentResolver) = resolver.openInputStream(this)?.use { input ->
    ExifInterface(input).getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED,
    )
} ?: ExifInterface.ORIENTATION_UNDEFINED

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

suspend fun String.urlToByteArrayWithSize(
    quality: Int = DEFAULT_QUALITY,
    contentType: ContentType = ContentType.JPEG,
): MediaMetaData = withContext(Dispatchers.IO) {
    val bytes = URL(this@urlToByteArrayWithSize).openStream().use { it.readBytes() }

    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
    val width = bounds.outWidth
    val height = bounds.outHeight

    val compressed = bytes.compress(quality, contentType.toCompressFormat())

    MediaMetaData(
        width = width,
        height = height,
        imageBytes = compressed,
    )
}

private fun ContentType.toCompressFormat(): Bitmap.CompressFormat = when (this) {
    ContentType.JPEG -> Bitmap.CompressFormat.JPEG
    ContentType.PNG -> Bitmap.CompressFormat.PNG
}

package com.neki.android.core.common.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

fun Uri.getOrientation(resolver: ContentResolver) = resolver.openInputStream(this)?.use { input ->
    ExifInterface(input).getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED,
    )
} ?: ExifInterface.ORIENTATION_UNDEFINED

fun Uri.getImageSize(context: Context): Pair<Int, Int> {
    val orientation = getOrientation(context.contentResolver)

    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(this)?.use { input ->
        BitmapFactory.decodeStream(input, null, options)
    }

    val isRotated = orientation in listOf(
        ExifInterface.ORIENTATION_ROTATE_90,
        ExifInterface.ORIENTATION_ROTATE_270,
        ExifInterface.ORIENTATION_TRANSPOSE,
        ExifInterface.ORIENTATION_TRANSVERSE,
    )

    return if (isRotated) {
        options.outHeight to options.outWidth
    } else {
        options.outWidth to options.outHeight
    }
}

suspend fun String.getImageSizeFromUrl(): Pair<Int, Int> = withContext(Dispatchers.IO) {
    val bytes = URL(this@getImageSizeFromUrl).openStream().use { it.readBytes() }
    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

    options.outWidth to options.outHeight
}

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

/**
 * Uri에서 이미지를 읽어와 크기 정보와 함께 ByteArray로 변환합니다.
 * 이미지의 방향(Orientation)을 확인하여 올바르게 회전시킵니다.
 *
 * @param context 컨텍스트
 * @param quality 이미지 압축 품질 (0-100, 기본값 80)
 * @param contentType 이미지 포맷 (JPEG, PNG 등)
 * @return 이미지 크기 및 데이터가 포함된 MediaMetaData 객체. 실패 시 null 반환.
 */
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

/**
 * Uri의 EXIF 정보를 통해 이미지의 방향(Orientation)을 가져옵니다.
 *
 * @param resolver ContentResolver
 * @return EXIF Orientation 값 (기본값: ORIENTATION_UNDEFINED)
 */
private fun Uri.getOrientation(resolver: ContentResolver) = resolver.openInputStream(this)?.use { input ->
    ExifInterface(input).getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED,
    )
} ?: ExifInterface.ORIENTATION_UNDEFINED

/**
 * 주어진 Orientation 값에 따라 비트맵을 회전하거나 대칭시킵니다.
 *
 * @param orientation EXIF Orientation 값
 * @return 회전/대칭이 적용된 새로운 비트맵. 변경이 필요 없는 경우 원본 반환.
 */
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

/**
 * ByteArray 형태의 이미지를 지정된 포맷과 품질로 압축합니다.
 *
 * @param quality 이미지 압축 품질 (0-100)
 * @param format 이미지 압축 포맷
 * @return 압축된 이미지 데이터 (ByteArray)
 */
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

/**
 * 이미지 URL에서 데이터를 다운로드하여 크기 정보와 함께 ByteArray로 변환합니다.
 *
 * @param quality 이미지 압축 품질 (0-100, 기본값 80)
 * @param contentType 이미지 포맷
 * @return 이미지 크기 및 데이터가 포함된 MediaMetaData 객체
 */
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

/**
 * ContentType을 Bitmap.CompressFormat으로 변환합니다.
 */
private fun ContentType.toCompressFormat(): Bitmap.CompressFormat = when (this) {
    ContentType.JPEG -> Bitmap.CompressFormat.JPEG
    ContentType.PNG -> Bitmap.CompressFormat.PNG
}

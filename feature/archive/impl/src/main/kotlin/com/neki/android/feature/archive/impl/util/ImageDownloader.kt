package com.neki.android.feature.archive.impl.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object ImageDownloader {

    suspend fun downloadImage(context: Context, imageUrl: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val bitmap = loadBitmapFromUrl(context, imageUrl)
                saveBitmapToGallery(context, bitmap)
            }
        }

    suspend fun downloadImages(context: Context, imageUrls: List<String>): Result<Int> =
        withContext(Dispatchers.IO) {
            var successCount = 0
            imageUrls.forEach { url ->
                downloadImage(context, url).onSuccess { successCount++ }
            }
            if (successCount > 0) Result.success(successCount)
            else Result.failure(IllegalStateException("다운로드에 실패했습니다."))
        }

    private suspend fun loadBitmapFromUrl(context: Context, imageUrl: String): Bitmap {
        val imageLoader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .build()

        val result = imageLoader.execute(request)
        if (result is SuccessResult) {
            return result.image.toBitmap()
        } else {
            error("이미지 로드에 실패했습니다.")
        }
    }

    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap) {
        val filename = "NEKI_${System.currentTimeMillis()}.jpg"

        val outputStream: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Neki")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues,
            ) ?: error("이미지 저장에 실패했습니다.")

            context.contentResolver.openOutputStream(uri)?.also {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                context.contentResolver.update(uri, contentValues, null, null)
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + "/Neki",
            )
            if (!imagesDir.exists()) imagesDir.mkdirs()

            val imageFile = File(imagesDir, filename)
            FileOutputStream(imageFile).also {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
        }

        outputStream?.close()
    }
}

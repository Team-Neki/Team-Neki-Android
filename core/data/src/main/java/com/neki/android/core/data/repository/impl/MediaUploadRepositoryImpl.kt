package com.neki.android.core.data.repository.impl

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.neki.android.core.common.util.getImageSize
import com.neki.android.core.common.util.getImageSizeFromUrl
import com.neki.android.core.common.util.toByteArray
import com.neki.android.core.common.util.urlToByteArray
import com.neki.android.core.data.remote.api.UploadService
import com.neki.android.core.data.remote.model.request.MediaUploadTicketRequest
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.MediaUploadRepository
import com.neki.android.core.model.ContentType
import com.neki.android.core.model.MediaType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaUploadRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val uploadService: UploadService,
) : MediaUploadRepository {

    override suspend fun uploadImageFromUri(
        fileName: String,
        uri: Uri,
        contentType: ContentType,
        mediaType: String,
    ) = runSuspendCatching {
        val imageBytes = withContext(Dispatchers.Default) {
            uri.toByteArray(
                context = context,
                format = contentType.toCompressFormat(),
            ) ?: error("Failed to convert uri to byte array")
        }
        val (width, height) = uri.getImageSize(context)

        val (mediaId, presignedUrl) = getSingleUploadTicket(
            fileName = fileName,
            contentType = contentType.label,
            mediaType = MediaType.USER_PROFILE.name,
            width = width,
            height = height,
        ).getOrThrow()

        uploadService.uploadImage(
            presignedUrl = presignedUrl,
            imageBytes = imageBytes,
            contentType = contentType.label,
        )
        return@runSuspendCatching mediaId
    }

    override suspend fun uploadImageFromUrl(
        imageUrl: String,
        fileName: String,
        contentType: ContentType,
        mediaType: MediaType,
    ): Result<Long> = runSuspendCatching {
        val (width, height) = imageUrl.getImageSizeFromUrl()

        val imageBytes = imageUrl.urlToByteArray(
            format = contentType.toCompressFormat(),
        )

        val (mediaId, presignedUrl) = getSingleUploadTicket(
            fileName = fileName,
            contentType = contentType.label,
            mediaType = MediaType.PHOTO_BOOTH.name,
            width = width,
            height = height,
        ).getOrThrow()

        uploadService.uploadImage(
            presignedUrl = presignedUrl,
            imageBytes = imageBytes,
            contentType = contentType.label,
        )

        return@runSuspendCatching mediaId
    }

    private suspend fun getSingleUploadTicket(
        fileName: String,
        contentType: String,
        mediaType: String,
        width: Int?,
        height: Int?,
    ) = runSuspendCatching {
        uploadService.getUploadTicket(
            requestBody = MediaUploadTicketRequest(
                items = listOf(
                    MediaUploadTicketRequest.Item(
                        filename = fileName,
                        contentType = contentType,
                        mediaType = mediaType,
                        width = width,
                        height = height,
                        size = if (width != null && height != null) (width + height).toLong() else null,
                    ),
                ),
            ),
        ).data.toModels().first()
    }

    private fun ContentType.toCompressFormat(): Bitmap.CompressFormat = when (this) {
        ContentType.JPEG -> Bitmap.CompressFormat.JPEG
        ContentType.PNG -> Bitmap.CompressFormat.PNG
    }
}

package com.neki.android.core.data.repository.impl

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.neki.android.core.common.util.toByteArray
import com.neki.android.core.common.util.urlToByteArray
import com.neki.android.core.data.remote.api.UploadService
import com.neki.android.core.data.remote.model.request.MediaUploadTicketRequest
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.MediaUploadRepository
import com.neki.android.core.model.ContentType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MediaUploadRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val uploadService: UploadService,
) : MediaUploadRepository {
    override suspend fun getSingleUploadTicket(
        fileName: String,
        contentType: String,
        mediaType: String,
    ) = runSuspendCatching {
        uploadService.getUploadTicket(
            requestBody = MediaUploadTicketRequest(
                items = listOf(
                    MediaUploadTicketRequest.Item(
                        filename = fileName,
                        contentType = contentType,
                        mediaType = mediaType,
                    ),
                ),
            ),
        ).data.toModels().first()
    }

    override suspend fun getMultipleUploadTicket(
        uploadCount: Int,
        fileName: String,
        contentType: String,
        mediaType: String,
    ) = runSuspendCatching {
        uploadService.getUploadTicket(
            requestBody = MediaUploadTicketRequest(
                items = List(uploadCount) {
                    MediaUploadTicketRequest.Item(
                        filename = fileName,
                        contentType = contentType,
                        mediaType = mediaType,
                    )
                },
            ),
        ).data.toModels()
    }

    override suspend fun uploadImage(
        uploadUrl: String,
        imageBytes: ByteArray,
        contentType: ContentType,
    ) = runSuspendCatching {
        uploadService.uploadImage(
            presignedUrl = uploadUrl,
            imageBytes = imageBytes,
            contentType = contentType.label,
        )
    }

    override suspend fun uploadImageFromUri(
        uploadUrl: String,
        uri: Uri,
        contentType: ContentType,
    ) = runSuspendCatching {
        val imageBytes = uri.toByteArray(
            context = context,
            format = contentType.toCompressFormat(),
        ) ?: error("Failed to convert uri to byte array")

        uploadService.uploadImage(
            presignedUrl = uploadUrl,
            imageBytes = imageBytes,
            contentType = contentType.label,
        )
    }

    override suspend fun uploadImageFromUrl(
        uploadUrl: String,
        imageUrl: String,
        contentType: ContentType,
    ) = runSuspendCatching {
        val imageBytes = imageUrl.urlToByteArray(
            format = contentType.toCompressFormat(),
        )

        uploadService.uploadImage(
            presignedUrl = uploadUrl,
            imageBytes = imageBytes,
            contentType = contentType.label,
        )
    }

    private fun ContentType.toCompressFormat(): Bitmap.CompressFormat = when (this) {
        ContentType.JPEG -> Bitmap.CompressFormat.JPEG
        ContentType.PNG -> Bitmap.CompressFormat.PNG
    }
}

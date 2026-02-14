package com.neki.android.core.data.repository.impl

import android.content.Context
import android.net.Uri
import com.neki.android.core.common.util.toByteArrayWithSize
import com.neki.android.core.common.util.urlToByteArrayWithSize
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
        mediaType: MediaType,
    ) = runSuspendCatching {
        val metadata = withContext(Dispatchers.Default) {
            uri.toByteArrayWithSize(context = context, contentType = contentType)
        } ?: error("Failed to convert uri to byte array")

        val (mediaId, presignedUrl) = getSingleUploadTicket(
            fileName = fileName,
            contentType = contentType.label,
            mediaType = mediaType.label,
            width = metadata.width,
            height = metadata.height,
            fileSize = metadata.imageBytes.size.toLong(),
        ).getOrThrow()

        uploadService.uploadImage(
            presignedUrl = presignedUrl,
            imageBytes = metadata.imageBytes,
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
        val metaData = imageUrl.urlToByteArrayWithSize(contentType = contentType)

        val (mediaId, presignedUrl) = getSingleUploadTicket(
            fileName = fileName,
            contentType = contentType.label,
            mediaType = mediaType.label,
            width = metaData.width,
            height = metaData.height,
            fileSize = metaData.imageBytes.size.toLong(),
        ).getOrThrow()

        uploadService.uploadImage(
            presignedUrl = presignedUrl,
            imageBytes = metaData.imageBytes,
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
        fileSize: Long?,
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
                        size = fileSize,
                    ),
                ),
            ),
        ).data.toModels().first()
    }
}

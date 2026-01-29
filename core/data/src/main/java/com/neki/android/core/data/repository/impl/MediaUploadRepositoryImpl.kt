package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.remote.api.UploadService
import com.neki.android.core.data.remote.model.request.MediaUploadTicketRequest
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.MediaUploadRepository
import com.neki.android.core.model.ContentType
import javax.inject.Inject

class MediaUploadRepositoryImpl @Inject constructor(
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
}

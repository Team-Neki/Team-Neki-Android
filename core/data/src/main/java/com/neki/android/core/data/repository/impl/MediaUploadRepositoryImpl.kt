package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.remote.api.ApiService
import com.neki.android.core.data.remote.api.UploadService
import com.neki.android.core.data.remote.model.request.MediaUploadTicketRequest
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.MediaUploadRepository
import com.neki.android.core.model.ContentType
import java.io.File
import javax.inject.Inject

class MediaUploadRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val uploadService: UploadService,
) : MediaUploadRepository {
    override suspend fun getUploadTicket(
        fileName: String,
        contentType: String,
        mediaType: String,
    ) = runSuspendCatching {
        apiService.getUploadTicket(
            requestBody = MediaUploadTicketRequest(
                filename = fileName,
                contentType = contentType,
                mediaType = mediaType,
            ),
        ).data.toPair()
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

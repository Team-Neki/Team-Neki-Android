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
        contentType: String,
        mediaType: String,
    ) = runSuspendCatching {
        apiService.getUploadTicket(
            requestBody = MediaUploadTicketRequest(
                contentType = contentType,
                mediaType = mediaType,
            ),
        ).data.toPair()
    }

    override suspend fun uploadImage(
        fileName: String,
        uploadUrl: String,
        file: File,
        contentType: ContentType,
    ) = runSuspendCatching {
        uploadService.uploadImage(
            fileName = fileName,
            presignedUrl = uploadUrl,
            imageFile = file,
            contentType = contentType,
        )
    }
}

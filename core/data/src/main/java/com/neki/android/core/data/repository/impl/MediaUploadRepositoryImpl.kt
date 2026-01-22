package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.remote.api.ApiService
import com.neki.android.core.data.remote.model.request.MediaUploadTicketRequest
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.MediaUploadRepository
import javax.inject.Inject

class MediaUploadRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
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
}

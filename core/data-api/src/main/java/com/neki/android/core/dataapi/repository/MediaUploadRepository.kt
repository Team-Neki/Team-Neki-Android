package com.neki.android.core.dataapi.repository

interface MediaUploadRepository {
    suspend fun getUploadTicket(
        contentType: String,
        mediaType: String,
    ): Result<Pair<Long, String>>
}

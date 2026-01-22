package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.ContentType

interface MediaUploadRepository {
    suspend fun getUploadTicket(
        fileName: String,
        contentType: String,
        mediaType: String,
    ): Result<Pair<Long, String>>

    suspend fun uploadImage(
        uploadUrl: String,
        imageBytes: ByteArray,
        contentType: ContentType,
    ): Result<Unit>
}

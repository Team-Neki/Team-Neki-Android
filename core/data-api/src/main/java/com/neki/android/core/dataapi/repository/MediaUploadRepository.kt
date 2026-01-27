package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.ContentType
import com.neki.android.core.model.MediaUploadTicket

interface MediaUploadRepository {
    suspend fun getUploadTicket(
        uploadCount: Int = 1,
        fileName: String,
        contentType: String,
        mediaType: String,
    ): Result<List<MediaUploadTicket>>

    suspend fun uploadImage(
        uploadUrl: String,
        imageBytes: ByteArray,
        contentType: ContentType,
    ): Result<Unit>
}

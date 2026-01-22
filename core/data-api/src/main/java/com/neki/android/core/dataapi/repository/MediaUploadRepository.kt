package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.ContentType
import java.io.File

interface MediaUploadRepository {
    suspend fun getUploadTicket(
        contentType: String,
        mediaType: String,
    ): Result<Pair<Long, String>>

    suspend fun uploadImage(
        fileName: String,
        uploadUrl: String,
        file: File,
        contentType: ContentType,
    ): Result<Unit>
}

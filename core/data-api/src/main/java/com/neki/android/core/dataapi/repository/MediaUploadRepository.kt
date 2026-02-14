package com.neki.android.core.dataapi.repository

import android.net.Uri
import com.neki.android.core.model.ContentType
import com.neki.android.core.model.MediaType

interface MediaUploadRepository {
    suspend fun uploadImageFromUri(
        fileName: String,
        uri: Uri,
        contentType: ContentType,
        mediaType: MediaType,
    ): Result<Long>

    suspend fun uploadImageFromUrl(
        imageUrl: String,
        fileName: String,
        contentType: ContentType,
        mediaType: MediaType,
    ): Result<Long>
}

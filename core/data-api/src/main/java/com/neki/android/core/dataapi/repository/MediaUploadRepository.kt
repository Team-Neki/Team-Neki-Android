package com.neki.android.core.dataapi.repository

import android.net.Uri
import com.neki.android.core.model.ContentType
import com.neki.android.core.model.MediaUploadTicket

interface MediaUploadRepository {
    suspend fun getSingleUploadTicket(
        fileName: String,
        contentType: String,
        mediaType: String,
    ): Result<MediaUploadTicket>

    suspend fun getMultipleUploadTicket(
        uploadCount: Int,
        fileName: String,
        contentType: String,
        mediaType: String,
    ): Result<List<MediaUploadTicket>>

    suspend fun uploadImage(
        uploadUrl: String,
        imageBytes: ByteArray,
        contentType: ContentType,
    ): Result<Unit>

    suspend fun uploadImageFromUri(
        uploadUrl: String,
        uri: Uri,
        contentType: ContentType,
    ): Result<Unit>

    suspend fun uploadImageFromUrl(
        uploadUrl: String,
        imageUrl: String,
        contentType: ContentType,
    ): Result<Unit>
}

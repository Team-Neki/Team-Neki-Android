package com.neki.android.core.dataapi.repository

interface PhotoRepository {
    suspend fun registerPhoto(
        mediaId: Long,
        folderId: Long,
    ): Result<Long>
}

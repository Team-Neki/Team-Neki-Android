package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.Photo

interface PhotoRepository {
    suspend fun getPhotos(
        folderId: Long? = null,
        page: Int = 0,
        size: Int = 20,
    ): Result<List<Photo>>

    suspend fun registerPhoto(
        mediaId: Long,
        folderId: Long? = null,
    ): Result<Long>
}

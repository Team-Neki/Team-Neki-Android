package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.FavoriteSummary
import com.neki.android.core.model.Photo
import com.neki.android.core.model.SortOrder

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

    suspend fun deletePhoto(photoId: Long): Result<Unit>

    suspend fun updateFavorite(photoId: Long, favorite: Boolean): Result<Unit>

    suspend fun getFavoritePhotos(
        page: Int = 0,
        size: Int = 20,
        sortOrder: SortOrder = SortOrder.DESC,
    ): Result<List<Photo>>

    suspend fun getFavoriteSummary(): Result<FavoriteSummary>
}

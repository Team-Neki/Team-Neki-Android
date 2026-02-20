package com.neki.android.core.dataapi.repository

import androidx.paging.PagingData
import com.neki.android.core.model.AlbumPreview
import com.neki.android.core.model.Photo
import com.neki.android.core.model.SortOrder
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    suspend fun getPhotos(
        folderId: Long? = null,
        page: Int = 0,
        size: Int = 20,
    ): Result<List<Photo>>

    suspend fun registerPhoto(
        mediaIds: List<Long>,
        folderId: Long? = null,
        favorite: Boolean = false,
    ): Result<Unit>

    suspend fun deletePhoto(photoId: Long): Result<Unit>
    suspend fun deletePhoto(photoIds: List<Long>): Result<Unit>

    suspend fun updateFavorite(photoId: Long, favorite: Boolean): Result<Unit>

    suspend fun getFavoritePhotos(
        page: Int = 0,
        size: Int = 20,
        sortOrder: SortOrder = SortOrder.DESC,
    ): Result<List<Photo>>

    suspend fun getFavoriteSummary(): Result<AlbumPreview>

    fun getPhotosFlow(
        folderId: Long? = null,
        sortOrder: SortOrder = SortOrder.DESC,
    ): Flow<PagingData<Photo>>

    fun getFavoritePhotosFlow(
        sortOrder: SortOrder = SortOrder.DESC,
    ): Flow<PagingData<Photo>>
}

package com.neki.android.core.data.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.neki.android.core.data.paging.FavoritePhotoPagingSource
import com.neki.android.core.data.paging.PhotoPagingSource
import com.neki.android.core.data.remote.api.PhotoService
import com.neki.android.core.data.remote.model.request.DeletePhotoRequest
import com.neki.android.core.data.remote.model.request.RegisterPhotoRequest
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.model.AlbumPreview
import com.neki.android.core.model.Photo
import com.neki.android.core.model.SortOrder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val PAGE_SIZE = 20
private const val PREFETCH_DISTANCE = 10

class PhotoRepositoryImpl @Inject constructor(
    private val photoService: PhotoService,
) : PhotoRepository {
    override suspend fun getPhotos(
        folderId: Long?,
        page: Int,
        size: Int,
    ): Result<List<Photo>> = runSuspendCatching {
        photoService.getPhotos(
            folderId = folderId,
            page = page,
            size = size,
        ).data.toModels()
    }

    override suspend fun registerPhoto(
        mediaIds: List<Long>,
        folderId: Long?,
    ): Result<Unit> = runSuspendCatching {
        photoService.registerPhoto(
            requestBody = RegisterPhotoRequest(
                folderId = folderId,
                uploads = mediaIds.map { RegisterPhotoRequest.Upload(mediaId = it) },
            ),
        ).data
    }

    override suspend fun deletePhoto(photoId: Long): Result<Unit> = runSuspendCatching {
        photoService.deletePhoto(
            requestBody = DeletePhotoRequest(photoIds = listOf(photoId)),
        ).data
    }

    override suspend fun deletePhoto(photoIds: List<Long>): Result<Unit> = runSuspendCatching {
        photoService.deletePhoto(
            requestBody = DeletePhotoRequest(photoIds = photoIds),
        ).data
    }

    override suspend fun updateFavorite(photoId: Long, favorite: Boolean): Result<Unit> = runSuspendCatching {
        photoService.updateFavorite(photoId, favorite).data
    }

    override suspend fun getFavoritePhotos(
        page: Int,
        size: Int,
        sortOrder: SortOrder,
    ): Result<List<Photo>> = runSuspendCatching {
        photoService.getFavoritePhotos(page, size, sortOrder.name).data.toModels()
    }

    override suspend fun getFavoriteSummary(): Result<AlbumPreview> = runSuspendCatching {
        photoService.getFavoriteSummary().data.toModel()
    }

    override fun getPhotosFlow(folderId: Long?, sortOrder: SortOrder): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { PhotoPagingSource(photoService, folderId, sortOrder.name) },
        ).flow
    }

    override fun getFavoritePhotosFlow(sortOrder: SortOrder): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { FavoritePhotoPagingSource(photoService, sortOrder) },
        ).flow
    }
}

package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.remote.api.PhotoService
import com.neki.android.core.data.remote.model.request.RegisterPhotoRequest
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.model.AlbumPreview
import com.neki.android.core.model.Photo
import com.neki.android.core.model.SortOrder
import javax.inject.Inject

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
        mediaId: Long,
        folderId: Long?,
    ): Result<Long> = runSuspendCatching {
        photoService.registerPhoto(
            requestBody = RegisterPhotoRequest(
                mediaId = mediaId,
                folderId = folderId,
            ),
        ).data.photoId
    }

    override suspend fun deletePhoto(photoId: Long): Result<Unit> = runSuspendCatching {
        photoService.deletePhoto(photoId).data
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
}

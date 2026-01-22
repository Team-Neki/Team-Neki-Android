package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.remote.api.PhotoService
import com.neki.android.core.data.remote.model.request.RegisterPhotoRequest
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.PhotoRepository
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val photoService: PhotoService,
) : PhotoRepository {
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
}

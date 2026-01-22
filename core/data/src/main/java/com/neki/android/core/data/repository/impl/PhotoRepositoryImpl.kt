package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.remote.api.ApiService
import com.neki.android.core.data.remote.model.request.RegisterPhotoRequest
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.PhotoRepository
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : PhotoRepository {
    override suspend fun registerPhoto(
        mediaId: Long,
        folderId: Long,
    ): Result<Long> = runSuspendCatching {
        apiService.registerPhoto(
            requestBody = RegisterPhotoRequest(
                mediaId = mediaId,
                folderId = folderId,
            ),
        ).data
    }
}

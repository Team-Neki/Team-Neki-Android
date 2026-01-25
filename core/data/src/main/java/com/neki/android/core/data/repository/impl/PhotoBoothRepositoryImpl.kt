package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.remote.api.PhotoBoothService
import com.neki.android.core.data.remote.model.request.PhotoBoothPointRequest
import com.neki.android.core.data.remote.model.response.toModels
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.PhotoBoothRepository
import com.neki.android.core.model.Brand
import com.neki.android.core.model.BrandInfo
import javax.inject.Inject

class PhotoBoothRepositoryImpl @Inject constructor(
    private val photoBoothService: PhotoBoothService,
) : PhotoBoothRepository {
    override suspend fun getBrands(): Result<List<Brand>> = runSuspendCatching {
        photoBoothService.getBrands().data.toModels()
    }

    override suspend fun getPhotoBoothsByPoint(
        longitude: Double,
        latitude: Double,
        radiusInMeters: Int,
        brandIds: List<Long>,
    ): Result<List<BrandInfo>> = runSuspendCatching {
        photoBoothService.getPhotoBoothsByPoint(
            request = PhotoBoothPointRequest(
                longitude = longitude,
                latitude = latitude,
                radiusInMeters = radiusInMeters,
                brandIds = brandIds,
            ),
        ).data.toModels()
    }
}

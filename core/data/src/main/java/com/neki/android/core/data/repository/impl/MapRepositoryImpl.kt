package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.remote.api.MapService
import com.neki.android.core.data.remote.model.request.Coordinate
import com.neki.android.core.data.remote.model.request.PhotoBoothPointRequest
import com.neki.android.core.data.remote.model.request.PhotoBoothPolygonRequest
import com.neki.android.core.data.remote.model.response.toModels
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.MapRepository
import com.neki.android.core.model.Brand
import com.neki.android.core.model.PhotoBooth
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(
    private val mapService: MapService,
) : MapRepository {
    override suspend fun getBrands(): Result<List<Brand>> = runSuspendCatching {
        mapService.getBrands().data.toModels()
    }

    override suspend fun getPhotoBoothsByPoint(
        longitude: Double,
        latitude: Double,
        radiusInMeters: Int,
        brandIds: List<Long>,
    ): Result<List<PhotoBooth>> = runSuspendCatching {
        mapService.getPhotoBoothsByPoint(
            request = PhotoBoothPointRequest(
                longitude = longitude,
                latitude = latitude,
                radiusInMeters = radiusInMeters,
                brandIds = brandIds,
            ),
        ).data.toModels()
    }

    override suspend fun getPhotoBoothsByPolygon(
        coordinates: List<Pair<Double, Double>>,
        brandIds: List<Long>,
    ): Result<List<PhotoBooth>> = runSuspendCatching {
        mapService.getPhotoBoothsByPolygon(
            request = PhotoBoothPolygonRequest(
                coordinates = coordinates.map { (longitude, latitude) ->
                    Coordinate(longitude = longitude, latitude = latitude)
                },
                brandIds = brandIds,
            ),
        ).data.toModels()
    }
}

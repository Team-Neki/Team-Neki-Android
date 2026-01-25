package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.Brand
import com.neki.android.core.model.BrandInfo

interface MapRepository {
    suspend fun getBrands(): Result<List<Brand>>

    suspend fun getPhotoBoothsByPoint(
        longitude: Double,
        latitude: Double,
        radiusInMeters: Int,
        brandIds: List<Long>,
    ): Result<List<BrandInfo>>
}

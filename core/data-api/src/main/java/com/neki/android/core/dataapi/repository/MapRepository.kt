package com.neki.android.core.dataapi.repository

import androidx.paging.PagingData
import com.neki.android.core.model.Brand
import com.neki.android.core.model.PhotoBooth
import com.neki.android.core.model.SearchBrand
import com.neki.android.core.model.SearchCompletion
import com.neki.android.core.model.SearchFilterGroup
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    suspend fun getBrands(): Result<List<Brand>>

    suspend fun getPhotoBoothsByPoint(
        longitude: Double?,
        latitude: Double?,
        radiusInMeters: Int,
        brandIds: List<Long>,
    ): Result<List<PhotoBooth>>

    suspend fun getPhotoBoothsByPolygon(
        coordinates: List<Pair<Double, Double>>,
        brandIds: List<Long>,
    ): Result<List<PhotoBooth>>

    suspend fun saveBrandOrder(brandIds: List<Long>): Result<Unit>

    suspend fun getFavoritePhotoBooths(): Result<List<PhotoBooth>>

    suspend fun updatePhotoBoothFavorite(locationId: String, favorite: Boolean): Result<Unit>

    fun getSearchCompletionsFlow(
        keyword: String,
        latitude: Double?,
        longitude: Double?,
        onSearchSuccess: (isInitial: Boolean, resultCount: Int, hasNextPage: Boolean) -> Unit,
    ): Flow<PagingData<SearchCompletion>>

    suspend fun getPhotoBoothsByKeyword(
        keyword: String,
        latitude: Double?,
        longitude: Double?,
        filterGroup: SearchFilterGroup = SearchFilterGroup(),
    ): Result<List<PhotoBooth>>

    suspend fun getSearchBrandFilters(keyword: String, filterGroup: SearchFilterGroup = SearchFilterGroup()): Result<List<SearchBrand>>
}

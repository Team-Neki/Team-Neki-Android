package com.neki.android.core.data.remote.api

import com.neki.android.core.data.remote.model.request.BrandOrderChangeRequest
import com.neki.android.core.data.remote.model.request.PhotoBoothPointRequest
import com.neki.android.core.data.remote.model.request.PhotoBoothPolygonRequest
import com.neki.android.core.data.remote.model.request.SearchRequest
import com.neki.android.core.data.remote.model.request.UpdateFavoriteRequest
import com.neki.android.core.data.remote.model.response.BasicNullableResponse
import com.neki.android.core.data.remote.model.response.BasicResponse
import com.neki.android.core.data.remote.model.response.BrandResponse
import com.neki.android.core.data.remote.model.response.PhotoBoothResponse
import com.neki.android.core.data.remote.model.response.SearchCompletionResponse
import com.neki.android.core.data.remote.model.response.SearchFiltersResponse
import com.neki.android.core.data.remote.model.response.SearchPhotoBoothsResponse
import com.neki.android.core.model.SearchCompletionType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import javax.inject.Inject

class MapService @Inject constructor(
    private val client: HttpClient,
) {
    suspend fun getBrands(): BasicResponse<List<BrandResponse>> {
        return client.get("/api/photo-booths/brand").body()
    }

    suspend fun getPhotoBoothsByPoint(
        request: PhotoBoothPointRequest,
    ): BasicResponse<PhotoBoothResponse> {
        return client.post("/api/v2/photo-booths/point") {
            setBody(request)
        }.body()
    }

    suspend fun getPhotoBoothsByPolygon(
        request: PhotoBoothPolygonRequest,
    ): BasicResponse<PhotoBoothResponse> {
        return client.post("/api/v2/photo-booths/polygon") {
            setBody(request)
        }.body()
    }

    suspend fun saveBrandOrder(
        request: BrandOrderChangeRequest,
    ): BasicNullableResponse<Unit> {
        return client.put("/api/photo-booths/brand/order") {
            setBody(request)
        }.body()
    }

    suspend fun getFavoritePhotoBooths(): BasicResponse<PhotoBoothResponse> {
        return client.get("/api/v2/photo-booths/favorite").body()
    }

    suspend fun updatePhotoBoothFavorite(
        locationId: String,
        request: UpdateFavoriteRequest,
    ): BasicNullableResponse<Unit> {
        return client.patch("/api/v2/photo-booths/$locationId/favorite") {
            setBody(request)
        }.body()
    }

    suspend fun getSearchCompletions(
        type: SearchCompletionType,
        keyword: String,
        page: Int,
        size: Int,
        latitude: Double?,
        longitude: Double?,
    ): BasicResponse<SearchCompletionResponse> = client.get("/api/search/completion/${type.path}") {
        parameter("keyword", keyword)
        parameter("page", page)
        parameter("size", size)
        if (type != SearchCompletionType.REGION && latitude != null && longitude != null) {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
        }
    }.body()

    suspend fun getPhotoBoothsByKeyword(request: SearchRequest): BasicResponse<SearchPhotoBoothsResponse> =
        client.post("/api/search/photo-booths") {
            setBody(request)
        }.body()

    suspend fun getSearchBrandFilters(request: SearchRequest): BasicResponse<SearchFiltersResponse> =
        client.post("/api/search/filter") {
            setBody(request)
        }.body()
}

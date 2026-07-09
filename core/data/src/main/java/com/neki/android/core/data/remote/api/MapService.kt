package com.neki.android.core.data.remote.api

import com.neki.android.core.data.remote.model.request.BrandOrderChangeRequest
import com.neki.android.core.data.remote.model.request.PhotoBoothPointRequest
import com.neki.android.core.data.remote.model.request.PhotoBoothPolygonRequest
import com.neki.android.core.data.remote.model.request.UpdateFavoriteRequest
import com.neki.android.core.data.remote.model.response.BasicNullableResponse
import com.neki.android.core.data.remote.model.response.BasicResponse
import com.neki.android.core.data.remote.model.response.BrandResponse
import com.neki.android.core.data.remote.model.response.PhotoBoothResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
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
        return client.post("/api/photo-booths/point") {
            setBody(request)
        }.body()
    }

    suspend fun getPhotoBoothsByPolygon(
        request: PhotoBoothPolygonRequest,
    ): BasicResponse<PhotoBoothResponse> {
        return client.post("/api/photo-booths/polygon") {
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
        return client.get("/api/photo-booths/favorite").body()
    }

    suspend fun updatePhotoBoothFavorite(
        locationId: Long,
        request: UpdateFavoriteRequest,
    ): BasicNullableResponse<Unit> {
        return client.patch("/api/photo-booths/$locationId/favorite") {
            setBody(request)
        }.body()
    }
}

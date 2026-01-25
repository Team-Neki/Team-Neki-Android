package com.neki.android.core.data.remote.api

import com.neki.android.core.data.remote.model.response.BasicResponse
import com.neki.android.core.data.remote.model.response.BrandResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class PhotoBoothService @Inject constructor(
    private val client: HttpClient,
) {
    suspend fun getBrands(): BasicResponse<List<BrandResponse>> {
        return client.get("/api/photo-booths/brand").body()
    }
}

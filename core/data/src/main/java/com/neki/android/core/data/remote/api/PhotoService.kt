package com.neki.android.core.data.remote.api

import com.neki.android.core.data.remote.model.request.RegisterPhotoRequest
import com.neki.android.core.data.remote.model.response.BasicResponse
import com.neki.android.core.data.remote.model.response.RegisterPhotoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import javax.inject.Inject

class PhotoService @Inject constructor(
    private val client: HttpClient,
) {
    // 사진 등록
    suspend fun registerPhoto(requestBody: RegisterPhotoRequest): BasicResponse<RegisterPhotoResponse> {
        return client.post("/api/photos") { setBody(requestBody) }.body()
    }
}

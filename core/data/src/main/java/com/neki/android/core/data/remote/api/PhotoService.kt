package com.neki.android.core.data.remote.api

import com.neki.android.core.data.remote.model.request.RegisterPhotoRequest
import com.neki.android.core.data.remote.model.response.BasicResponse
import com.neki.android.core.data.remote.model.response.PhotoResponse
import com.neki.android.core.data.remote.model.response.RegisterPhotoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import javax.inject.Inject

class PhotoService @Inject constructor(
    private val client: HttpClient,
) {
    // 사진 조회
    suspend fun getPhotos(
        folderId: Long? = null,
        page: Int = 0,
        size: Int = 20,
    ): BasicResponse<PhotoResponse> {
        return client.get("/api/photos") {
            parameter("folderId", folderId)
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

    // 사진 등록
    suspend fun registerPhoto(requestBody: RegisterPhotoRequest): BasicResponse<RegisterPhotoResponse> {
        return client.post("/api/photos") { setBody(requestBody) }.body()
    }

    // 사진 삭제
    suspend fun deletePhoto(photoId: Long): BasicResponse<Unit> {
        return client.delete("/api/photos/$photoId").body()
    }
}

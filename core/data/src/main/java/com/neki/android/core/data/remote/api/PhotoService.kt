package com.neki.android.core.data.remote.api

import com.neki.android.core.data.remote.model.request.DeletePhotoRequest
import com.neki.android.core.data.remote.model.request.RegisterPhotoRequest
import com.neki.android.core.data.remote.model.request.UpdateFavoriteRequest
import com.neki.android.core.data.remote.model.response.BasicNullableResponse
import com.neki.android.core.data.remote.model.response.BasicResponse
import com.neki.android.core.data.remote.model.response.FavoritePhotoResponse
import com.neki.android.core.data.remote.model.response.FavoriteSummaryResponse
import com.neki.android.core.data.remote.model.response.PhotoResponse
import com.neki.android.core.data.remote.model.response.RegisterPhotoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
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
        sortOrder: String = "DESC",
    ): BasicResponse<PhotoResponse> {
        return client.get("/api/photos") {
            parameter("folderId", folderId)
            parameter("page", page)
            parameter("size", size)
            parameter("sortOrder", sortOrder)
        }.body()
    }

    // 사진 등록
    suspend fun registerPhoto(requestBody: RegisterPhotoRequest): BasicNullableResponse<RegisterPhotoResponse> {
        return client.post("/api/photos") { setBody(requestBody) }.body()
    }

    // 사진 삭제
    suspend fun deletePhoto(requestBody: DeletePhotoRequest): BasicNullableResponse<Unit> {
        return client.delete("/api/photos") { setBody(requestBody) }.body()
    }

    // 즐겨찾기 업데이트
    suspend fun updateFavorite(photoId: Long, favorite: Boolean): BasicNullableResponse<Unit> {
        return client.patch("/api/photos/$photoId/favorite") {
            setBody(UpdateFavoriteRequest(favorite))
        }.body()
    }

    // 즐겨찾는 앨범 조회
    suspend fun getFavoritePhotos(
        page: Int = 0,
        size: Int = 20,
        sortOrder: String,
    ): BasicResponse<FavoritePhotoResponse> {
        return client.get("/api/photos/favorite") {
            parameter("page", page)
            parameter("size", size)
            parameter("sortOrder", sortOrder)
        }.body()
    }

    // 즐겨찾기 요약 조회
    suspend fun getFavoriteSummary(): BasicResponse<FavoriteSummaryResponse> {
        return client.get("/api/photos/favorite/summary").body()
    }
}

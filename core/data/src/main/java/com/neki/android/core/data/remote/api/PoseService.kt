package com.neki.android.core.data.remote.api

import com.neki.android.core.data.remote.model.request.UpdateScrapRequest
import com.neki.android.core.data.remote.model.response.BasicNullableResponse
import com.neki.android.core.data.remote.model.response.BasicResponse
import com.neki.android.core.data.remote.model.response.PoseDetailResponse
import com.neki.android.core.data.remote.model.response.PoseResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import javax.inject.Inject

class PoseService @Inject constructor(
    private val client: HttpClient,
) {
    // 포즈 목록 조회
    suspend fun getPoses(
        page: Int = 0,
        size: Int = 20,
        headCount: String? = null,
        sortOrder: String = "DESC",
    ): BasicResponse<PoseResponse> {
        return client.get("/api/poses") {
            parameter("page", page)
            parameter("size", size)
            parameter("headCount", headCount)
            parameter("sortOrder", sortOrder)
        }.body()
    }

    // 포즈 상세 조회
    suspend fun getPose(poseId: Long): BasicResponse<PoseDetailResponse> {
        return client.get("/api/poses/$poseId").body()
    }

    // 랜덤 포즈 조회
    suspend fun getRandomPose(headCount: String, excludeIds: String): BasicResponse<PoseDetailResponse> {
        return client.get("/api/poses/random") {
            parameter("headCount", headCount)
            parameter("excludeIds", excludeIds)
        }.body()
    }

    // 북마크된 포즈 목록 조회
    suspend fun getScrappedPoses(
        page: Int = 0,
        size: Int = 20,
        sortOrder: String = "DESC",
    ): BasicResponse<PoseResponse> {
        return client.get("/api/poses/scrap") {
            parameter("page", page)
            parameter("size", size)
            parameter("sortOrder", sortOrder)
        }.body()
    }

    // 북마크 업데이트
    suspend fun updateScrap(poseId: Long, scrap: Boolean): BasicNullableResponse<Unit> {
        return client.patch("/api/poses/$poseId/scrap") {
            setBody(UpdateScrapRequest(scrap))
        }.body()
    }
}

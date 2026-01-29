package com.neki.android.core.data.remote.api

import com.neki.android.core.data.remote.model.response.BasicResponse
import com.neki.android.core.data.remote.model.response.PoseItemResponse
import com.neki.android.core.data.remote.model.response.PoseResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
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
    suspend fun getPose(poseId: Long): BasicResponse<PoseItemResponse> {
        return client.get("/api/poses/$poseId").body()
    }

    // 랜덤 포즈 조회
    suspend fun getRandomPose(): BasicResponse<PoseItemResponse> {
        return client.get("/api/poses/random").body()
    }
}

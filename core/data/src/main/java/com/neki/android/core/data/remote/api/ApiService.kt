package com.neki.android.core.data.remote.api

import com.neki.android.core.data.remote.model.request.MediaUploadTicketRequest
import com.neki.android.core.data.remote.model.request.KakaoLoginRequest
import com.neki.android.core.data.remote.model.request.RefreshTokenRequest
import com.neki.android.core.data.remote.model.response.BasicResponse
import com.neki.android.core.data.remote.model.response.AuthResponse
import com.neki.android.core.data.remote.model.response.MediaUploadTicketResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class ApiService(
    private val client: HttpClient,
) {
    // 카카오 로그인
    suspend fun loginWithKakao(requestBody: KakaoLoginRequest): BasicResponse<AuthResponse> {
        return client.post("/api/auth/kakao/login") { setBody(requestBody) }.body()
    }

    // AccessToken 갱신
    suspend fun updateAccessToken(requestBody: RefreshTokenRequest): BasicResponse<AuthResponse> {
        return client.post("/api/auth/refresh") { setBody(requestBody) }.body()
    }

    // Media Upload Ticket 받기
    suspend fun getUploadTicket(requestBody: MediaUploadTicketRequest): BasicResponse<MediaUploadTicketResponse> {
        return client.post("/api/media/upload") { setBody(requestBody) }.body()
    }
}

package com.neki.android.core.data.remote.api

import com.neki.android.core.data.remote.model.request.UpdateUserInfoRequest
import com.neki.android.core.data.remote.model.response.BasicNullableResponse
import com.neki.android.core.data.remote.model.response.BasicResponse
import com.neki.android.core.data.remote.model.response.UserInfoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import javax.inject.Inject

class UserService @Inject constructor(
    private val client: HttpClient,
) {
    // 사용자 정보 조회
    suspend fun getUserInfo(): BasicResponse<UserInfoResponse> {
        return client.get("/api/users/info").body()
    }

    // 사용자 프로필 정보 변경(닉네임)
    suspend fun updateUserInfo(request: UpdateUserInfoRequest): BasicNullableResponse<Unit> {
        return client.patch("/api/users/me") { setBody(request) }.body()
    }
}

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
    suspend fun getUserInfo(): BasicResponse<UserInfoResponse> {
        return client.get("/api/users/info").body()
    }

    suspend fun updateUserInfo(request: UpdateUserInfoRequest): BasicNullableResponse<Unit> {
        return client.patch("/api/users/me") { setBody(request) }.body()
    }
}

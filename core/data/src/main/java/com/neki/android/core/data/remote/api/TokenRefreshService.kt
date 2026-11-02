package com.neki.android.core.data.remote.api

import com.neki.android.core.data.remote.model.request.RefreshTokenRequest
import com.neki.android.core.data.remote.model.response.AuthResponse
import com.neki.android.core.data.remote.model.response.BasicResponse
import com.neki.android.core.data.remote.qualifier.ClientLogHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

internal class TokenRefreshService @Inject constructor(@ClientLogHttpClient private val client: HttpClient) {
    suspend fun refresh(refreshToken: String): BasicResponse<AuthResponse> = client.post("/api/auth/refresh") {
        contentType(ContentType.Application.Json)
        setBody(RefreshTokenRequest(refreshToken))
    }.body()
}

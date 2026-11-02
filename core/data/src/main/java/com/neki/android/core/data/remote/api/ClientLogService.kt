package com.neki.android.core.data.remote.api

import com.neki.android.core.data.remote.model.request.ClientLogRequest
import com.neki.android.core.data.remote.model.response.BasicNullableResponse
import com.neki.android.core.data.remote.qualifier.ClientLogHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import javax.inject.Inject

internal class ClientLogService @Inject constructor(
    @ClientLogHttpClient private val client: HttpClient,
) {
    suspend fun send(request: ClientLogRequest, accessToken: String?): BasicNullableResponse<Unit> {
        val response = client.post("/api/logs") {
            contentType(ContentType.Application.Json)
            accessToken?.let { bearerAuth(it) }
            setBody(request)
        }
        check(response.status.value == 200) { "Unexpected client log status" }
        return response.body()
    }
}

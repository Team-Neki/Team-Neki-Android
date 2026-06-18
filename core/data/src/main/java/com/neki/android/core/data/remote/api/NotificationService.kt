package com.neki.android.core.data.remote.api

import com.neki.android.core.data.remote.model.request.UpdateNotificationRequest
import com.neki.android.core.data.remote.model.response.BasicNullableResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import javax.inject.Inject

class NotificationService @Inject constructor(
    private val client: HttpClient,
) {
    suspend fun updateNotification(request: UpdateNotificationRequest): BasicNullableResponse<Unit> =
        client.patch("/api/notifications") { setBody(request) }.body()
}

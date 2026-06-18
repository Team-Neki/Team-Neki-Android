package com.neki.android.core.dataapi.repository

interface NotificationRepository {
    suspend fun savePushToken(token: String)
    suspend fun getPushToken(): String?
    suspend fun updateNotification(deviceToken: String, pushAgreed: Boolean): Result<Unit>
}

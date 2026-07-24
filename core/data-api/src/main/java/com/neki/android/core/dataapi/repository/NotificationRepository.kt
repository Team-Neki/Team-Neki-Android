package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.Notification

interface NotificationRepository {
    suspend fun getRecentNotifications(): Result<List<Notification>>
    suspend fun savePushToken(token: String)
    suspend fun getPushToken(): String?
    suspend fun updateNotification(deviceToken: String, pushAgreed: Boolean): Result<Unit>
}

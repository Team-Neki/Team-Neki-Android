package com.neki.android.core.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.neki.android.core.data.local.di.UserDataStore
import com.neki.android.core.data.remote.api.NotificationService
import com.neki.android.core.data.remote.model.request.UpdateNotificationRequest
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.NotificationRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    @UserDataStore private val dataStore: DataStore<Preferences>,
    private val notificationService: NotificationService,
) : NotificationRepository {

    companion object {
        private val PUSH_TOKEN = stringPreferencesKey("push_token")
    }

    override suspend fun savePushToken(token: String) {
        dataStore.edit { it[PUSH_TOKEN] = token }
    }

    override suspend fun getPushToken(): String? =
        dataStore.data.map { it[PUSH_TOKEN] }.first()

    override suspend fun updateNotification(
        deviceToken: String,
        pushAgreed: Boolean,
    ): Result<Unit> = runSuspendCatching {
        notificationService.updateNotification(
            UpdateNotificationRequest(
                deviceToken = deviceToken,
                pushAgreed = pushAgreed,
            ),
        )
    }
}

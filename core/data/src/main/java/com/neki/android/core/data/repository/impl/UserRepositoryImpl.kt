package com.neki.android.core.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.neki.android.core.data.local.di.UserDataStore
import com.neki.android.core.data.remote.api.UserService
import com.neki.android.core.data.remote.model.request.UpdateProfileImageRequest
import com.neki.android.core.data.remote.model.request.UpdateUserInfoRequest
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.UserRepository
import com.neki.android.core.model.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    @UserDataStore private val dataStore: DataStore<Preferences>,
    private val userService: UserService,
) : UserRepository {
    override val hasVisitedRandomPose: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[HAS_VISITED_RANDOM_POSE] ?: false
        }

    override suspend fun setRandomPoseVisited() {
        dataStore.edit { preferences ->
            preferences[HAS_VISITED_RANDOM_POSE] = true
        }
    }

    override val hasShownQRInfoToolTip: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[HAS_SHOWN_QR_INFO_TOOLTIP] ?: false
        }

    override suspend fun setQRInfoToolTipShown() {
        dataStore.edit { preferences ->
            preferences[HAS_SHOWN_QR_INFO_TOOLTIP] = true
        }
    }

    override val lastAutoNotificationGuideEpochDay: Flow<Long> =
        dataStore.data.map { preferences ->
            preferences[LAST_AUTO_NOTIFICATION_GUIDE_EPOCH_DAY] ?: -1L
        }

    override suspend fun setAutoNotificationGuideShown(epochDay: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_AUTO_NOTIFICATION_GUIDE_EPOCH_DAY] = epochDay
        }
    }

    override val updateNotificationGuideVersionCode: Flow<Long> =
        dataStore.data.map { preferences ->
            preferences[UPDATE_NOTIFICATION_GUIDE_VERSION_CODE] ?: -1L
        }

    override suspend fun setUpdateNotificationGuideChecked(versionCode: Long) {
        dataStore.edit { preferences ->
            preferences[UPDATE_NOTIFICATION_GUIDE_VERSION_CODE] = versionCode
        }
    }

    override val hasRequestedNotificationPermission: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[HAS_REQUESTED_NOTIFICATION_PERMISSION] ?: false
        }

    override suspend fun setNotificationPermissionRequested() {
        dataStore.edit { preferences ->
            preferences[HAS_REQUESTED_NOTIFICATION_PERMISSION] = true
        }
    }

    override suspend fun clearNotificationGuideRecord() {
        dataStore.edit { preferences ->
            preferences.remove(LAST_AUTO_NOTIFICATION_GUIDE_EPOCH_DAY)
            preferences.remove(UPDATE_NOTIFICATION_GUIDE_VERSION_CODE)
        }
    }

    override suspend fun getUserInfo(): Result<UserInfo> = runSuspendCatching {
        userService.getUserInfo().data.toModel()
    }

    override suspend fun updateUserInfo(nickname: String): Result<Unit> = runSuspendCatching {
        userService.updateUserInfo(UpdateUserInfoRequest(nickname))
    }

    override suspend fun updateProfileImage(mediaId: Long?): Result<Unit> = runSuspendCatching {
        userService.updateProfileImage(UpdateProfileImageRequest(mediaId))
    }

    companion object {
        private val HAS_VISITED_RANDOM_POSE = booleanPreferencesKey("is_first_visit_random_pose")
        private val HAS_SHOWN_QR_INFO_TOOLTIP = booleanPreferencesKey("is_first_visit_archive")
        private val LAST_AUTO_NOTIFICATION_GUIDE_EPOCH_DAY = longPreferencesKey("last_auto_notification_guide_epoch_day")
        private val UPDATE_NOTIFICATION_GUIDE_VERSION_CODE = longPreferencesKey("update_notification_guide_version_code")
        private val HAS_REQUESTED_NOTIFICATION_PERMISSION = booleanPreferencesKey("has_requested_notification_permission")
    }
}

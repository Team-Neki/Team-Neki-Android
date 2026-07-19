package com.neki.android.core.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.neki.android.core.data.local.di.MarketingPopupDataStore
import com.neki.android.core.data.local.di.UserDataStore
import com.neki.android.core.data.local.model.MarketingPopupRecord
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
    @MarketingPopupDataStore private val marketingPopupDataStore: DataStore<MarketingPopupRecord>,
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

    override val lastArchiveMarketingPopupTimestamp: Flow<Long> = marketingPopupDataStore.data.map { it.lastShownAt }

    override val archiveMarketingPopupShownCount: Flow<Int> = marketingPopupDataStore.data.map { it.shownCount }

    override suspend fun recordMarketingPopupShown() {
        marketingPopupDataStore.updateData { current ->
            current.copy(
                lastShownAt = System.currentTimeMillis(),
                shownCount = current.shownCount + 1,
            )
        }
    }

    override suspend fun clearMarketingPopupRecord() {
        marketingPopupDataStore.updateData { MarketingPopupRecord() }
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
    }
}

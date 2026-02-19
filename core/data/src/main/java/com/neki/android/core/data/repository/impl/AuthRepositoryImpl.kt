package com.neki.android.core.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.neki.android.core.data.local.di.AuthDataStore
import com.neki.android.core.data.remote.api.AuthService
import com.neki.android.core.data.remote.model.request.KakaoLoginRequest
import com.neki.android.core.data.remote.model.request.RefreshTokenRequest
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.AuthRepository
import com.neki.android.core.model.AppVersion
import com.neki.android.core.model.Auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @AuthDataStore private val dataStore: DataStore<Preferences>,
    private val authService: AuthService,
) : AuthRepository {
    override val dismissedVersion: Flow<String> =
        dataStore.data.map { preferences ->
            preferences[DISMISSED_VERSION] ?: ""
        }

    override suspend fun getAppVersion(): Result<AppVersion> = runSuspendCatching {
        authService.getAppVersion(platform = "android").data.toModel()
    }

    override suspend fun setDismissedVersion(version: String) {
        dataStore.edit { preferences ->
            preferences[DISMISSED_VERSION] = version
        }
    }

    override suspend fun loginWithKakao(idToken: String): Result<Auth> = runSuspendCatching {
        authService.loginWithKakao(
            requestBody = KakaoLoginRequest(
                idToken = idToken,
            ),
        ).data.toModel()
    }

    override suspend fun updateAccessToken(refreshToken: String): Result<Auth> = runSuspendCatching {
        authService.updateAccessToken(
            requestBody = RefreshTokenRequest(
                refreshToken = refreshToken,
            ),
        ).data.toModel()
    }

    override suspend fun withdrawAccount(): Result<Unit> = runSuspendCatching {
        authService.withdrawAccount()
    }

    override fun hasCompletedOnboarding(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[HAS_COMPLETED_ONBOARDING] ?: false
        }
    }

    override suspend fun setCompletedOnboarding(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[HAS_COMPLETED_ONBOARDING] = value
        }
    }

    companion object {
        private val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        private val DISMISSED_VERSION = stringPreferencesKey("dismissed_version")
    }
}

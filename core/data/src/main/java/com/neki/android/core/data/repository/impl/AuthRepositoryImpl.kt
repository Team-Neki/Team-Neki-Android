package com.neki.android.core.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.neki.android.core.data.local.di.AuthDataStore
import com.neki.android.core.data.remote.api.AuthService
import com.neki.android.core.data.remote.model.request.KakaoLoginRequest
import com.neki.android.core.data.remote.model.request.RefreshTokenRequest
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.AuthRepository
import com.neki.android.core.model.Auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @AuthDataStore private val dataStore: DataStore<Preferences>,
    private val authService: AuthService,
) : AuthRepository {
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

    override fun isReadOnboarding(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_READ_ONBOARDING] ?: false
        }
    }

    override suspend fun setReadOnboarding(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_READ_ONBOARDING] = value
        }
    }

    companion object {
        private val IS_READ_ONBOARDING = booleanPreferencesKey("is_read_onboarding")
    }
}

package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.analytics.ClientLogAuthState
import com.neki.android.core.model.Auth
import kotlinx.coroutines.flow.first
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.neki.android.core.common.crypto.CryptoManager
import com.neki.android.core.dataapi.repository.TokenRepository
import dagger.Lazy
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.neki.android.core.data.local.di.TokenDataStore
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    @TokenDataStore private val dataStore: DataStore<Preferences>,
    private val httpClient: Lazy<HttpClient>,
    private val clientLogAuthState: ClientLogAuthState,
) : TokenRepository {

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    ) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = CryptoManager.encrypt(accessToken)
            preferences[REFRESH_TOKEN] = CryptoManager.encrypt(refreshToken)
            clientLogAuthState.updateTokens(accessToken, refreshToken, isRefresh = false)
        }
        httpClient.get().authProvider<BearerAuthProvider>()?.clearToken()
    }

    override suspend fun replaceTokensIfCurrent(
        expectedAccessToken: String,
        accessToken: String,
        refreshToken: String,
    ): Boolean {
        var replaced = false
        dataStore.edit { preferences ->
            val current = preferences[ACCESS_TOKEN]?.let { CryptoManager.decrypt(it) }
            if (current == expectedAccessToken) {
                preferences[ACCESS_TOKEN] = CryptoManager.encrypt(accessToken)
                preferences[REFRESH_TOKEN] = CryptoManager.encrypt(refreshToken)
                clientLogAuthState.updateTokens(accessToken, refreshToken, isRefresh = true)
                replaced = true
            }
        }
        if (replaced) httpClient.get().authProvider<BearerAuthProvider>()?.clearToken()
        return replaced
    }

    override suspend fun clearTokensIfCurrent(expectedAccessToken: String): Boolean {
        var cleared = false
        dataStore.edit { preferences ->
            val current = preferences[ACCESS_TOKEN]?.let { CryptoManager.decrypt(it) }
            if (current == expectedAccessToken) {
                preferences.remove(ACCESS_TOKEN)
                preferences.remove(REFRESH_TOKEN)
                clientLogAuthState.clear()
                cleared = true
            }
        }
        if (cleared) httpClient.get().authProvider<BearerAuthProvider>()?.clearToken()
        return cleared
    }

    override suspend fun getTokens(): Auth? {
        val preferences = dataStore.data.first()
        return runCatching {
            val access = preferences[ACCESS_TOKEN]?.let { CryptoManager.decrypt(it) }
            val refresh = preferences[REFRESH_TOKEN]?.let { CryptoManager.decrypt(it) }
            if (access.isNullOrBlank() || refresh.isNullOrBlank()) null else Auth(access, refresh)
        }.getOrNull()
    }

    override fun hasTokens(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            try {
                val accessToken = preferences[ACCESS_TOKEN]?.let { CryptoManager.decrypt(it) }
                val refreshToken = preferences[REFRESH_TOKEN]?.let { CryptoManager.decrypt(it) }
                !accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()
            } catch (e: Exception) {
                clientLogAuthState.clear()
                dataStore.edit { it.remove(ACCESS_TOKEN); it.remove(REFRESH_TOKEN) }
                false
            }
        }
    }

    override fun getAccessToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            runCatching {
                preferences[ACCESS_TOKEN]?.let { CryptoManager.decrypt(it) } ?: ""
            }.getOrElse { "" }
        }
    }

    override fun getRefreshToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            runCatching {
                preferences[REFRESH_TOKEN]?.let { CryptoManager.decrypt(it) } ?: ""
            }.getOrElse { "" }
        }
    }

    override suspend fun clearTokensWithAuthCache() {
        clientLogAuthState.clear()
        httpClient.get().authProvider<BearerAuthProvider>()?.clearToken()
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
    }
}

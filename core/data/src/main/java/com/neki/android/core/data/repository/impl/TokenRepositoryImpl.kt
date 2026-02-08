package com.neki.android.core.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.neki.android.core.common.crypto.CryptoManager
import com.neki.android.core.dataapi.auth.AuthCacheManager
import com.neki.android.core.dataapi.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.neki.android.core.data.local.di.TokenDataStore
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    @TokenDataStore private val dataStore: DataStore<Preferences>,
    private val authCacheManager: AuthCacheManager,
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
        }
        authCacheManager.invalidateTokenCache()
    }

    override fun hasTokens(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            val accessToken = preferences[ACCESS_TOKEN]?.let { CryptoManager.decrypt(it) }
            val refreshToken = preferences[REFRESH_TOKEN]?.let { CryptoManager.decrypt(it) }

            !accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()
        }
    }

    override fun getAccessToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]?.let { CryptoManager.decrypt(it) } ?: ""
        }
    }

    override fun getRefreshToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN]?.let { CryptoManager.decrypt(it) } ?: ""
        }
    }

    override suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
        authCacheManager.invalidateTokenCache()
    }
}

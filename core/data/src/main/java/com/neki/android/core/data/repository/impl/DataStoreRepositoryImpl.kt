package com.neki.android.core.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.neki.android.core.common.crypto.CryptoManager
import com.neki.android.core.dataapi.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : DataStoreRepository {
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    override suspend fun saveJwtTokens(
        accessToken: String,
        refreshToken: String,
    ) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = CryptoManager.encrypt(accessToken)
            preferences[REFRESH_TOKEN] = CryptoManager.encrypt(refreshToken)
        }
    }

    override fun isSavedJwtTokens(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            val accessToken = preferences[ACCESS_TOKEN]?.let { CryptoManager.decrypt(it) }
            val refreshToken = preferences[REFRESH_TOKEN]?.let { CryptoManager.decrypt(it) }

            !accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()
        }
    }

    override fun getAccessToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]?.let { CryptoManager.decrypt(it) }
        }
    }

    override fun getRefreshToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN]?.let { CryptoManager.decrypt(it) }
        }
    }

    override suspend fun clearTokens() {
        dataStore.edit { it.clear() }
    }

    override suspend fun setBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    override fun getBoolean(key: Preferences.Key<Boolean>): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: false
        }
    }
}

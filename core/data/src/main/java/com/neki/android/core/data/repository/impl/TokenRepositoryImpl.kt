package com.neki.android.core.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.neki.android.core.common.crypto.CryptoManager
import com.neki.android.core.dataapi.repository.TokenRepository
import com.neki.android.core.model.analytics.ClientLogWithdrawal
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
) : TokenRepository {

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val CLIENT_LOG_WITHDRAWALS = stringSetPreferencesKey("client_log_withdrawals")
    }

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    ) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = CryptoManager.encrypt(accessToken)
            preferences[REFRESH_TOKEN] = CryptoManager.encrypt(refreshToken)
        }
    }

    override fun hasTokens(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            try {
                val accessToken = preferences[ACCESS_TOKEN]?.let { CryptoManager.decrypt(it) }
                val refreshToken = preferences[REFRESH_TOKEN]?.let { CryptoManager.decrypt(it) }
                !accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()
            } catch (e: Exception) {
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

    override suspend fun clearTokensWithAuthCache(withdrawal: ClientLogWithdrawal?) {
        httpClient.get().authProvider<BearerAuthProvider>()?.clearToken()
        dataStore.edit { preferences ->
            if (withdrawal != null) {
                // Persist queue cleanup across the same transition that clears credentials.
                preferences[CLIENT_LOG_WITHDRAWALS] = preferences[CLIENT_LOG_WITHDRAWALS].orEmpty() + withdrawal.encode()
            }
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
    }

    override fun pendingClientLogWithdrawals(): Flow<Set<ClientLogWithdrawal>> = dataStore.data.map { preferences ->
        preferences[CLIENT_LOG_WITHDRAWALS].orEmpty().mapNotNull { entry ->
            val parts = entry.split(':', limit = 2)
            if (parts.size == 2) ClientLogWithdrawal(parts[0], parts[1]) else null
        }.toSet()
    }

    override suspend fun clearClientLogWithdrawals(withdrawals: Set<ClientLogWithdrawal>) {
        dataStore.edit { preferences ->
            val remaining = preferences[CLIENT_LOG_WITHDRAWALS].orEmpty() - withdrawals.map { it.encode() }.toSet()
            if (remaining.isEmpty()) {
                preferences.remove(CLIENT_LOG_WITHDRAWALS)
            } else {
                preferences[CLIENT_LOG_WITHDRAWALS] = remaining
            }
        }
    }

    private fun ClientLogWithdrawal.encode(): String = "$userId:$eventId"
}

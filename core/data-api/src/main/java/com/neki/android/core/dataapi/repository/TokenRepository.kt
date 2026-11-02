package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.Auth
import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    )
    suspend fun replaceTokensIfCurrent(
        expectedAccessToken: String,
        accessToken: String,
        refreshToken: String,
    ): Boolean
    suspend fun clearTokensIfCurrent(expectedAccessToken: String): Boolean
    suspend fun getTokens(): Auth?
    fun hasTokens(): Flow<Boolean>
    fun getAccessToken(): Flow<String>
    fun getRefreshToken(): Flow<String>
    suspend fun clearTokensWithAuthCache()
}

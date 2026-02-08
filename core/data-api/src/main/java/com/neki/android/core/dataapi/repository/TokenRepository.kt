package com.neki.android.core.dataapi.repository

import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    )
    fun hasTokens(): Flow<Boolean>
    fun getAccessToken(): Flow<String>
    fun getRefreshToken(): Flow<String>
    suspend fun clearTokens()
}

package com.neki.android.core.dataapi.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun setTokens(
        accessToken: String,
        refreshToken: String,
    )

    fun getAccessToken(): Flow<String?>
    fun getRefreshToken(): Flow<String?>
    suspend fun clearTokens()
}

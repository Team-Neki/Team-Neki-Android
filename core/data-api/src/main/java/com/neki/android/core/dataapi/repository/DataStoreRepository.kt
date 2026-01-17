package com.neki.android.core.dataapi.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun saveJwtTokens(
        accessToken: String,
        refreshToken: String,
    )

    fun isSavedJwtTokens(): Flow<Boolean>
    fun getAccessToken(): Flow<String?>
    fun getRefreshToken(): Flow<String?>
    suspend fun clearTokens()
}

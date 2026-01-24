package com.neki.android.core.dataapi.repository

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun saveJwtTokens(
        accessToken: String,
        refreshToken: String,
    )
    fun isSavedJwtTokens(): Flow<Boolean>
    fun getAccessToken(): Flow<String>
    fun getRefreshToken(): Flow<String>
    suspend fun clearTokens()

    suspend fun setBoolean(key: Preferences.Key<Boolean>, value: Boolean)
    fun getBoolean(key: Preferences.Key<Boolean>): Flow<Boolean>
}

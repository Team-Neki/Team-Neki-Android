package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.Auth
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun loginWithKakao(idToken: String): Result<Auth>
    suspend fun updateAccessToken(refreshToken: String): Result<Auth>
    suspend fun withdrawAccount(): Result<Unit>

    fun hasCompletedOnboarding(): Flow<Boolean>
    suspend fun setCompletedOnboarding(value: Boolean)
}

package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.AppVersion
import com.neki.android.core.model.Auth
import com.neki.android.core.model.LoginType
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val dismissedVersion: Flow<String>
    suspend fun getAppVersion(): Result<AppVersion>
    suspend fun setDismissedVersion(version: String)

    suspend fun loginWithKakao(idToken: String): Result<Auth>
    suspend fun updateAccessToken(refreshToken: String): Result<Auth>
    suspend fun logout(): Result<Unit>
    suspend fun withdrawAccount(): Result<Unit>

    fun hasCompletedOnboarding(): Flow<Boolean>
    suspend fun setCompletedOnboarding(value: Boolean)

    val lastLoginType: Flow<LoginType?>
    suspend fun setLastLoginType(type: LoginType)
}

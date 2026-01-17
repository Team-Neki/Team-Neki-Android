package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.Auth

interface AuthRepository {
    suspend fun loginWithKakao(idToken: String): Result<Auth>
    suspend fun updateAccessToken(refreshToken: String): Result<Auth>
}

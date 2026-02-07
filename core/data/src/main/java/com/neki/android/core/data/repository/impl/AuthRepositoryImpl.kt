package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.remote.api.AuthService
import com.neki.android.core.data.remote.model.request.KakaoLoginRequest
import com.neki.android.core.data.remote.model.request.RefreshTokenRequest
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.AuthRepository
import com.neki.android.core.model.Auth
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
) : AuthRepository {
    override suspend fun loginWithKakao(idToken: String): Result<Auth> = runSuspendCatching {
        authService.loginWithKakao(
            requestBody = KakaoLoginRequest(
                idToken = idToken,
            ),
        ).data.toModel()
    }

    override suspend fun updateAccessToken(refreshToken: String): Result<Auth> = runSuspendCatching {
        authService.updateAccessToken(
            requestBody = RefreshTokenRequest(
                refreshToken = refreshToken,
            ),
        ).data.toModel()
    }

    override suspend fun withdrawAccount(): Result<Unit> = runSuspendCatching {
        authService.withdrawAccount()
    }
}

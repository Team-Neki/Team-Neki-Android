package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.remote.api.ApiService
import com.neki.android.core.data.remote.model.request.KakaoLoginRequest
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.AuthRepository
import com.neki.android.core.model.Login
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): AuthRepository {
    override suspend fun loginWithKakao(idToken: String): Result<Login> = runSuspendCatching {
        apiService.loginWithKakao(
            requestBody = KakaoLoginRequest(
                idToken = idToken
            )
        ).data.toModel()
    }
}

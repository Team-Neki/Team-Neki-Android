package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.Login

interface AuthRepository {
    suspend fun loginWithKakao(idToken: String): Result<Login>

}

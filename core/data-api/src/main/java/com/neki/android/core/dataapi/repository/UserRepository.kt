package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.User

interface UserRepository {
    suspend fun getUserInfo(): Result<User>
    suspend fun updateUserInfo(nickname: String): Result<Unit>
    suspend fun updateProfileImage(mediaId: Long?): Result<Unit>
}

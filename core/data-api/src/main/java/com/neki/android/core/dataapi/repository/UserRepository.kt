package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.UserInfo

interface UserRepository {
    suspend fun getUserInfo(): Result<UserInfo>
    suspend fun updateUserInfo(nickname: String): Result<Unit>
    suspend fun updateProfileImage(mediaId: Long?): Result<Unit>
}

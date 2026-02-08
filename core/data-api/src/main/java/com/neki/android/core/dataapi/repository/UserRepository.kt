package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.UserInfo
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val isFirstVisitRandomPose: Flow<Boolean>
    suspend fun setFirstVisitRandomPose(value: Boolean)
    suspend fun getUserInfo(): Result<UserInfo>
    suspend fun updateUserInfo(nickname: String): Result<Unit>

    suspend fun updateProfileImage(mediaId: Long?): Result<Unit>
}

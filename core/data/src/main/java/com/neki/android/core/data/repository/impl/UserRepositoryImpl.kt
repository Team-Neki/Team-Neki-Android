package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.remote.api.UserService
import com.neki.android.core.data.remote.model.request.UpdateProfileImageRequest
import com.neki.android.core.data.remote.model.request.UpdateUserInfoRequest
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.UserRepository
import com.neki.android.core.model.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
) : UserRepository {
    override suspend fun getUserInfo(): Result<User> = runSuspendCatching {
        userService.getUserInfo().data.toModel()
    }

    override suspend fun updateUserInfo(nickname: String): Result<Unit> = runSuspendCatching {
        userService.updateUserInfo(UpdateUserInfoRequest(nickname))
    }

    override suspend fun updateProfileImage(mediaId: Long?): Result<Unit> = runSuspendCatching {
        userService.updateProfileImage(UpdateProfileImageRequest(mediaId))
    }
}

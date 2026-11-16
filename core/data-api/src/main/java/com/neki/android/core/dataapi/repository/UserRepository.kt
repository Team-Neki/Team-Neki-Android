package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.UserInfo
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val hasVisitedRandomPose: Flow<Boolean>
    suspend fun setRandomPoseVisited()
    val hasShownQRInfoToolTip: Flow<Boolean>
    suspend fun setQRInfoToolTipShown()
    val lastAutoNotificationGuideEpochDay: Flow<Long>
    suspend fun setAutoNotificationGuideShown(epochDay: Long)
    val updateNotificationGuideVersionCode: Flow<Long>
    suspend fun setUpdateNotificationGuideChecked(versionCode: Long)
    val hasRequestedNotificationPermission: Flow<Boolean>
    suspend fun setNotificationPermissionRequested()
    suspend fun clearNotificationGuideRecord()

    suspend fun getUserInfo(): Result<UserInfo>
    suspend fun updateUserInfo(nickname: String): Result<Unit>

    suspend fun updateProfileImage(mediaId: Long?): Result<Unit>
}

package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.UserInfo
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val hasVisitedRandomPose: Flow<Boolean>
    suspend fun setRandomPoseVisited()
    val hasShownQRInfoToolTip: Flow<Boolean>
    suspend fun setQRInfoToolTipShown()
    val lastArchiveMarketingPopupTimestamp: Flow<Long>
    val archiveMarketingPopupShownCount: Flow<Int>
    suspend fun recordMarketingPopupShown()
    suspend fun clearMarketingPopupRecord()

    suspend fun getUserInfo(): Result<UserInfo>
    suspend fun updateUserInfo(nickname: String): Result<Unit>

    suspend fun updateProfileImage(mediaId: Long?): Result<Unit>
}

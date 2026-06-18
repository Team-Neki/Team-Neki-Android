package com.neki.android.core.dataapi.repository

import androidx.paging.PagingData
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import com.neki.android.core.model.PosePage
import com.neki.android.core.model.SortOrder
import kotlinx.coroutines.flow.Flow

interface PoseRepository {

    fun getPosesFlow(
        headCount: PeopleCount? = null,
        sortOrder: SortOrder = SortOrder.DESC,
    ): Flow<PagingData<Pose>>

    fun getBookmarkedPosesFlow(
        sortOrder: SortOrder = SortOrder.DESC,
    ): Flow<PagingData<Pose>>

    suspend fun getPosesPage(
        headCount: PeopleCount? = null,
        page: Int = 0,
        size: Int = 20,
        sortOrder: SortOrder = SortOrder.DESC,
    ): Result<PosePage>

    suspend fun getBookmarkedPosesPage(
        page: Int = 0,
        size: Int = 20,
        sortOrder: SortOrder = SortOrder.DESC,
    ): Result<PosePage>

    suspend fun getPose(poseId: Long): Result<Pose>

    suspend fun getSingleRandomPose(
        headCount: PeopleCount,
        excludeIds: Set<Long>,
    ): Result<Pose>

    suspend fun getMultipleRandomPose(
        headCount: PeopleCount,
        excludeIds: Set<Long>,
        poseSize: Int,
    ): Result<List<Pose>>

    suspend fun updateBookmark(poseId: Long, bookmark: Boolean): Result<Unit>
}

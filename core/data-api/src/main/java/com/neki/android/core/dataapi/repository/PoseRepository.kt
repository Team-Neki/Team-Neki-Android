package com.neki.android.core.dataapi.repository

import androidx.paging.PagingData
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import com.neki.android.core.model.SortOrder
import kotlinx.coroutines.flow.Flow

const val NO_MORE_RANDOM_POSE = 400

interface PoseRepository {

    fun getPosesFlow(
        headCount: PeopleCount? = null,
        sortOrder: SortOrder = SortOrder.DESC,
    ): Flow<PagingData<Pose>>

    fun getScrappedPosesFlow(
        sortOrder: SortOrder = SortOrder.DESC,
    ): Flow<PagingData<Pose>>

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

    suspend fun updateScrap(poseId: Long, scrap: Boolean): Result<Unit>
}

package com.neki.android.core.dataapi.repository

import androidx.paging.PagingData
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import com.neki.android.core.model.SortOrder
import kotlinx.coroutines.flow.Flow

interface PoseRepository {

    fun getPosesFlow(
        headCount: PeopleCount? = null,
        sortOrder: SortOrder = SortOrder.DESC,
    ): Flow<PagingData<Pose>>

    suspend fun getPose(poseId: Long): Result<Pose>

    suspend fun getRandomPose(): Result<Pose>

    suspend fun updateScrap(poseId: Long, scrap: Boolean): Result<Unit>
}

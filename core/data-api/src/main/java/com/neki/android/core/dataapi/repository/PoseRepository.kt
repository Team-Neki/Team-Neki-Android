package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import com.neki.android.core.model.SortOrder

interface PoseRepository {
    suspend fun getPoses(
        page: Int = 0,
        size: Int = 20,
        headCount: PeopleCount? = null,
        sortOrder: SortOrder = SortOrder.DESC,
    ): Result<List<Pose>>

    suspend fun getPose(poseId: Long): Result<Pose>

    suspend fun getRandomPose(): Result<Pose>
}

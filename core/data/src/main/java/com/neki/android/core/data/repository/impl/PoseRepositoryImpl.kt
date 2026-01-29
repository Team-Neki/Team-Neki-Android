package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.remote.api.PoseService
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.PoseRepository
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import com.neki.android.core.model.SortOrder
import javax.inject.Inject

class PoseRepositoryImpl @Inject constructor(
    private val poseService: PoseService,
) : PoseRepository {
    override suspend fun getPoses(
        page: Int,
        size: Int,
        headCount: PeopleCount?,
        sortOrder: SortOrder,
    ): Result<List<Pose>> = runSuspendCatching {
        poseService.getPoses(
            page = page,
            size = size,
            headCount = headCount?.name,
            sortOrder = sortOrder.name,
        ).data.toModels()
    }

    override suspend fun getPose(poseId: Long): Result<Pose> = runSuspendCatching {
        poseService.getPose(poseId).data.toModel()
    }

    override suspend fun getRandomPose(): Result<Pose> = runSuspendCatching {
        poseService.getRandomPose().data.toModel()
    }
}

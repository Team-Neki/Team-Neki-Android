package com.neki.android.core.data.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.neki.android.core.data.paging.PosePagingSource
import com.neki.android.core.data.remote.api.PoseService
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.PoseRepository
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import com.neki.android.core.model.SortOrder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val PAGE_SIZE = 20
private const val PREFETCH_DISTANCE = 10

class PoseRepositoryImpl @Inject constructor(
    private val poseService: PoseService,
) : PoseRepository {

    override fun getPosesFlow(
        headCount: PeopleCount?,
        sortOrder: SortOrder,
    ): Flow<PagingData<Pose>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                PosePagingSource(
                    poseService = poseService,
                    headCount = headCount,
                    sortOrder = sortOrder,
                )
            },
        ).flow
    }

    override suspend fun getPose(poseId: Long): Result<Pose> = runSuspendCatching {
        poseService.getPose(poseId).data.toModel()
    }

    override suspend fun getRandomPose(): Result<Pose> = runSuspendCatching {
        poseService.getRandomPose().data.toModel()
    }

    override suspend fun updateScrap(poseId: Long, scrap: Boolean): Result<Unit> = runSuspendCatching {
        poseService.updateScrap(poseId, scrap)
    }
}

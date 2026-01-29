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

    override fun getPosesFlow(
        headCount: PeopleCount?,
        sortOrder: SortOrder,
    ): Flow<PagingData<Pose>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
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

    companion object {
        private const val PAGE_SIZE = 20
    }
}

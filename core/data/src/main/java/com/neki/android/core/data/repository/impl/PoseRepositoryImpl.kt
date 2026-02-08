package com.neki.android.core.data.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.neki.android.core.common.exception.RandomPoseRetryExhaustedException
import com.neki.android.core.data.paging.PosePagingSource
import com.neki.android.core.data.paging.ScrapPosePagingSource
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

    override fun getScrappedPosesFlow(
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
                ScrapPosePagingSource(
                    poseService = poseService,
                    sortOrder = sortOrder,
                )
            },
        ).flow
    }

    override suspend fun getPose(poseId: Long): Result<Pose> = runSuspendCatching {
        poseService.getPose(poseId).data.toModel()
    }

    override suspend fun getSingleRandomPose(
        headCount: PeopleCount,
        excludeIds: Set<Long>,
        maxRetry: Int,
    ): Result<Pose> = runSuspendCatching {
        repeat(maxRetry) {
            val pose = poseService.getRandomPose(headCount = headCount.name).data.toModel()
            if (pose.id !in excludeIds) {
                return@runSuspendCatching pose
            }
        }
        throw RandomPoseRetryExhaustedException("새로운 포즈를 찾지 못했어요")
    }

    override suspend fun getMultipleRandomPose(
        headCount: PeopleCount,
        excludeIds: Set<Long>,
        poseSize: Int,
        maxRetry: Int,
    ): Result<List<Pose>> = runSuspendCatching {
        val result = mutableListOf<Pose>()
        val collectedIds = excludeIds.toMutableSet()
        var retryCount = 0

        while (result.size < poseSize && retryCount < maxRetry) {
            val pose = poseService.getRandomPose(headCount = headCount.name).data.toModel()
            if (pose.id !in collectedIds) {
                result.add(pose)
                collectedIds.add(pose.id)
            } else {
                retryCount++
            }
        }

        result.ifEmpty { throw RandomPoseRetryExhaustedException("새로운 포즈를 찾지 못했어요") }
    }

    override suspend fun updateScrap(poseId: Long, scrap: Boolean): Result<Unit> = runSuspendCatching {
        poseService.updateScrap(poseId, scrap)
    }
}

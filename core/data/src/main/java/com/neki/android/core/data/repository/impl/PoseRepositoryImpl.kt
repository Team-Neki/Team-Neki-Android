package com.neki.android.core.data.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.neki.android.core.common.exception.ApiErrorCode.NO_MORE_RANDOM_POSE
import com.neki.android.core.common.exception.NekiApiException
import com.neki.android.core.data.paging.PosePagingSource
import com.neki.android.core.data.paging.ScrapPosePagingSource
import com.neki.android.core.data.remote.api.PoseService
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.PoseRepository
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import com.neki.android.core.model.SortOrder
import io.ktor.client.plugins.ClientRequestException
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
    ): Result<Pose> = runSuspendCatching {
        val excludeIdsString = excludeIds.joinToString(",")

        return@runSuspendCatching try {
            poseService.getRandomPose(
                headCount = headCount.name,
                excludeIds = excludeIdsString,
            ).data.toModel()
        } catch (e: ClientRequestException) {
            if (e.response.status.value == NO_MORE_RANDOM_POSE) throw NekiApiException.NoMorePoseException(NO_MORE_RANDOM_POSE, e.message, e)
            else throw e
        }
    }

    override suspend fun getMultipleRandomPose(
        headCount: PeopleCount,
        excludeIds: Set<Long>,
        poseSize: Int,
    ): Result<List<Pose>> = runSuspendCatching {
        val result = mutableListOf<Pose>()
        val collectedIds = excludeIds.toMutableSet()
        repeat(poseSize) {
            val excludeIdsString = collectedIds.joinToString(",")
            val pose = try {
                poseService.getRandomPose(
                    headCount = headCount.name,
                    excludeIds = excludeIdsString,
                ).data.toModel()
            } catch (e: ClientRequestException) {
                // Http Error Code 이지만, 클라이언트에서 성공으로 취급
                if (e.response.status.value == NO_MORE_RANDOM_POSE) return@runSuspendCatching result
                else throw e
            }

            if (pose.id !in collectedIds) {
                result.add(pose)
                collectedIds.add(pose.id)
            }
        }
        return@runSuspendCatching result
    }

    override suspend fun updateScrap(poseId: Long, scrap: Boolean): Result<Unit> = runSuspendCatching {
        poseService.updateScrap(poseId, scrap)
    }
}

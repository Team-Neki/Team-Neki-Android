package com.neki.android.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.neki.android.core.data.remote.api.PoseService
import com.neki.android.core.model.Pose
import com.neki.android.core.model.SortOrder

class BookmarkPosePagingSource(
    private val poseService: PoseService,
    private val sortOrder: SortOrder,
) : PagingSource<Int, Pose>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pose> {
        return try {
            val page = params.key ?: 0
            val poses = poseService.getBookmarkedPoses(
                page = page,
                size = params.loadSize,
                sortOrder = sortOrder.name,
            ).data.toModels()

            LoadResult.Page(
                data = poses,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (poses.isEmpty()) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Pose>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

package com.neki.android.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.neki.android.core.data.remote.api.PhotoService
import com.neki.android.core.model.Photo

class PhotoPagingSource(
    private val photoService: PhotoService,
    private val folderId: Long?,
    private val sortOrder: String,
) : PagingSource<Int, Photo>() {

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        return try {
            val page = params.key ?: 0
            val response = photoService.getPhotos(
                folderId = folderId,
                page = page,
                size = params.loadSize,
                sortOrder = sortOrder,
            )
            val photos = response.data.toModels()
            val hasNext = response.data.hasNext

            LoadResult.Page(
                data = photos,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (hasNext) page + 1 else null,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

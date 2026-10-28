package com.neki.android.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.neki.android.core.data.remote.api.MapService
import com.neki.android.core.model.SearchCompletion
import com.neki.android.core.model.SearchCompletionType
import com.neki.android.core.model.SearchFilterGroup
import kotlinx.coroutines.CancellationException

internal data class SearchPageKey(
    val type: SearchCompletionType,
    val page: Int = 0,
)

internal class SearchPagingSource(
    private val mapService: MapService,
    private val keyword: String,
    private val latitude: Double?,
    private val longitude: Double?,
    private val onSearchSuccess: (Boolean, Int, Boolean) -> Unit,
) : PagingSource<SearchPageKey, SearchCompletion>() {
    override fun getRefreshKey(state: PagingState<SearchPageKey, SearchCompletion>): SearchPageKey? = null

    override suspend fun load(params: LoadParams<SearchPageKey>): LoadResult<SearchPageKey, SearchCompletion> {
        if (keyword.isBlank()) return LoadResult.Page(emptyList(), null, null)
        return try {
            var page = fetchPage(params.key ?: SearchPageKey(SearchCompletionType.REGION), params.loadSize)
            while (page.data.isEmpty() && page.nextKey != null) {
                page = fetchPage(checkNotNull(page.nextKey), params.loadSize)
            }
            if (params is LoadParams.Refresh || page.data.isNotEmpty()) {
                onSearchSuccess(params is LoadParams.Refresh, page.data.size, page.nextKey != null)
            }
            page
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun fetchPage(
        cursor: SearchPageKey,
        size: Int,
    ): LoadResult.Page<SearchPageKey, SearchCompletion> {
        if (cursor.type == SearchCompletionType.PHOTO_BOOTH && keyword.none { !it.isWhitespace() && it != '-' && it != '_' }) {
            return LoadResult.Page(emptyList(), null, null)
        }
        val response = mapService.getSearchCompletions(
            type = cursor.type,
            keyword = keyword,
            page = cursor.page,
            size = size,
            latitude = latitude,
            longitude = longitude,
        ).data
        val next = if (response.hasNext) {
            cursor.copy(page = cursor.page + 1)
        } else {
            SearchCompletionType.entries.getOrNull(cursor.type.ordinal + 1)?.let { SearchPageKey(it) }
        }
        val filterGroup = response.filterGroup?.toModel() ?: SearchFilterGroup()
        return LoadResult.Page(
            data = response.items.map { it.toModel(cursor.type, filterGroup) },
            prevKey = null,
            nextKey = next,
        )
    }
}

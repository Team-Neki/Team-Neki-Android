package com.neki.android.feature.map.impl.search

import com.neki.android.core.model.Brand
import com.neki.android.core.model.PhotoBooth
import com.neki.android.feature.map.impl.DragLevel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList

/** 검색 API와 독립적인 지도 검색 결과의 화면 상태. 목록 공급은 #288에서 연결한다. */
data class SearchResultPanelState(
    val keyword: String,
    val photoBooths: ImmutableList<PhotoBooth> = persistentListOf(),
    val dragLevel: DragLevel = DragLevel.SECOND,
    val selectedBrandIds: ImmutableSet<Long> = persistentSetOf(),
    val draftBrandIds: ImmutableSet<Long> = persistentSetOf(),
    val showFilter: Boolean = false,
    val focusedBoothId: Long? = null,
) {
    fun visibleBooths(brands: ImmutableList<Brand>): ImmutableList<PhotoBooth> {
        val selectedNames = brands.filter { it.id in selectedBrandIds }.map { it.name }
        return photoBooths.filter { selectedBrandIds.isEmpty() || it.brandName in selectedNames }.toImmutableList()
    }
}

sealed interface SearchResultPanelIntent {
    data class ShowResults(val keyword: String) : SearchResultPanelIntent
    data object CloseResults : SearchResultPanelIntent
    data class ChangeDragLevel(val level: DragLevel) : SearchResultPanelIntent
    data object OpenFilter : SearchResultPanelIntent
    data object DismissFilter : SearchResultPanelIntent
    data class ToggleBrand(val brandId: Long) : SearchResultPanelIntent
    data object ApplyFilter : SearchResultPanelIntent
}

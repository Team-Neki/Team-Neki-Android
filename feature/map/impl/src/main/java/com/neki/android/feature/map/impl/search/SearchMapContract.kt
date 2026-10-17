package com.neki.android.feature.map.impl.search

import com.neki.android.core.model.PhotoBooth
import com.neki.android.core.model.SearchBrand
import com.neki.android.feature.map.impl.DragLevel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList

data class SearchMapState(
    val keyword: String,
    val booths: ImmutableList<PhotoBooth> = persistentListOf(),
    val filters: ImmutableList<SearchBrand> = persistentListOf(),
    val selectedBrandIds: ImmutableSet<Long> = persistentSetOf(),
    val draftBrandIds: ImmutableSet<Long> = persistentSetOf(),
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val showDistance: Boolean = false,
    val dragLevel: DragLevel = DragLevel.SECOND,
    val showFilter: Boolean = false,
    val focusedId: String? = null,
) {
    fun visibleBooths(): ImmutableList<PhotoBooth> {
        val names = filters.filter { it.id in selectedBrandIds }.map { it.name }.toSet()
        return booths.filter { names.isEmpty() || it.brandName in names }
            .map { it.copy(isFocused = it.id == focusedId) }.toImmutableList()
    }
}

sealed interface SearchMapIntent {
    data object Close : SearchMapIntent
    data class ChangeDragLevel(val level: DragLevel) : SearchMapIntent
    data object OpenFilter : SearchMapIntent
    data object DismissFilter : SearchMapIntent
    data class ToggleBrand(val id: Long) : SearchMapIntent
    data object ApplyFilter : SearchMapIntent
    data class SelectBooth(
        val id: String,
        val entryPoint: String,
    ) : SearchMapIntent
    data object CloseBooth : SearchMapIntent
}

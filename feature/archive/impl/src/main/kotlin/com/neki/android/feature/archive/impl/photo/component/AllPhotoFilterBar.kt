package com.neki.android.feature.archive.impl.photo.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.component.DropdownPopup
import com.neki.android.core.ui.component.FilterBar
import com.neki.android.feature.archive.impl.photo.PhotoFilter
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun AllPhotoFilterBar(
    showFilterPopup: Boolean,
    selectedFilter: PhotoFilter,
    isFavoriteSelected: Boolean,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    onClickSortChip: () -> Unit = {},
    onClickFavoriteChip: () -> Unit = {},
    onDismissPopup: () -> Unit = {},
    onClickFilterRow: (PhotoFilter) -> Unit = {},
) {
    Box {
        FilterBar(
            isDownIconChipSelected = false,
            isDefaultChipSelected = isFavoriteSelected,
            downIconChipDisplayText = selectedFilter.label,
            defaultChipDisplayText = "즐겨찾는",
            modifier = modifier,
            visible = visible,
            onClickDownIconChip = onClickSortChip,
            onClickDefaultChip = onClickFavoriteChip,
        )
        if (showFilterPopup) {
            val density = LocalDensity.current
            val popupOffsetX = with(density) { 20.dp.toPx().toInt() }
            val popupOffsetY = with(density) { 46.dp.toPx().toInt() }

            DropdownPopup(
                items = PhotoFilter.entries.toImmutableList(),
                selectedItem = selectedFilter,
                onSelect = onClickFilterRow,
                onDismissRequest = onDismissPopup,
                itemLabel = { it.label },
                modifier = Modifier.width(96.dp),
                offset = IntOffset(x = popupOffsetX, y = popupOffsetY),
            )
        }
    }
}

@ComponentPreview
@Composable
private fun AllPhotoFilterBarDefaultPreview() {
    NekiTheme {
        AllPhotoFilterBar(
            showFilterPopup = false,
            selectedFilter = PhotoFilter.NEWEST,
            isFavoriteSelected = false,
        )
    }
}

@ComponentPreview
@Composable
private fun AllPhotoFilterBarSelectedPreview() {
    NekiTheme {
        AllPhotoFilterBar(
            showFilterPopup = false,
            selectedFilter = PhotoFilter.OLDEST,
            isFavoriteSelected = true,
        )
    }
}

@ComponentPreview
@Composable
private fun PhotoFilterPopupPreview() {
    var showPopup by remember { mutableStateOf(true) }

    NekiTheme {
        AllPhotoFilterBar(
            showFilterPopup = showPopup,
            selectedFilter = PhotoFilter.NEWEST,
            isFavoriteSelected = false,
            onClickSortChip = { showPopup = true },
            onClickFilterRow = { showPopup = false },
            onDismissPopup = { showPopup = false },
        )
    }
}

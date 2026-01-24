package com.neki.android.feature.archive.impl.photo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.extension.buttonShadow
import com.neki.android.core.designsystem.extension.clickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.component.FilterBar
import com.neki.android.feature.archive.impl.photo.PhotoFilter

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
            PhotoFilterPopup(
                selectedFilter = selectedFilter,
                onDismissRequest = onDismissPopup,
                onClickFilterRow = onClickFilterRow,
            )
        }
    }
}

@Composable
private fun PhotoFilterPopup(
    selectedFilter: PhotoFilter,
    onDismissRequest: () -> Unit,
    onClickFilterRow: (PhotoFilter) -> Unit,
) {
    val density = LocalDensity.current
    val popupOffsetX = with(density) { 20.dp.toPx().toInt() }
    val popupOffsetY = with(density) { 46.dp.toPx().toInt() }

    Popup(
        offset = IntOffset(x = popupOffsetX, y = popupOffsetY),
        alignment = Alignment.TopStart,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        Column(
            modifier = Modifier
                .buttonShadow(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.2f),
                    offsetX = 0.dp,
                    offsetY = 0.dp,
                    blurRadius = 5.dp,
                )
                .background(
                    color = NekiTheme.colorScheme.white,
                    shape = RoundedCornerShape(8.dp),
                )
                .width(96.dp)
                .padding(vertical = 6.dp),
        ) {
            PhotoFilter.entries.forEach { filter ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (selectedFilter == filter) NekiTheme.colorScheme.gray50
                            else NekiTheme.colorScheme.white,
                        )
                        .clickableSingle { onClickFilterRow(filter) }
                        .padding(horizontal = 16.dp, vertical = 5.dp),
                    text = filter.label,
                    style = NekiTheme.typography.body14Medium,
                )
            }
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

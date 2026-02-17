package com.neki.android.feature.archive.impl.album_detail.component

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.topbar.BackTitleOptionTopBar
import com.neki.android.core.designsystem.topbar.BackTitleTextButtonTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.component.DropdownPopup
import com.neki.android.feature.archive.impl.model.SelectMode
import kotlinx.collections.immutable.toImmutableList

private enum class FavoriteOptionItem(val label: String) {
    SELECT("선택"),
    ADD_PHOTO("사진 추가"),
}

private enum class CustomOptionItem(val label: String) {
    SELECT("사진 선택"),
    ADD_PHOTO("사진 추가"),
    RENAME_ALBUM("앨범 이름 변경"),
}

@Composable
internal fun AlbumDetailTopBar(
    isFavoriteAlbum: Boolean,
    title: String,
    selectMode: SelectMode,
    showOptionPopup: Boolean,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit = {},
    onClickOptionIcon: () -> Unit = {},
    onClickSelect: () -> Unit = {},
    onClickAddPhoto: () -> Unit = {},
    onClickRenameAlbum: () -> Unit = {},
    onClickCancel: () -> Unit = {},
    onDismissPopup: () -> Unit = {},
) {
    when (selectMode) {
        SelectMode.DEFAULT -> {
            BackTitleOptionTopBar(
                modifier = modifier,
                title = title,
                onBack = onClickBack,
                onClickIcon = onClickOptionIcon,
            )
        }

        SelectMode.SELECTING -> {
            BackTitleTextButtonTopBar(
                modifier = modifier,
                title = title,
                buttonLabel = "취소",
                enabledTextColor = NekiTheme.colorScheme.gray800,
                onBack = onClickBack,
                onClickTextButton = onClickCancel,
            )
        }
    }

    if (showOptionPopup) {
        val density = LocalDensity.current
        val popupOffsetX = with(density) { (-20).dp.toPx().toInt() }
        val popupOffsetY = with(density) { 54.dp.toPx().toInt() }

        if (isFavoriteAlbum) {
            DropdownPopup(
                items = FavoriteOptionItem.entries.toImmutableList(),
                onSelect = { item ->
                    when (item) {
                        FavoriteOptionItem.SELECT -> onClickSelect()
                        FavoriteOptionItem.ADD_PHOTO -> onClickAddPhoto()
                    }
                },
                onDismissRequest = onDismissPopup,
                itemLabel = { it.label },
                modifier = Modifier.width(120.dp),
                offset = IntOffset(x = popupOffsetX, y = popupOffsetY),
                alignment = Alignment.TopEnd,
            )
        } else {
            DropdownPopup(
                items = CustomOptionItem.entries.toImmutableList(),
                onSelect = { item ->
                    when (item) {
                        CustomOptionItem.SELECT -> onClickSelect()
                        CustomOptionItem.ADD_PHOTO -> onClickAddPhoto()
                        CustomOptionItem.RENAME_ALBUM -> onClickRenameAlbum()
                    }
                },
                onDismissRequest = onDismissPopup,
                itemLabel = { it.label },
                modifier = Modifier.width(120.dp),
                offset = IntOffset(x = popupOffsetX, y = popupOffsetY),
                alignment = Alignment.TopEnd,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun AlbumDetailTopBarFavoritePreview() {
    NekiTheme {
        AlbumDetailTopBar(
            isFavoriteAlbum = true,
            title = "즐겨찾기",
            selectMode = SelectMode.DEFAULT,
            showOptionPopup = false,
        )
    }
}

@ComponentPreview
@Composable
private fun AlbumDetailTopBarCustomPreview() {
    NekiTheme {
        AlbumDetailTopBar(
            isFavoriteAlbum = false,
            title = "앨범 이름",
            selectMode = SelectMode.DEFAULT,
            showOptionPopup = false,
        )
    }
}

@ComponentPreview
@Composable
private fun AlbumDetailTopBarSelectingPreview() {
    NekiTheme {
        AlbumDetailTopBar(
            isFavoriteAlbum = false,
            title = "앨범 이름",
            selectMode = SelectMode.SELECTING,
            showOptionPopup = false,
        )
    }
}

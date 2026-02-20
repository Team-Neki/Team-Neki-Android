package com.neki.android.feature.archive.impl.album.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.topbar.BackTitleTextButtonOptionTopBar
import com.neki.android.core.designsystem.topbar.BackTitleTextButtonTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.component.DropdownPopup
import com.neki.android.feature.archive.impl.model.SelectMode
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun AllAlbumTopBar(
    selectMode: SelectMode,
    showOptionPopup: Boolean,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit = {},
    onClickCreate: () -> Unit = {},
    onClickOption: () -> Unit = {},
    onDismissPopup: () -> Unit = {},
    onClickDeleteOption: () -> Unit = {},
    onClickDelete: () -> Unit = {},
) {
    Box {
        when (selectMode) {
            SelectMode.DEFAULT -> {
                DefaultTopBar(
                    modifier = modifier,
                    onClickBack = onClickBack,
                    onClickCreate = onClickCreate,
                    onClickOption = onClickOption,
                )
            }

            SelectMode.SELECTING -> {
                SelectingTopBar(
                    modifier = modifier,
                    onClickBack = onClickBack,
                    onClickDelete = onClickDelete,
                )
            }
        }

        if (showOptionPopup) {
            val density = LocalDensity.current
            val popupOffsetX = with(density) { (-20).dp.toPx().toInt() }
            val popupOffsetY = with(density) { 47.dp.toPx().toInt() }

            DropdownPopup(
                items = persistentListOf("삭제하기"),
                onSelect = {
                    onClickDeleteOption()
                    onDismissPopup()
                },
                onDismissRequest = onDismissPopup,
                itemLabel = { it },
                modifier = Modifier.width(158.dp),
                offset = IntOffset(x = popupOffsetX, y = popupOffsetY),
                alignment = Alignment.TopEnd,
            )
        }
    }
}

@Composable
private fun DefaultTopBar(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit = {},
    onClickCreate: () -> Unit = {},
    onClickOption: () -> Unit = {},
) {
    BackTitleTextButtonOptionTopBar(
        modifier = modifier,
        title = "모든 앨범",
        buttonLabel = "생성",
        optionIconRes = R.drawable.icon_option,
        onBack = onClickBack,
        onClickTextButton = onClickCreate,
        onClickIcon = onClickOption,
    )
}

@Composable
private fun SelectingTopBar(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit = {},
    onClickDelete: () -> Unit = {},
) {
    BackTitleTextButtonTopBar(
        modifier = modifier,
        title = "모든 앨범",
        buttonLabel = "삭제",
        onBack = onClickBack,
        onClickTextButton = onClickDelete,
    )
}

@ComponentPreview
@Composable
private fun AllAlbumTopBarDefaultPreview() {
    NekiTheme {
        AllAlbumTopBar(
            selectMode = SelectMode.DEFAULT,
            showOptionPopup = false,
        )
    }
}

@ComponentPreview
@Composable
private fun AllAlbumTopBarWithPopupPreview() {
    NekiTheme {
        AllAlbumTopBar(
            selectMode = SelectMode.DEFAULT,
            showOptionPopup = true,
        )
    }
}

@ComponentPreview
@Composable
private fun AllAlbumTopBarSelectingPreview() {
    NekiTheme {
        AllAlbumTopBar(
            selectMode = SelectMode.SELECTING,
            showOptionPopup = false,
        )
    }
}

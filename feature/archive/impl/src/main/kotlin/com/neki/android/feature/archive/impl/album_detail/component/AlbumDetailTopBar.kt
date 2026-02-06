package com.neki.android.feature.archive.impl.album_detail.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.topbar.BackTitleTextButtonTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.archive.impl.model.SelectMode

@Composable
internal fun AlbumDetailTopBar(
    title: String,
    selectMode: SelectMode,
    onClickBack: () -> Unit,
    onClickSelect: () -> Unit,
    onClickCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackTitleTextButtonTopBar(
        modifier = modifier,
        title = title,
        buttonLabel = when (selectMode) {
            SelectMode.DEFAULT -> "선택"
            SelectMode.SELECTING -> "취소"
        },
        enabledTextColor = when (selectMode) {
            SelectMode.DEFAULT -> NekiTheme.colorScheme.primary500
            SelectMode.SELECTING -> NekiTheme.colorScheme.gray800
        },
        onBack = onClickBack,
        onClickTextButton = when (selectMode) {
            SelectMode.DEFAULT -> onClickSelect
            SelectMode.SELECTING -> onClickCancel
        },
    )
}

@ComponentPreview
@Composable
private fun AlbumDetailTopBarPreview() {
    NekiTheme {
        AlbumDetailTopBar(
            title = "Album Title",
            selectMode = SelectMode.DEFAULT,
            onClickBack = {},
            onClickSelect = {},
            onClickCancel = {},
        )
    }
}

@ComponentPreview
@Composable
private fun AlbumDetailTopBarSelectingPreview() {
    NekiTheme {
        AlbumDetailTopBar(
            title = "Album Title",
            selectMode = SelectMode.SELECTING,
            onClickBack = {},
            onClickSelect = {},
            onClickCancel = {},
        )
    }
}

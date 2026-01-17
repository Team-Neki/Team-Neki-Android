package com.neki.android.feature.archive.impl.photo.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.topbar.BackTitleTextButtonTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.archive.impl.photo.PhotoSelectMode

@Composable
internal fun AllPhotoTopBar(
    selectMode: PhotoSelectMode,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
) {
    BackTitleTextButtonTopBar(
        modifier = modifier,
        title = "모든 사진",
        buttonLabel = when (selectMode) {
            PhotoSelectMode.DEFAULT -> "선택"
            PhotoSelectMode.SELECTING -> "취소"
        },
        buttonLabelTextColor = when (selectMode) {
            PhotoSelectMode.DEFAULT -> NekiTheme.colorScheme.primary500
            PhotoSelectMode.SELECTING -> NekiTheme.colorScheme.gray800
        },
        onBack = onBackClick,
        onTextButtonClick = when (selectMode) {
            PhotoSelectMode.DEFAULT -> onSelectClick
            PhotoSelectMode.SELECTING -> onCancelClick
        },
    )
}

@ComponentPreview
@Composable
private fun AllPhotoTopBarDefaultPreview() {
    NekiTheme {
        AllPhotoTopBar(
            selectMode = PhotoSelectMode.DEFAULT,
        )
    }
}

@ComponentPreview
@Composable
private fun AllPhotoTopBarSelectingPreview() {
    NekiTheme {
        AllPhotoTopBar(
            selectMode = PhotoSelectMode.SELECTING,
        )
    }
}

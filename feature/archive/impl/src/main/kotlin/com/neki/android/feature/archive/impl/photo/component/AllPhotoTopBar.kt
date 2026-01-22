package com.neki.android.feature.archive.impl.photo.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.topbar.BackTitleTextButtonTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.archive.impl.model.SelectMode

@Composable
internal fun AllPhotoTopBar(
    selectMode: SelectMode,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
) {
    BackTitleTextButtonTopBar(
        modifier = modifier,
        title = "모든 사진",
        buttonLabel = when (selectMode) {
            SelectMode.DEFAULT -> "선택"
            SelectMode.SELECTING -> "취소"
        },
        enabledTextColor = when (selectMode) {
            SelectMode.DEFAULT -> NekiTheme.colorScheme.primary500
            SelectMode.SELECTING -> NekiTheme.colorScheme.gray800
        },
        onBack = onBackClick,
        onTextButtonClick = when (selectMode) {
            SelectMode.DEFAULT -> onSelectClick
            SelectMode.SELECTING -> onCancelClick
        },
    )
}

@ComponentPreview
@Composable
private fun AllPhotoTopBarDefaultPreview() {
    NekiTheme {
        AllPhotoTopBar(
            selectMode = SelectMode.DEFAULT,
        )
    }
}

@ComponentPreview
@Composable
private fun AllPhotoTopBarSelectingPreview() {
    NekiTheme {
        AllPhotoTopBar(
            selectMode = SelectMode.SELECTING,
        )
    }
}

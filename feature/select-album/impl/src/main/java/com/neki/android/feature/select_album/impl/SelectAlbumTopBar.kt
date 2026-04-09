package com.neki.android.feature.select_album.impl

import androidx.compose.runtime.Composable
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.topbar.BackTitleTextButtonTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun SelectAlbumTopBar(
    title: String,
    photoCount: Int,
    isConfirmEnabled: Boolean,
    onBack: () -> Unit,
    onClickUpload: () -> Unit,
) {
    BackTitleTextButtonTopBar(
        title = title,
        buttonLabel = "${photoCount}장 업로드",
        enabled = isConfirmEnabled,
        onBack = onBack,
        onClickTextButton = onClickUpload,
    )
}

@DevicePreview
@Composable
private fun SelectAlbumTopBarPreview() {
    NekiTheme {
        SelectAlbumTopBar(
            title = "앨범에 추가",
            photoCount = 3,
            isConfirmEnabled = true,
            onBack = {},
            onClickUpload = {},
        )
    }
}

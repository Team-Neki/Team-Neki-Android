package com.neki.android.feature.photo_upload.impl.album.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.topbar.BackTitleTextButtonTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun UploadAlbumTopBar(
    count: Int,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit = {},
    onClickUpload: () -> Unit = {},
) {
    BackTitleTextButtonTopBar(
        modifier = modifier,
        title = "모든 앨범",
        buttonLabel = "${count}장 업로드",
        enabled = count != 0,
        onBack = onClickBack,
        onClickTextButton = onClickUpload,
    )
}

@ComponentPreview
@Composable
private fun UploadAlbumTopBarPreview() {
    NekiTheme {
        UploadAlbumTopBar(
            count = 5,
        )
    }
}

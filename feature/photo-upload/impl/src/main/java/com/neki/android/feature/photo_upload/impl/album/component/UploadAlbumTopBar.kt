package com.neki.android.feature.photo_upload.impl.album.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.topbar.BackTitleTextButtonTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun UploadAlbumTopBar(
    count: Int,
    enabled: Boolean = false,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onUploadClick: () -> Unit = {},
) {
    BackTitleTextButtonTopBar(
        modifier = modifier,
        title = "모든 앨범",
        buttonLabel = "${count}장 업로드",
        enabled = enabled,
        onBack = onBackClick,
        onTextButtonClick = onUploadClick,
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

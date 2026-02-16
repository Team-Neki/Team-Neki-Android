package com.neki.android.app.ui

import androidx.compose.runtime.Composable
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.component.SelectDialog
import kotlinx.collections.immutable.toImmutableList

internal enum class AlbumUploadOption(val label: String) {
    WITHOUT_ALBUM("앨범 없이 업로드하기"),
    WITH_ALBUM("앨범 선택 후 업로드하기"),
    ;

    override fun toString(): String = label
}

@Composable
internal fun SelectWithAlbumDialog(
    onDismissRequest: () -> Unit = {},
    onSelect: (AlbumUploadOption) -> Unit = {},
) {
    SelectDialog(
        options = AlbumUploadOption.entries.toImmutableList(),
        onDismissRequest = onDismissRequest,
        onSelect = onSelect,
    )
}

@ComponentPreview
@Composable
private fun SelectWithAlbumDialogPreview() {
    NekiTheme {
        SelectWithAlbumDialog()
    }
}

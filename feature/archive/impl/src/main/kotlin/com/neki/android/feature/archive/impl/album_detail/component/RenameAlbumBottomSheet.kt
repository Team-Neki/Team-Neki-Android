package com.neki.android.feature.archive.impl.album_detail.component

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.bottomsheet.NekiTextFieldBottomSheet
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.archive.impl.const.ArchiveConst.ARCHIVE_ALBUM_NAME_MAX_LENGTH

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RenameAlbumBottomSheet(
    textFieldState: TextFieldState,
    onDismissRequest: () -> Unit,
    onClickCancel: () -> Unit,
    onClickConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    isConfirmEnabled: Boolean = true,
) {
    NekiTextFieldBottomSheet(
        title = "앨범 이름 변경",
        subtitle = "변경할 앨범 이름을 입력하세요",
        textFieldState = textFieldState,
        onDismissRequest = onDismissRequest,
        onClickCancel = onClickCancel,
        onClickConfirm = onClickConfirm,
        modifier = modifier,
        placeholder = "앨범명을 입력하세요",
        maxLength = ARCHIVE_ALBUM_NAME_MAX_LENGTH,
        confirmButtonText = "변경하기",
        isError = isError,
        errorMessage = errorMessage,
        isConfirmEnabled = isConfirmEnabled,
    )
}

@ComponentPreview
@Composable
private fun RenameAlbumBottomSheetPreview() {
    NekiTheme {
        RenameAlbumBottomSheet(
            textFieldState = TextFieldState("앨범명"),
            onDismissRequest = {},
            onClickCancel = {},
            onClickConfirm = {},
        )
    }
}

@ComponentPreview
@Composable
private fun RenameAlbumBottomSheetErrorPreview() {
    NekiTheme {
        RenameAlbumBottomSheet(
            textFieldState = TextFieldState("즐겨찾기즐겨찾기즐겨찾기"),
            onDismissRequest = {},
            onClickCancel = {},
            onClickConfirm = {},
        )
    }
}

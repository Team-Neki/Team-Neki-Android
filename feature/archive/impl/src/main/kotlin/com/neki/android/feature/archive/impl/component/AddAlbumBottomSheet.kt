package com.neki.android.feature.archive.impl.component

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
internal fun AddAlbumBottomSheet(
    textFieldState: TextFieldState,
    onDismissRequest: () -> Unit,
    onClickCancel: () -> Unit,
    onClickConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    NekiTextFieldBottomSheet(
        title = "새 앨범 추가",
        subtitle = "네컷사진을 모을 앨범명을 입력하세요",
        textFieldState = textFieldState,
        onDismissRequest = onDismissRequest,
        onClickCancel = onClickCancel,
        onClickConfirm = onClickConfirm,
        modifier = modifier,
        placeholder = "앨범명을 입력하세요",
        maxLength = ARCHIVE_ALBUM_NAME_MAX_LENGTH,
        confirmButtonText = "추가하기",
        isError = isError,
        errorMessage = errorMessage,
    )
}

@ComponentPreview
@Composable
private fun AddAlbumBottomSheetPreview() {
    NekiTheme {
        AddAlbumBottomSheet(
            textFieldState = TextFieldState(),
            onDismissRequest = {},
            onClickCancel = {},
            onClickConfirm = {},
        )
    }
}

@ComponentPreview
@Composable
private fun AddAlbumBottomSheetErrorPreview() {
    NekiTheme {
        AddAlbumBottomSheet(
            textFieldState = TextFieldState(),
            onDismissRequest = {},
            onClickCancel = {},
            onClickConfirm = {},
            isError = true,
            errorMessage = "앨범명은 최대 10자까지 입력할 수 있어요.",
        )
    }
}

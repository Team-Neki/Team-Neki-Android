package com.neki.android.feature.archive.impl.main.component

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.bottomsheet.NekiTextFieldBottomSheet
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.archive.impl.main.AlbumNameErrorState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddAlbumBottomSheet(
    textFieldState: TextFieldState,
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
    albumNameErrorState: AlbumNameErrorState? = null,
) {
    NekiTextFieldBottomSheet(
        title = "새 앨범 추가",
        subtitle = "네컷사진을 모을 앨범명을 입력하세요",
        textFieldState = textFieldState,
        onDismissRequest = onDismissRequest,
        onCancelClick = onCancelClick,
        onConfirmClick = onConfirmClick,
        modifier = modifier,
        placeholder = "앨범명을 입력하세요",
        maxLength = 16,
        confirmButtonText = "추가하기",
        isError = albumNameErrorState != null,
        errorMessage = albumNameErrorState?.message,
    )
}

@ComponentPreview
@Composable
private fun AddAlbumBottomSheetPreview() {
    NekiTheme {
        AddAlbumBottomSheet(
            textFieldState = TextFieldState(),
            onDismissRequest = {},
            onCancelClick = {},
            onConfirmClick = {},
        )
    }
}

@ComponentPreview
@Composable
private fun AddAlbumBottomSheetExceedLengthPreview() {
    NekiTheme {
        AddAlbumBottomSheet(
            textFieldState = TextFieldState(),
            onDismissRequest = {},
            onCancelClick = {},
            onConfirmClick = {},
            albumNameErrorState = AlbumNameErrorState.EXCEED_LENGTH,
        )
    }
}

@ComponentPreview
@Composable
private fun AddAlbumBottomSheetInvalidCharacterPreview() {
    NekiTheme {
        AddAlbumBottomSheet(
            textFieldState = TextFieldState(),
            onDismissRequest = {},
            onCancelClick = {},
            onConfirmClick = {},
            albumNameErrorState = AlbumNameErrorState.INVALID_CHARACTER,
        )
    }
}

@ComponentPreview
@Composable
private fun AddAlbumBottomSheetDuplicatePreview() {
    NekiTheme {
        AddAlbumBottomSheet(
            textFieldState = TextFieldState(),
            onDismissRequest = {},
            onCancelClick = {},
            onConfirmClick = {},
            albumNameErrorState = AlbumNameErrorState.DUPLICATE,
        )
    }
}

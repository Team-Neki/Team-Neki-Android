package com.neki.android.feature.archive.impl.photo.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.dialog.DoubleButtonAlertDialog
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun DeletePhotoDialog(
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
    onCancelClick: () -> Unit,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
) {
    DoubleButtonAlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        title = "사진을 삭제하시겠어요?",
        content = "이 작업은 실행취소할 수 없어요",
        grayButtonText = "취소",
        primaryButtonText = "삭제하기",
        onDismissRequest = onDismissRequest,
        onPrimaryButtonClick = onDeleteClick,
        onGrayButtonClick = onCancelClick,
        properties = properties,
    )
}

@ComponentPreview
@Composable
private fun DeletePhotoDialogPreview() {
    NekiTheme {
        DeletePhotoDialog(
            onDismissRequest = {},
            onDeleteClick = {},
            onCancelClick = {},
        )
    }
}

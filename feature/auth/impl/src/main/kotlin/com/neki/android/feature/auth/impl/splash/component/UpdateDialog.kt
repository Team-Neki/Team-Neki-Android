package com.neki.android.feature.auth.impl.splash.component

import androidx.compose.runtime.Composable
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.dialog.DoubleButtonAlertDialog
import com.neki.android.core.designsystem.dialog.SingleButtonAlertDialog
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun RequiredUpdateDialog(
    onClickUpdate: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    SingleButtonAlertDialog(
        title = "필수 업데이트 안내",
        content = "더 안정적인 서비스 이용을 위해\n필수 업데이트가 필요해요",
        buttonText = "업데이트 하러 가기",
        onDismissRequest = onDismissRequest,
        onClick = onClickUpdate,
    )
}

@Composable
internal fun OptionalUpdateDialog(
    onDismissRequest: () -> Unit = {},
    onClickUpdate: () -> Unit = {},
    onClickDismiss: () -> Unit = {},
) {
    DoubleButtonAlertDialog(
        title = "새로운 버전 업데이트",
        content = "새로운 기능이 추가되었어요!\n더 나은 이용을 위해 업데이트 해보세요",
        grayButtonText = "다음에 할래요",
        primaryButtonText = "업데이트",
        onDismissRequest = onDismissRequest,
        onClickGrayButton = onClickDismiss,
        onClickPrimaryButton = onClickUpdate,
    )
}

@ComponentPreview
@Composable
private fun RequiredUpdateDialogPreview() {
    NekiTheme {
        RequiredUpdateDialog()
    }
}

@ComponentPreview
@Composable
private fun OptionalUpdateDialogPreview() {
    NekiTheme {
        OptionalUpdateDialog()
    }
}

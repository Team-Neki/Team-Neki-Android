package com.neki.android.core.ui.component

import androidx.compose.runtime.Composable
import com.neki.android.core.common.permission.NotificationGuideType
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.dialog.DoubleButtonAlertDialog
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun NotificationGuideDialog(
    type: NotificationGuideType,
    onDismissRequest: () -> Unit = {},
    onClickConfirm: () -> Unit = {},
    onClickDismiss: () -> Unit = {},
) {
    DoubleButtonAlertDialog(
        title = "놓치고 있던 소식들이 있어요!",
        content = type.content,
        grayButtonText = "다음에 하기",
        primaryButtonText = type.confirmButtonText,
        onDismissRequest = onDismissRequest,
        onClickPrimaryButton = onClickConfirm,
        onClickGrayButton = onClickDismiss,
        imageRes = R.drawable.image_dialog_notification,
    )
}

private val NotificationGuideType.content: String
    get() = when (this) {
        NotificationGuideType.INTEGRATED -> "새로운 소식과 콘텐츠를 놓치지 않도록\n알림을 켜주세요.\n(광고성 정보 수신 동의 포함)"
        NotificationGuideType.ENABLE_NOTIFICATION -> "새로운 소식과 콘텐츠를 놓치지 않도록\n기기 알림을 켜주세요."
        NotificationGuideType.MARKETING_CONSENT -> "새로운 브랜드·이벤트 소식을\n알림으로 받아보세요.\n(광고성 정보 수신 동의 포함)"
        NotificationGuideType.OPEN_SETTINGS -> "설정 > 알림에서 켤 수 있어요."
    }

private val NotificationGuideType.confirmButtonText: String
    get() = when (this) {
        NotificationGuideType.OPEN_SETTINGS -> "설정에서 켜기"
        else -> "알림 받기"
    }

@ComponentPreview
@Composable
private fun NotificationGuideDialogIntegratedPreview() {
    NekiTheme {
        NotificationGuideDialog(type = NotificationGuideType.INTEGRATED)
    }
}

@ComponentPreview
@Composable
private fun NotificationGuideDialogEnableNotificationPreview() {
    NekiTheme {
        NotificationGuideDialog(type = NotificationGuideType.ENABLE_NOTIFICATION)
    }
}

@ComponentPreview
@Composable
private fun NotificationGuideDialogMarketingConsentPreview() {
    NekiTheme {
        NotificationGuideDialog(type = NotificationGuideType.MARKETING_CONSENT)
    }
}

@ComponentPreview
@Composable
private fun NotificationGuideDialogOpenSettingsPreview() {
    NekiTheme {
        NotificationGuideDialog(type = NotificationGuideType.OPEN_SETTINGS)
    }
}

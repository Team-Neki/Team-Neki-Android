package com.neki.android.feature.archive.impl.main.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.dialog.DoubleButtonSubContentDialog
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun MarketingAgreementDialog(
    onDismissRequest: () -> Unit = {},
    onClickConfirm: () -> Unit = {},
    onClickDismiss: () -> Unit = {},
) {
    DoubleButtonSubContentDialog(
        title = "놓치지 마세요!",
        content = "네키의 이벤트, 혜택, 프로모션,\n신규 업데이트 소식을 선별해서 알려드려요.",
        grayButtonText = "괜찮아요",
        primaryButtonText = "네, 알려주세요",
        onDismissRequest = onDismissRequest,
        onClickPrimaryButton = onClickConfirm,
        onClickGrayButton = onClickDismiss,
        subContent = {
            val highlightText = "마이페이지 > 권한 설정 > 알림 설정"
            val fullText = "마케팅 정보 푸시 수신 동의 여부는 ${highlightText}에서 변경 가능해요."
            val annotatedText = buildAnnotatedString {
                val highlightStart = fullText.indexOf(highlightText)
                append(fullText.substring(0, highlightStart))
                withStyle(SpanStyle(color = NekiTheme.colorScheme.primary400)) {
                    append(highlightText)
                }
                append(fullText.substring(highlightStart + highlightText.length))
            }
            Text(
                text = annotatedText,
                style = NekiTheme.typography.body14Medium,
                color = NekiTheme.colorScheme.gray900,
                textAlign = TextAlign.Center,
            )
        },
    )
}

@ComponentPreview
@Composable
private fun MarketingAgreementDialogPreview() {
    NekiTheme {
        MarketingAgreementDialog()
    }
}

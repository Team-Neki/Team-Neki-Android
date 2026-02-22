package com.neki.android.feature.photo_upload.impl.qrscan.component

import android.graphics.RectF
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.neki.android.core.common.permission.NekiPermission
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.button.NekiIconButton
import com.neki.android.core.designsystem.dialog.DoubleButtonAlertDialog
import com.neki.android.core.designsystem.dialog.SingleButtonAlertDialog
import com.neki.android.core.designsystem.dialog.SingleButtonWithTextButtonAlertDialog
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.photo_upload.impl.qrscan.QRScanIntent
import com.neki.android.feature.photo_upload.impl.qrscan.const.QRScanConst

@Composable
internal fun QRScannerContent(
    modifier: Modifier = Modifier,
    isPermissionRationaleDialogShown: Boolean = false,
    isOpenAppSettingDialogShown: Boolean = false,
    isDownloadNeededDialogShown: Boolean = false,
    isUnSupportedBrandDialogShown: Boolean = false,
    isTorchEnabled: Boolean = false,
    onIntent: (QRScanIntent) -> Unit = {},
) {
    var frameOffset: Offset? by remember { mutableStateOf(null) }
    var frameSize: IntSize? by remember { mutableStateOf(null) }
    var containerSize: IntSize? by remember { mutableStateOf(null) }
    var isNavigatedToSettings by rememberSaveable { mutableStateOf(false) }

    LifecycleResumeEffect(Unit) {
        if (isNavigatedToSettings) {
            onIntent(QRScanIntent.RequestCameraPermission)
            isNavigatedToSettings = false
        }

        onPauseOrDispose { }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                containerSize = coordinates.size
            },
    ) {
        QRScanner(
            modifier = Modifier.fillMaxSize(),
            isTorchEnabled = isTorchEnabled,
            scanAreaRatio = {
                val offset = frameOffset
                val frame = frameSize
                val container = containerSize
                if (offset != null && frame != null && container != null &&
                    container.width > 0 && container.height > 0
                ) {
                    RectF(
                        offset.x / container.width,
                        offset.y / container.height,
                        (offset.x + frame.width) / container.width,
                        (offset.y + frame.height) / container.height,
                    )
                } else {
                    null
                }
            },
            onQRCodeScanned = { url -> onIntent(QRScanIntent.ScanQRCode(url)) },
        )
        DimExceptContent(
            modifier = Modifier.fillMaxSize(),
            offSet = frameOffset,
            size = frameSize,
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 상단 영역 - weight(234f)
            Column(modifier = Modifier.weight(234f)) {
                // Scanner 상단 영역에서부터 (32-3)만큼 떨어짐
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    QRCodeText(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 29.dp),
                    )
                }
            }

            // ScanCornerFrame (고정 크기)
            ScanCornerFrame(
                modifier = Modifier
                    .size(304.dp)
                    .onGloballyPositioned { coordinates ->
                        frameOffset = coordinates.positionInParent()
                        frameSize = coordinates.size
                    },
                color = Color.White,
            )

            // 하단 영역 - weight(274f)
            Box(
                modifier = Modifier.weight(274f),
                contentAlignment = Alignment.TopCenter,
            ) {
                // Scanner 하단 영역에서부터 (40-3)만큼 떨어짐
                NekiIconButton(
                    modifier = Modifier
                        .padding(top = 37.dp)
                        .size(48.dp)
                        .background(
                            color = NekiTheme.colorScheme.white.copy(alpha = 0.1f),
                            shape = CircleShape,
                        ),
                    onClick = { onIntent(QRScanIntent.ToggleTorch) },
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        imageVector = ImageVector.vectorResource(
                            if (isTorchEnabled) R.drawable.icon_torch_off
                            else R.drawable.icon_torch_on,
                        ),
                        contentDescription = null,
                        tint = NekiTheme.colorScheme.white,
                    )
                }
            }
        }
    }

    if (isPermissionRationaleDialogShown) {
        DoubleButtonAlertDialog(
            title = NekiPermission.CAMERA.dialogTitle,
            content = NekiPermission.CAMERA.dialogContent,
            grayButtonText = "취소",
            primaryButtonText = "허용",
            onDismissRequest = { onIntent(QRScanIntent.DismissPermissionRationaleDialog) },
            onClickGrayButton = { onIntent(QRScanIntent.ClickPermissionRationaleDialogCancel) },
            onClickPrimaryButton = { onIntent(QRScanIntent.ClickPermissionRationaleDialogConfirm) },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
        )
    }

    if (isOpenAppSettingDialogShown) {
        DoubleButtonAlertDialog(
            title = "카메라 권한",
            content = NekiPermission.CAMERA_PERMANENT_DENIED_DIALOG_CONTENT,
            grayButtonText = "취소",
            primaryButtonText = QRScanConst.OPEN_APP_SETTING_DIALOG_BUTTON_TEXT,
            onDismissRequest = { onIntent(QRScanIntent.DismissOpenAppSettingDialog) },
            onClickGrayButton = { onIntent(QRScanIntent.ClickOpenAppSettingDialogCancel) },
            onClickPrimaryButton = {
                onIntent(QRScanIntent.ClickOpenAppSettingDialogConfirm)
                isNavigatedToSettings = true
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
        )
    }

    if (isDownloadNeededDialogShown) {
        SingleButtonAlertDialog(
            title = "갤러리에 사진을 먼저 다운받아주세요",
            content = "해당 브랜드는 웹사이트에서 사진을 저장해야\n네키에 자동으로 저장돼요",
            buttonText = "사진 다운로드하러가기",
            onDismissRequest = { onIntent(QRScanIntent.DismissShouldDownloadDialog) },
            onClick = { onIntent(QRScanIntent.ClickGoDownload) },
        )
    }

    if (isUnSupportedBrandDialogShown) {
        SingleButtonWithTextButtonAlertDialog(
            title = "지원하지 않는 브랜드예요",
            content = "갤러리에서 사진을 추가해 바로 저장할 수 있어요\n원하는 브랜드가 있다면 제안해주세요!",
            buttonText = "갤러리에서 추가하기",
            textButtonText = "브랜드 제안하기",
            onDismissRequest = { onIntent(QRScanIntent.DismissUnSupportedBrandDialog) },
            onButtonClick = { onIntent(QRScanIntent.ClickUploadGallery) },
            onTextButtonClick = { onIntent(QRScanIntent.ClickProposeBrand) },
        )
    }
}

@Composable
private fun QRCodeText(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            NekiTheme.colorScheme.primary500,
                            NekiTheme.colorScheme.primary100,
                        ),
                    ),
                ),
            ) {
                append("QR을 스캔")
            }
            withStyle(SpanStyle(color = Color.White)) {
                append("하면\n보관함에 자동 저장돼요!")
            }
        },
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 36.sp,
        textAlign = TextAlign.Center,
    )
}

@Preview(showBackground = true)
@Composable
private fun QRScannerContentPreview() {
    NekiTheme {
        QRScannerContent()
    }
}

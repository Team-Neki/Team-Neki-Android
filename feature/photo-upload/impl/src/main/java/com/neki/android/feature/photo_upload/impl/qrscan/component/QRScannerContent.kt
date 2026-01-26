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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.button.NekiIconButton
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun QRScannerContent(
    modifier: Modifier = Modifier,
    isTorchEnabled: Boolean = false,
    onClickTorch: () -> Unit = {},
    onClickClose: () -> Unit = {},
    onQRCodeScanned: (String) -> Unit = {},
) {
    var frameOffset: Offset? by remember { mutableStateOf(null) }
    var frameSize: IntSize? by remember { mutableStateOf(null) }
    var containerSize: IntSize? by remember { mutableStateOf(null) }

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
            onQRCodeScanned = onQRCodeScanned,
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
                // TopBar (고정 크기)
                Box(modifier = Modifier.fillMaxWidth()) {
                    NekiIconButton(
                        modifier = Modifier.padding(start = 8.dp),
                        onClick = onClickClose,
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.icon_close),
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                }

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
                        .clip(CircleShape)
                        .background(
                            if (isTorchEnabled) Color.White
                            else Color.White.copy(alpha = 0.1f),
                        ),
                    onClick = onClickTorch,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.icon_qr_light),
                        contentDescription = null,
                        tint = if (isTorchEnabled) Color.Black else Color.White,
                    )
                }
            }
        }
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

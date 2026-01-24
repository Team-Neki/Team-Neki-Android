package com.neki.android.feature.photo_upload.impl.qrscan.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.constraintlayout.compose.ConstraintLayout
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

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        QRScanner(
            modifier = Modifier.fillMaxSize(),
            isTorchEnabled = isTorchEnabled,
            onQRCodeScanned = onQRCodeScanned,
        )
        DimExceptContent(
            modifier = Modifier.fillMaxSize(),
            offSet = frameOffset,
            size = frameSize,
        )
        ConstraintLayout(
            modifier = Modifier.fillMaxSize(),
        ) {
            val (topBar, text, scanFrame, torchButton) = createRefs()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(topBar) {
                        top.linkTo(parent.top)
                    },
            ) {
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

            QRCodeText(
                modifier = Modifier.constrainAs(text) {
                    top.linkTo(topBar.bottom)
                    bottom.linkTo(scanFrame.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            )

            ScanCornerFrame(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .aspectRatio(1f)
                    .constrainAs(scanFrame) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        verticalBias = 234f / (234f + 274f)
                    }
                    .onGloballyPositioned { coordinates ->
                        frameOffset = coordinates.positionInParent()
                        frameSize = coordinates.size
                    },
                color = Color.White,
            )

            NekiIconButton(
                modifier = Modifier
                    .constrainAs(torchButton) {
                        top.linkTo(scanFrame.bottom, margin = 37.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
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

@Composable
private fun QRCodeText(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFF311F),
                            Color(0xFFFFDAD6),
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

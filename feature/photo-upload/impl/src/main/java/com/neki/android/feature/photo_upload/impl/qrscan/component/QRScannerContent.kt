package com.neki.android.feature.photo_upload.impl.qrscan.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.VerticalSpacer

@Composable
internal fun QRScannerContent(
    modifier: Modifier = Modifier,
    isTorchEnabled: Boolean = false,
    onClickTorch: () -> Unit = {},
    onClickClose: () -> Unit = {},
    onQRCodeScanned: (String) -> Unit = {},
) {
    val cutoutSize = LocalConfiguration.current.screenWidthDp.dp * 0.82f

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        QRScanner(
            isTorchEnabled = isTorchEnabled,
            onQRCodeScanned = onQRCodeScanned,
        )
        DimExceptContent(
            modifier = Modifier.fillMaxSize(),
            cutout = { cutoutModifier ->
                Box(
                    cutoutModifier
                        .fillMaxWidth(0.82f)
                        .aspectRatio(1f)
                        .align(Alignment.Center),
                )
            },
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .padding(bottom = 20.dp)
                .aspectRatio(1f)
                .align(Alignment.Center),
        ) {
            ScanCornerFrame(
                modifier = Modifier.fillMaxSize(),
                color = Color.White,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
        ) {
            IconButton(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
                onClick = onClickClose,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.icon_close),
                    contentDescription = null,
                    tint = Color.White,
                )
            }
            VerticalSpacer(32.dp)
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
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

        IconButton(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = cutoutSize / 2 + 40.dp)
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

@Preview(showBackground = true)
@Composable
private fun QRScannerContentPreview() {
    NekiTheme {
        QRScannerContent()
    }
}

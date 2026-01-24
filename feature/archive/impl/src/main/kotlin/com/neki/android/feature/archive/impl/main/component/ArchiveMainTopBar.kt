package com.neki.android.feature.archive.impl.main.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.button.NekiIconButton
import com.neki.android.core.designsystem.extension.buttonShadow
import com.neki.android.core.designsystem.topbar.NekiLeftTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.archive.impl.R as ArchiveR

@Composable
internal fun ArchiveMainTopBar(
    showAddPopup: Boolean,
    modifier: Modifier = Modifier,
    onPlusIconClick: () -> Unit = {},
    onNotificationIconClick: () -> Unit = {},
    onQRScanClick: () -> Unit = {},
    onGalleryClick: () -> Unit = {},
    onNewAlbumClick: () -> Unit = {},
    onDismissPopup: () -> Unit = {},
    showTooltip: Boolean = true,
) {
    NekiLeftTitleTopBar(
        modifier = modifier,
        title = {
            Box(
                modifier = Modifier
                    .height(28.dp)
                    .width(56.dp)
                    .background(color = Color(0xFFB7B9C3)),
            )
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box {
                    NekiIconButton(
                        onClick = onPlusIconClick,
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.icon_plus_primary),
                            contentDescription = null,
                            tint = Color.Unspecified,
                        )
                    }

                    if (showAddPopup) {
                        AddPhotoPopup(
                            onDismissRequest = onDismissPopup,
                            onQRScanClick = onQRScanClick,
                            onGalleryClick = onGalleryClick,
                            onNewAlbumClick = onNewAlbumClick,
                        )
                    }
                    if (showTooltip) {
                        ToolTip()
                    }
                }
                NekiIconButton(
                    onClick = onNotificationIconClick,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.icon_bell),
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )
                }
            }
        },
    )
}

@Composable
private fun ToolTip() {
    val caretColor = NekiTheme.colorScheme.gray800
    val density = LocalDensity.current

    val popupOffsetX = with(density) { 1.dp.toPx().toInt() }
    val popupOffsetY = with(density) { 47.dp.toPx().toInt() }

    Popup(
        alignment = Alignment.TopEnd,
        offset = IntOffset(popupOffsetX, popupOffsetY),
    ) {
        Column(
            modifier = Modifier.width(IntrinsicSize.Max),
        ) {
            // 꼬리 (오른쪽 정렬, 오른쪽에서 16dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Canvas(modifier = Modifier.size(width = 10.dp, height = 8.dp)) {
                    val cornerRadius = 2.dp.toPx()
                    val path = Path().apply {
                        // 왼쪽 하단에서 시작
                        moveTo(0f, size.height)
                        // 왼쪽 하단 -> 꼭대기 (둥근 모서리)
                        lineTo(
                            size.width / 2 - cornerRadius,
                            cornerRadius,
                        )
                        quadraticTo(
                            size.width / 2,
                            0f,
                            size.width / 2 + cornerRadius,
                            cornerRadius,
                        )
                        // 꼭대기 -> 오른쪽 하단
                        lineTo(size.width, size.height)
                        close()
                    }
                    drawPath(path, caretColor)
                }
            }

            // 몸통
            Box(
                modifier = Modifier
                    .background(
                        color = NekiTheme.colorScheme.gray800,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "버튼을 눌러 네컷을 추가할 수 있어요",
                    style = NekiTheme.typography.body14Medium,
                    color = NekiTheme.colorScheme.white,
                )
            }
        }
    }
}

@Composable
private fun AddPhotoPopup(
    onDismissRequest: () -> Unit,
    onQRScanClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onNewAlbumClick: () -> Unit,
) {
    val density = LocalDensity.current
    val popupOffsetX = with(density) { (-4).dp.toPx().toInt() }
    val popupOffsetY = with(density) { 48.dp.toPx().toInt() }

    Popup(
        offset = IntOffset(x = popupOffsetX, y = popupOffsetY),
        alignment = Alignment.TopEnd,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        Column(
            modifier = Modifier
                .buttonShadow(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.2f),
                    offsetX = 0.dp,
                    offsetY = 0.dp,
                    blurRadius = 5.dp,
                )
                .background(
                    color = NekiTheme.colorScheme.white,
                    shape = RoundedCornerShape(12.dp),
                )
                .width(IntrinsicSize.Max)
                .padding(vertical = 8.dp),
        ) {
            AddPhotoRow(
                modifier = Modifier.fillMaxWidth(),
                iconResource = ArchiveR.drawable.icon_qrcode_scan,
                label = "QR 인식",
                onClick = onQRScanClick,
            )
            AddPhotoRow(
                modifier = Modifier.fillMaxWidth(),
                iconResource = ArchiveR.drawable.icon_upload_gallery,
                label = "갤러리에서 추가",
                onClick = onGalleryClick,
            )
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                thickness = 1.dp,
                color = NekiTheme.colorScheme.gray50,
            )
            AddPhotoRow(
                modifier = Modifier.fillMaxWidth(),
                iconResource = ArchiveR.drawable.icon_add_new_album,
                label = "새 앨범 추가",
                onClick = onNewAlbumClick,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun ArchiveMainTopBarPreview() {
    NekiTheme {
        Column(
            modifier = Modifier.padding(bottom = 50.dp),
        ) {
            ArchiveMainTopBar(
                modifier = Modifier.padding(start = 20.dp, end = 8.dp),
                showAddPopup = false,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun ArchiveMainTopBarWithTooltipPreview() {
    NekiTheme {
        Box(modifier = Modifier.height(200.dp)) {
            ArchiveMainTopBar(
                modifier = Modifier.padding(start = 20.dp, end = 8.dp),
                showTooltip = true,
                showAddPopup = false,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun ArchiveMainTopBarWithPopupPreview() {
    NekiTheme {
        Box(modifier = Modifier.height(200.dp)) {
            ArchiveMainTopBar(
                modifier = Modifier.padding(start = 20.dp, end = 8.dp),
                showAddPopup = true,
            )
        }
    }
}

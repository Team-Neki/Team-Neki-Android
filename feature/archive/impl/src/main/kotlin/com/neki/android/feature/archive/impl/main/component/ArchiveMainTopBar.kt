package com.neki.android.feature.archive.impl.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.neki.android.core.designsystem.logo.PrimaryNekiTypoLogo
import com.neki.android.core.designsystem.modifier.dropdownShadow
import com.neki.android.core.designsystem.popup.ToolTipPopup
import com.neki.android.core.designsystem.topbar.NekiLeftTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.archive.impl.R as ArchiveR

@Composable
internal fun ArchiveMainTopBar(
    showAddPopup: Boolean,
    modifier: Modifier = Modifier,
    onClickPlusIcon: () -> Unit = {},
    onClickNotificationIcon: () -> Unit = {},
    onClickQRScan: () -> Unit = {},
    onClickGallery: () -> Unit = {},
    onClickNewAlbum: () -> Unit = {},
    onDismissPopup: () -> Unit = {},
    showTooltip: Boolean = true,
) {
    NekiLeftTitleTopBar(
        modifier = modifier,
        title = {
            PrimaryNekiTypoLogo()
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box {
                    NekiIconButton(
                        onClick = onClickPlusIcon,
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
                            onClickQRScan = onClickQRScan,
                            onClickGallery = onClickGallery,
                            onClickNewAlbum = onClickNewAlbum,
                        )
                    }
                    if (showTooltip) {
                        ArchiveToolTip()
                    }
                }
                NekiIconButton(
                    onClick = onClickNotificationIcon,
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
private fun ArchiveToolTip() {
    val density = LocalDensity.current
    val popupOffsetX = with(density) { 1.dp.toPx().toInt() }
    val popupOffsetY = with(density) { 47.dp.toPx().toInt() }
    val offset = IntOffset(popupOffsetX, popupOffsetY)

    ToolTipPopup(
        tooltipText = "버튼을 눌러 네컷을 추가할 수 있어요",
        color = NekiTheme.colorScheme.gray800,
        offset = offset,
        alignment = Alignment.TopEnd,
    )
}

@Composable
private fun AddPhotoPopup(
    onDismissRequest: () -> Unit,
    onClickQRScan: () -> Unit,
    onClickGallery: () -> Unit,
    onClickNewAlbum: () -> Unit,
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
                .dropdownShadow(shape = RoundedCornerShape(12.dp))
                .background(
                    color = NekiTheme.colorScheme.white,
                    shape = RoundedCornerShape(12.dp),
                )
                .width(IntrinsicSize.Max)
                .padding(vertical = 8.dp),
        ) {
            AddPhotoRow(
                modifier = Modifier.fillMaxWidth(),
                iconRes = ArchiveR.drawable.icon_qrcode_scan,
                label = "QR 인식",
                onClick = onClickQRScan,
            )
            AddPhotoRow(
                modifier = Modifier.fillMaxWidth(),
                iconRes = ArchiveR.drawable.icon_upload_gallery,
                label = "갤러리에서 추가",
                onClick = onClickGallery,
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
                iconRes = ArchiveR.drawable.icon_add_new_album,
                label = "새 앨범 추가",
                onClick = onClickNewAlbum,
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

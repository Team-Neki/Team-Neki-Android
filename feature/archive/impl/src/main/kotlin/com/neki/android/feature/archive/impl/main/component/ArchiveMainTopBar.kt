package com.neki.android.feature.archive.impl.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.draw.shadow
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
import com.neki.android.core.designsystem.extension.clickableSingle
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box {
                    Icon(
                        modifier = Modifier.clickableSingle(onClick = onPlusIconClick),
                        imageVector = ImageVector.vectorResource(R.drawable.icon_plus_primary),
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )

                    if (showAddPopup) {
                        AddPhotoPopup(
                            onDismissRequest = onDismissPopup,
                            onQRScanClick = onQRScanClick,
                            onGalleryClick = onGalleryClick,
                            onNewAlbumClick = onNewAlbumClick,
                        )
                    }
                }
                Icon(
                    modifier = Modifier.clickableSingle(onClick = onNotificationIconClick),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_bell),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
            }
        },
    )
}

@Composable
private fun AddPhotoPopup(
    onDismissRequest: () -> Unit,
    onQRScanClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onNewAlbumClick: () -> Unit,
) {
    val density = LocalDensity.current
    val popupOffsetY = with(density) { 31.dp.toPx().toInt() }

    Popup(
        offset = IntOffset(x = 0, y = popupOffsetY),
        alignment = Alignment.TopEnd,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        Column(
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(12.dp))
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
        ArchiveMainTopBar(
            showAddPopup = false,
        )
    }
}

@ComponentPreview
@Composable
private fun ArchiveMainTopBarWithPopupPreview() {
    NekiTheme {
        Box(modifier = Modifier.height(200.dp)) {
            ArchiveMainTopBar(
                showAddPopup = true,
            )
        }
    }
}

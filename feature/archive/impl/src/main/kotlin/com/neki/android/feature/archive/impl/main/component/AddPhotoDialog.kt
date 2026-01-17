package com.neki.android.feature.archive.impl.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.archive.impl.R

@Composable
internal fun AddPhotoDialog(
    onQRScanClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    color = NekiTheme.colorScheme.white,
                )
                .padding(vertical = 8.dp),
        ) {
            AddPhotoRow(
                iconResource = R.drawable.icon_qrcode_scan,
                label = "QR 인식",
                onClick = onQRScanClick,
            )
            AddPhotoRow(
                iconResource = R.drawable.icon_upload_gallery,
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
                iconResource = R.drawable.icon_add_new_album,
                label = "새 앨범 추가",
                onClick = onGalleryClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

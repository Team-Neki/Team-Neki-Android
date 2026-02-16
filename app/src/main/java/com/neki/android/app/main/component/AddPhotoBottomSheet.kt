package com.neki.android.app.main.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.bottomsheet.BottomSheetDragHandle
import com.neki.android.core.designsystem.modifier.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.archive.impl.R as ArchiveR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddPhotoBottomSheet(
    onDismissRequest: () -> Unit,
    onClickQRScan: () -> Unit,
    onClickGallery: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = NekiTheme.colorScheme.white,
        dragHandle = { BottomSheetDragHandle(color = NekiTheme.colorScheme.gray100) },
    ) {
        AddPhotoBottomSheetContent(
            onClickQRScan = onClickQRScan,
            onClickGallery = onClickGallery,
        )
    }
}

@Composable
private fun AddPhotoBottomSheetContent(
    onClickQRScan: () -> Unit = {},
    onClickGallery: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 44.dp),
    ) {
        Text(
            text = "네컷사진 추가",
            style = NekiTheme.typography.title20SemiBold,
            color = NekiTheme.colorScheme.gray900,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(36.dp, Alignment.CenterHorizontally),
        ) {
            AddPhotoOptionButton(
                iconRes = ArchiveR.drawable.icon_qrcode_scan,
                label = "QR로 추가",
                onClick = onClickQRScan,
            )
            AddPhotoOptionButton(
                iconRes = ArchiveR.drawable.icon_upload_gallery,
                label = "갤러리에서 추가",
                onClick = onClickGallery,
            )
        }
    }
}

@Composable
private fun AddPhotoOptionButton(
    @DrawableRes iconRes: Int,
    label: String,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .noRippleClickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .border(
                    width = 1.dp,
                    color = NekiTheme.colorScheme.gray50,
                    shape = RoundedCornerShape(12.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = ImageVector.vectorResource(iconRes),
                contentDescription = null,
                tint = NekiTheme.colorScheme.gray700,
            )
        }
        Text(
            text = label,
            style = NekiTheme.typography.body14Medium,
            color = NekiTheme.colorScheme.gray700,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddPhotoBottomSheetContentPreview() {
    NekiTheme {
        AddPhotoBottomSheetContent()
    }
}

package com.neki.android.feature.mypage.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.extension.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun PermissionSectionItem(
    title: String,
    subTitle: String,
    isGranted: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickableSingle(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                color = NekiTheme.colorScheme.gray900,
                style = NekiTheme.typography.body16Medium,
            )
            Text(
                text = subTitle,
                color = NekiTheme.colorScheme.gray400,
                style = NekiTheme.typography.caption12Medium,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (isGranted) "허용됨" else "허용안됨",
                color = NekiTheme.colorScheme.gray500,
                style = NekiTheme.typography.body14Medium,
            )
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_right),
                tint = NekiTheme.colorScheme.gray300,
                contentDescription = null,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun PermissionSectionItemCameraPreview() {
    NekiTheme {
        PermissionSectionItem(
            title = "카메라",
            subTitle = "QR 촬영에 필요해요.",
            isGranted = true,
        )
    }
}

@ComponentPreview
@Composable
private fun PermissionSectionItemLocationPreview() {
    NekiTheme {
        PermissionSectionItem(
            title = "위치",
            subTitle = "QR 촬영에 필요해요.",
            isGranted = false,
        )
    }
}

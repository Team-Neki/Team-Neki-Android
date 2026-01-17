package com.neki.android.feature.archive.impl.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.extension.clickableSingle
import com.neki.android.core.designsystem.topbar.NekiLeftTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun ArchiveMainTopBar(
    modifier: Modifier = Modifier,
    onPlusIconClick: () -> Unit = {},
    onNotificationIconClick: () -> Unit = {},
) {
    NekiLeftTitleTopBar(
        modifier = modifier,
        title = {
            Box(
                modifier = Modifier
                    .height(28.dp)
                    .width(56.dp)
                    .background(
                        color = Color(0xFFB7B9C3),
                    ),
            )
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    modifier = Modifier.clickableSingle(onClick = onPlusIconClick),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_plus_primary),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
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

@ComponentPreview
@Composable
private fun ArchiveMainTopBarPreview() {
    NekiTheme {
        ArchiveMainTopBar()
    }
}

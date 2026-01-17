package com.neki.android.feature.archive.impl.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.neki.android.core.designsystem.button.NekiTextButton
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun AlbumTitleRow(
    modifier: Modifier = Modifier,
    onShowAllAlbumClick: () -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "앨범",
            style = NekiTheme.typography.title20SemiBold,
            color = NekiTheme.colorScheme.gray900,
        )
        NekiTextButton(
            onClick = onShowAllAlbumClick,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "전체 앨범",
                    style = NekiTheme.typography.body14Regular,
                    color = NekiTheme.colorScheme.gray500,
                )
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_right),
                    contentDescription = null,
                    tint = NekiTheme.colorScheme.gray500,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@ComponentPreview
@Composable
private fun AlbumTitleRowPreview() {
    NekiTheme {
        AlbumTitleRow()
    }
}

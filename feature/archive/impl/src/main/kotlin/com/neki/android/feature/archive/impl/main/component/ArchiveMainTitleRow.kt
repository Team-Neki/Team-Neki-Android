package com.neki.android.feature.archive.impl.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
internal fun ArchiveMainTitleRow(
    title: String,
    textButtonTitle: String,
    modifier: Modifier = Modifier,
    onClickShowAllAlbum: () -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = NekiTheme.typography.title20Bold,
            color = NekiTheme.colorScheme.gray900,
        )
        NekiTextButton(
            onClick = onClickShowAllAlbum,
            contentPadding = PaddingValues(start = 12.dp, top = 4.dp, bottom = 4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = textButtonTitle,
                    style = NekiTheme.typography.body14Regular,
                    color = NekiTheme.colorScheme.gray500,
                )
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_right),
                    contentDescription = null,
                    tint = NekiTheme.colorScheme.gray500,
                )
            }
        }
    }
}

@ComponentPreview
@Composable
private fun ArchiveMainTitleRowPreview() {
    NekiTheme {
        ArchiveMainTitleRow(
            title = "앨범",
            textButtonTitle = "전체 앨범",
        )
    }
}

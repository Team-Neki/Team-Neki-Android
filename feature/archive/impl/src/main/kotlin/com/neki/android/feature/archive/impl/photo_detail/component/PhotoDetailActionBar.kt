package com.neki.android.feature.archive.impl.photo_detail.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.actionbar.NekiActionBar
import com.neki.android.core.designsystem.button.NekiIconButton
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun PhotoDetailActionBar(
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    onClickDownload: () -> Unit = {},
    onClickFavorite: () -> Unit = {},
    onClickMemo: () -> Unit = {},
    onClickDelete: () -> Unit = {},
) {
    NekiActionBar(
        modifier = modifier,
        content = {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NekiIconButton(
                    onClick = onClickDownload,
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.icon_download),
                        contentDescription = null,
                        tint = NekiTheme.colorScheme.gray700,
                    )
                }
                NekiIconButton(
                    onClick = onClickFavorite,
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        imageVector = ImageVector.vectorResource(
                            if (isFavorite) R.drawable.icon_favorite_filled
                            else R.drawable.icon_favorite_stroked,
                        ),
                        contentDescription = null,
                        tint = if (isFavorite) NekiTheme.colorScheme.primary400
                        else NekiTheme.colorScheme.gray700,
                    )
                }
                NekiIconButton(
                    onClick = onClickMemo,
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.icon_memo),
                        contentDescription = null,
                        tint = NekiTheme.colorScheme.gray700,
                    )
                }
            }
            NekiIconButton(
                modifier = Modifier.padding(8.dp),
                onClick = onClickDelete,
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_trash),
                    contentDescription = null,
                    tint = NekiTheme.colorScheme.gray700,
                )
            }
        },
    )
}

@ComponentPreview
@Composable
private fun PhotoDetailActionBarClosedPreview() {
    NekiTheme {
        PhotoDetailActionBar(
            isFavorite = false,
        )
    }
}

@ComponentPreview
@Composable
private fun PhotoDetailActionBarMemoActivePreview() {
    NekiTheme {
        PhotoDetailActionBar(
            isFavorite = true,
        )
    }
}

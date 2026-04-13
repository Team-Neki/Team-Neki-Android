package com.neki.android.feature.archive.impl.album_detail.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.actionbar.NekiActionBar
import com.neki.android.core.designsystem.button.NekiIconButton
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.designsystem.R as DesignR

@Composable
internal fun FavoriteAlbumActionBar(
    visible: Boolean,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onClickDownload: () -> Unit = {},
    onClickDelete: () -> Unit = {},
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = expandVertically(expandFrom = Alignment.Top),
        exit = shrinkVertically(shrinkTowards = Alignment.Top),
    ) {
        NekiActionBar(
            modifier = Modifier.fillMaxWidth(),
            content = {
                NekiIconButton(
                    enabled = isEnabled,
                    onClick = onClickDownload,
                    contentPadding = PaddingValues(20.dp),
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        imageVector = ImageVector.vectorResource(DesignR.drawable.icon_download),
                        contentDescription = null,
                        tint = if (isEnabled) NekiTheme.colorScheme.gray700
                        else NekiTheme.colorScheme.gray200,
                    )
                }
                NekiIconButton(
                    enabled = isEnabled,
                    onClick = onClickDelete,
                    contentPadding = PaddingValues(20.dp),
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        imageVector = ImageVector.vectorResource(DesignR.drawable.icon_trash),
                        contentDescription = null,
                        tint = if (isEnabled) NekiTheme.colorScheme.gray700
                        else NekiTheme.colorScheme.gray200,
                    )
                }
            },
        )
    }
}

@ComponentPreview
@Composable
private fun FavoriteAlbumActionBarEnabledPreview() {
    NekiTheme {
        FavoriteAlbumActionBar(visible = true, isEnabled = true)
    }
}

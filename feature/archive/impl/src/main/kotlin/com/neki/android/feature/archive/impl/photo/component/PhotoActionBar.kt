package com.neki.android.feature.archive.impl.photo.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.button.NekiIconButton
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.designsystem.R as DesignR
import com.neki.android.feature.archive.impl.R

@Composable
internal fun PhotoActionBar(
    visible: Boolean,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onClickDownload: () -> Unit = {},
    onClickCopy: (() -> Unit)? = null,
    onClickMove: (() -> Unit)? = null,
    onClickDelete: () -> Unit = {},
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = expandVertically(expandFrom = Alignment.Top),
        exit = shrinkVertically(shrinkTowards = Alignment.Top),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(thickness = 1.dp, color = NekiTheme.colorScheme.gray75)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NekiIconButton(
                    modifier = Modifier.padding(8.dp),
                    enabled = isEnabled,
                    onClick = onClickDownload,
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        imageVector = ImageVector.vectorResource(DesignR.drawable.icon_download),
                        contentDescription = null,
                        tint = if (isEnabled) NekiTheme.colorScheme.gray700
                        else NekiTheme.colorScheme.gray200,
                    )
                }
                if (onClickCopy != null) {
                    NekiIconButton(
                        modifier = Modifier.padding(8.dp),
                        enabled = isEnabled,
                        onClick = onClickCopy,
                    ) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.icon_add_photo),
                            contentDescription = null,
                            tint = if (isEnabled) NekiTheme.colorScheme.gray700
                            else NekiTheme.colorScheme.gray200,
                        )
                    }
                }
                if (onClickMove != null) {
                    NekiIconButton(
                        modifier = Modifier.padding(8.dp),
                        enabled = isEnabled,
                        onClick = onClickMove,
                    ) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.icon_move_photo),
                            contentDescription = null,
                            tint = if (isEnabled) NekiTheme.colorScheme.gray700
                            else NekiTheme.colorScheme.gray200,
                        )
                    }
                }
                NekiIconButton(
                    modifier = Modifier.padding(8.dp),
                    onClick = onClickDelete,
                    enabled = isEnabled,
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        imageVector = ImageVector.vectorResource(DesignR.drawable.icon_trash),
                        contentDescription = null,
                        tint = if (isEnabled) NekiTheme.colorScheme.gray700
                        else NekiTheme.colorScheme.gray200,
                    )
                }
            }
        }
    }
}

@ComponentPreview
@Composable
private fun PhotoActionBarEnabledPreview() {
    NekiTheme {
        PhotoActionBar(
            visible = true,
            isEnabled = true,
        )
    }
}

@ComponentPreview
@Composable
private fun PhotoActionBarDisabledPreview() {
    NekiTheme {
        PhotoActionBar(
            visible = true,
            isEnabled = false,
        )
    }
}

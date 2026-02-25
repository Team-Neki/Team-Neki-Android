package com.neki.android.feature.archive.impl.photo.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.neki.android.core.designsystem.actionbar.NekiBothSidesActionBar
import com.neki.android.core.designsystem.button.NekiIconButton
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun PhotoActionBar(
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
        NekiBothSidesActionBar(
            modifier = Modifier.fillMaxWidth(),
            startContent = {
                NekiIconButton(
                    modifier = Modifier.padding(8.dp),
                    enabled = isEnabled,
                    onClick = onClickDownload,
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.icon_download),
                        contentDescription = null,
                        tint = if (isEnabled) NekiTheme.colorScheme.gray700
                        else NekiTheme.colorScheme.gray200,
                    )
                }
            },
            endContent = {
                NekiIconButton(
                    modifier = Modifier.padding(8.dp),
                    onClick = onClickDelete,
                    enabled = isEnabled,
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.icon_trash),
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

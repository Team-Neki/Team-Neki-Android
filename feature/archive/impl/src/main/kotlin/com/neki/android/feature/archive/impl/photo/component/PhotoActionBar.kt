package com.neki.android.feature.archive.impl.photo.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
internal fun PhotoActionBar(
    visible: Boolean,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onDownloadClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = expandVertically(expandFrom = Alignment.Top),
        exit = shrinkVertically(shrinkTowards = Alignment.Top),
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
        ) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = NekiTheme.colorScheme.gray75,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.noRippleClickableSingle(
                        enabled = isEnabled,
                        onClick = onDownloadClick,
                    ),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_download),
                    contentDescription = null,
                    tint = if (isEnabled) NekiTheme.colorScheme.gray600 else NekiTheme.colorScheme.gray200,
                )
                Icon(
                    modifier = Modifier.noRippleClickableSingle(
                        enabled = isEnabled,
                        onClick = onDeleteClick,
                    ),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_trash),
                    contentDescription = null,
                    tint = if (isEnabled) NekiTheme.colorScheme.gray600 else NekiTheme.colorScheme.gray200,
                )
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
            isEnabled = true,
        )
    }
}

package com.neki.android.feature.pose.impl.detail.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.actionbar.NekiEndActionBar
import com.neki.android.core.designsystem.button.NekiIconButton
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun PoseActionBar(
    isScrapped: Boolean,
    modifier: Modifier = Modifier,
    onClickScrap: () -> Unit = {},
) {
    NekiEndActionBar(
        modifier = modifier.fillMaxWidth(),
    ) {
        NekiIconButton(
            modifier = Modifier.padding(8.dp),
            onClick = onClickScrap,
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = ImageVector.vectorResource(
                    if (isScrapped) R.drawable.icon_scrap_filled
                    else R.drawable.icon_scrap_stroked,
                ),
                contentDescription = null,
                tint = if (isScrapped) NekiTheme.colorScheme.gray900
                else NekiTheme.colorScheme.gray500,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun ScrappedPoseActionBarPreview() {
    NekiTheme {
        PoseActionBar(isScrapped = true)
    }
}

@ComponentPreview
@Composable
private fun UnScrappedPoseActionBarPreview() {
    NekiTheme {
        PoseActionBar(isScrapped = false)
    }
}

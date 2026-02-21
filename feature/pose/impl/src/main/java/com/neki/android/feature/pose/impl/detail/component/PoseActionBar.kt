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
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun PoseActionBar(
    isBookmarked: Boolean,
    modifier: Modifier = Modifier,
    onClickBookmark: () -> Unit = {},
) {
    NekiEndActionBar(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
    ) {
        Icon(
            modifier = Modifier
                .size(28.dp)
                .noRippleClickableSingle(
                    onClick = onClickBookmark,
                ),
            imageVector = ImageVector.vectorResource(
                if (isBookmarked) R.drawable.icon_bookmark_filled
                else R.drawable.icon_bookmark_stroked,
            ),
            contentDescription = null,
            tint = if (isBookmarked) NekiTheme.colorScheme.gray900
            else NekiTheme.colorScheme.gray500,
        )
    }
}

@ComponentPreview
@Composable
private fun BookmarkedPoseActionBarPreview() {
    NekiTheme {
        PoseActionBar(isBookmarked = true)
    }
}

@ComponentPreview
@Composable
private fun UnBookmarkedPoseActionBarPreview() {
    NekiTheme {
        PoseActionBar(isBookmarked = false)
    }
}

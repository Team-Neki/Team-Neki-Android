package com.neki.android.feature.pose.impl.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.extension.clickableSingle
import com.neki.android.core.designsystem.topbar.NekiTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun PoseTopBar(
    modifier: Modifier = Modifier,
    onIconClick: () -> Unit = {},
) {
    NekiTopBar(
        modifier = modifier.fillMaxWidth(),
        leadingIcon = { modifier ->
            Text(
                modifier = modifier,
                text = "포즈",
                style = NekiTheme.typography.title18SemiBold,
                color = NekiTheme.colorScheme.gray900,
            )
        },
        actions = { modifier ->
            Icon(
                modifier = modifier.clickableSingle(onClick = onIconClick),
                imageVector = ImageVector.vectorResource(R.drawable.icon_bell),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        },
    )
}

@ComponentPreview
@Composable
private fun SubTitleTopBarPreview() {
    NekiTheme {
        PoseTopBar()
    }
}

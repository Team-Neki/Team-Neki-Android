package com.neki.android.feature.archive.impl.main.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.extension.buttonShadow
import com.neki.android.core.designsystem.extension.clickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun GotoTopButton(
    modifier: Modifier = Modifier,
    visible: Boolean = false,
    onClick: () -> Unit = {},
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
        exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center),
    ) {
        Box(
            modifier = Modifier
                .buttonShadow()
                .clip(CircleShape)
                .background(
                    color = NekiTheme.colorScheme.gray800,
                    shape = CircleShape,
                )
                .clickableSingle(onClick = onClick)
                .padding(10.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.icon_check_primary),
                contentDescription = null,
                tint = NekiTheme.colorScheme.white,
            )
        }
    }
}

@Preview
@Composable
private fun GotoTopButtonPreview() {
    NekiTheme {
        GotoTopButton { }
    }
}

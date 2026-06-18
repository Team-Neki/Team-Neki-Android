package com.neki.android.core.designsystem.toggle

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun NekiToggle(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 26.dp else 2.dp,
        animationSpec = tween(durationMillis = 200),
        label = "toggleThumb",
    )

    Box(
        modifier = modifier
            .size(width = 52.dp, height = 28.dp)
            .clip(CircleShape)
            .background(if (checked) NekiTheme.colorScheme.primary300 else NekiTheme.colorScheme.gray100)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(24.dp)
                .clip(CircleShape)
                .background(NekiTheme.colorScheme.white),
        )
    }
}

@ComponentPreview
@Composable
private fun NekiToggleOnPreview() {
    NekiTheme {
        NekiToggle(checked = true)
    }
}

@ComponentPreview
@Composable
private fun NekiToggleOffPreview() {
    NekiTheme {
        NekiToggle(checked = false)
    }
}

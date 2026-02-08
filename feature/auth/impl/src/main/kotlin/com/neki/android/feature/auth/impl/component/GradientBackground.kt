package com.neki.android.feature.auth.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun GradientBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        NekiTheme.colorScheme.primary500,
                        NekiTheme.colorScheme.primary300,
                    ),
                ),
            ),
    )
}

@ComponentPreview
@Composable
private fun GradientBackgroundPreview() {
    NekiTheme {
        GradientBackground()
    }
}

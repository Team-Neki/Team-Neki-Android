package com.neki.android.feature.auth.impl.splash.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.auth.impl.component.GradientBackground

@Composable
internal fun SplashBackground(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        GradientBackground()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 133.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.icon_neki_logo_white),
                contentDescription = null,
            )
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.icon_splash_text),
                contentDescription = null,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun SplashBackgroundPreview() {
    NekiTheme {
        SplashBackground()
    }
}

package com.neki.android.feature.auth.impl.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.auth.impl.component.GradientBackground
import com.neki.android.feature.auth.impl.splash.component.SplashBackground
import kotlinx.coroutines.delay

@Composable
internal fun SplashRoute(
    navigateToOnboarding: () -> Unit,
) {
    LaunchedEffect(Unit) {
        delay(1000)
        navigateToOnboarding()
    }

    SplashScreen()
}

@Composable
internal fun SplashScreen() {
    SplashBackground(
        modifier = Modifier.fillMaxSize()
    )
}

@ComponentPreview
@Composable
internal fun SplashScreenPreview() {
    NekiTheme {
        SplashScreen()
    }
}

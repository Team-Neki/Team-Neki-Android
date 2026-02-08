package com.neki.android.feature.auth.impl.splash

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.auth.impl.splash.component.SplashBackground
import kotlinx.coroutines.delay

@Composable
internal fun SplashRoute(
    viewModel: SplashViewModel = hiltViewModel(),
    navigateToOnboarding: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToMain: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            SplashSideEffect.NavigateToOnboarding -> {
                delay(1000)
                navigateToOnboarding()
            }
            SplashSideEffect.NavigateToLogin -> {
                delay(1000)
                navigateToLogin()
            }
            SplashSideEffect.NavigateToMain -> {
                delay(1000)
                navigateToMain()
            }
        }
    }

    SplashScreen()
}

@Composable
private fun SplashScreen() {
    SplashBackground(
        modifier = Modifier.fillMaxSize()
    )
}

@ComponentPreview
@Composable
private fun SplashScreenPreview() {
    NekiTheme {
        SplashScreen()
    }
}

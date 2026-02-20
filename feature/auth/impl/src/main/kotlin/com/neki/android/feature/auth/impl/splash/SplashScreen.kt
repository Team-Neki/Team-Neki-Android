package com.neki.android.feature.auth.impl.splash

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.auth.impl.splash.component.OptionalUpdateDialog
import com.neki.android.feature.auth.impl.splash.component.RequiredUpdateDialog
import com.neki.android.feature.auth.impl.splash.component.SplashBackground
import com.neki.android.feature.auth.impl.splash.model.SplashConst

@Composable
internal fun SplashRoute(
    viewModel: SplashViewModel = hiltViewModel(),
    navigateToOnboarding: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToMain: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        delay(SplashConst.SPLASH_DELAY)
        val appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
        viewModel.store.onIntent(SplashIntent.EnterSplashScreen(appVersion))
    }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            SplashSideEffect.NavigateToOnboarding -> navigateToOnboarding()
            SplashSideEffect.NavigateToLogin -> navigateToLogin()
            SplashSideEffect.NavigateToMain -> navigateToMain()
            SplashSideEffect.NavigatePlayStore -> {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    "market://details?id=${context.packageName}".toUri(),
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        }
    }

    SplashScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
private fun SplashScreen(
    uiState: SplashState = SplashState(),
    onIntent: (SplashIntent) -> Unit = {},
) {
    SplashBackground(
        modifier = Modifier.fillMaxSize(),
    )

    if (uiState.isShowRequiredUpdateDialog) {
        RequiredUpdateDialog(
            onClickUpdate = { onIntent(SplashIntent.ClickUpdateDialogConfirmButton) },
        )
    }

    if (uiState.isShowOptionalUpdateDialog) {
        OptionalUpdateDialog(
            onClickUpdate = { onIntent(SplashIntent.ClickUpdateDialogConfirmButton) },
            onClickDismiss = { onIntent(SplashIntent.ClickUpdateDialogDismissButton) },
            onDismissRequest = { onIntent(SplashIntent.ClickUpdateDialogDismissButton) },
        )
    }
}

@ComponentPreview
@Composable
private fun SplashScreenPreview() {
    NekiTheme {
        SplashScreen()
    }
}

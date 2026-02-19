package com.neki.android.feature.auth.impl.splash

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.auth.impl.splash.component.OptionalUpdateDialog
import com.neki.android.feature.auth.impl.splash.component.RequiredUpdateDialog
import com.neki.android.feature.auth.impl.splash.component.SplashBackground
import com.neki.android.feature.auth.impl.splash.model.UpdateType
import kotlinx.coroutines.delay

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
        val appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
        viewModel.store.onIntent(SplashIntent.EnterSplashScreen(appVersion))
    }

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

            SplashSideEffect.NavigatePlayStore -> {
                val packageName = context.packageName
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName"),
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
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

    when (uiState.updateType) {
        UpdateType.Required -> {
            RequiredUpdateDialog(
                onClickUpdate = { onIntent(SplashIntent.ClickUpdateButton) },
            )
        }

        UpdateType.Optional -> {
            OptionalUpdateDialog(
                onDismissRequest = { onIntent(SplashIntent.ClickDismissUpdateDialog) },
                onClickUpdate = { onIntent(SplashIntent.ClickUpdateButton) },
                onClickDismiss = { onIntent(SplashIntent.ClickDismissUpdateDialog) },
            )
        }

        UpdateType.None -> {}
    }
}

@ComponentPreview
@Composable
private fun SplashScreenPreview() {
    NekiTheme {
        SplashScreen()
    }
}

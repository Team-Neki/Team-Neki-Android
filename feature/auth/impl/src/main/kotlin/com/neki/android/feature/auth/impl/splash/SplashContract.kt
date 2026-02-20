package com.neki.android.feature.auth.impl.splash

data class SplashState(
    val isShowRequiredUpdateDialog: Boolean = false,
    val isShowOptionalUpdateDialog: Boolean = false,
    val latestVersion: String = "",
)

sealed interface SplashIntent {
    data class EnterSplashScreen(val appVersion: String) : SplashIntent
    data object ClickUpdateDialogDismissButton : SplashIntent
    data object ClickUpdateDialogConfirmButton : SplashIntent
}

sealed interface SplashSideEffect {
    data object NavigateToOnboarding : SplashSideEffect
    data object NavigateToLogin : SplashSideEffect
    data object NavigateToMain : SplashSideEffect
    data object NavigatePlayStore : SplashSideEffect
}

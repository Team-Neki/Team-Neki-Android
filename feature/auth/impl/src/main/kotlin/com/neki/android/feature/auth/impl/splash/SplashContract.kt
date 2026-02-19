package com.neki.android.feature.auth.impl.splash

import com.neki.android.feature.auth.impl.splash.model.UpdateType

data class SplashState(
    val updateType: UpdateType = UpdateType.None,
    val minVersion: String = "",
)

sealed interface SplashIntent {
    data class EnterSplashScreen(val appVersion: String) : SplashIntent
    data object ClickDismissUpdateDialog : SplashIntent
    data object ClickUpdateButton : SplashIntent
}

sealed interface SplashSideEffect {
    data object NavigateToOnboarding : SplashSideEffect
    data object NavigateToLogin : SplashSideEffect
    data object NavigateToMain : SplashSideEffect
    data object NavigatePlayStore : SplashSideEffect
}

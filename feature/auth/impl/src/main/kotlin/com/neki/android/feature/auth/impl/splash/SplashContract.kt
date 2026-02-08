package com.neki.android.feature.auth.impl.splash

data object SplashState

sealed interface SplashIntent {
    data object EnterSplashScreen : SplashIntent
}

sealed interface SplashSideEffect {
    data object NavigateToOnboarding : SplashSideEffect
    data object NavigateToLogin : SplashSideEffect
    data object NavigateToMain : SplashSideEffect
}

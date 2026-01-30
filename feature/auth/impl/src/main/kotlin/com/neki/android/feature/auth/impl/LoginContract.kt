package com.neki.android.feature.auth.impl

data class LoginState(
    val isLoading: Boolean = false,
)

sealed interface LoginIntent {
    data object CheckLoginState : LoginIntent
    data object ClickKakaoLogin : LoginIntent
    data class SuccessLogin(val idToken: String) : LoginIntent
    data object FailLogin : LoginIntent
}

sealed interface LoginSideEffect {
    data object NavigateToHome : LoginSideEffect
    data object NavigateToKakaoRedirectingUri : LoginSideEffect
    data class ShowToastMessage(val message: String) : LoginSideEffect
}

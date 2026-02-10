package com.neki.android.feature.auth.impl.login

data class LoginState(
    val isLoading: Boolean = false,
)

sealed interface LoginIntent {
    data object ClickKakaoLogin : LoginIntent
    data class SuccessKakaoLogin(val idToken: String) : LoginIntent
    data object FailKakaoLogin : LoginIntent
}

sealed interface LoginSideEffect {
    data object NavigateToMain : LoginSideEffect
    data object NavigateToTerm : LoginSideEffect
    data object NavigateToKakaoRedirectingUri : LoginSideEffect
    data class ShowToastMessage(val message: String) : LoginSideEffect
}

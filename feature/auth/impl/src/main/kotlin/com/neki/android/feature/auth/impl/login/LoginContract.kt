package com.neki.android.feature.auth.impl.login

data class LoginState(
    val isLoading: Boolean = false,
    val kakaoIdToken: String = "",
)

sealed interface LoginIntent {
    data object ClickKakaoLogin : LoginIntent
    data class SuccessLogin(val idToken: String) : LoginIntent
    data object FailLogin : LoginIntent
}

sealed interface LoginSideEffect {
    data class NavigateToTerm(val kakaoIdToken: String) : LoginSideEffect
    data object NavigateToKakaoRedirectingUri : LoginSideEffect
    data class ShowToastMessage(val message: String) : LoginSideEffect
}

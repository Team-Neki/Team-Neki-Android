package com.neki.android.feature.auth.impl

data class AuthState(
    val isLoading: Boolean = false,
)

sealed interface AuthIntent {
    data object EnterLoginScreen : AuthIntent
    data object ClickKakaoLogin : AuthIntent
    data class SuccessLogin(val idToken: String) : AuthIntent
    data object FailLogin : AuthIntent
}

sealed interface AuthSideEffect {
    data object NavigateToTerm : AuthSideEffect
    data object NavigateToMain : AuthSideEffect
    data object NavigateToKakaoRedirectingUri : AuthSideEffect
    data class ShowToastMessage(val message: String) : AuthSideEffect
}

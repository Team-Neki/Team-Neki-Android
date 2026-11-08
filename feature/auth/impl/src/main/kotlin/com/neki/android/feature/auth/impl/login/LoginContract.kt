package com.neki.android.feature.auth.impl.login

import com.neki.android.core.model.LoginType

data class LoginState(
    val isLoading: Boolean = false,
    val lastLoginType: LoginType? = null,
)

sealed interface LoginIntent {
    data object EnterLoginScreen : LoginIntent
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

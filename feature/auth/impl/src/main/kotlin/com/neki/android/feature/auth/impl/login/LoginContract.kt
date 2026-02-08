package com.neki.android.feature.auth.impl.login

import com.neki.android.feature.auth.impl.term.model.TermAgreement
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

data class LoginState(
    val isLoading: Boolean = false,
    val kakaoIdToken: String = "",
    val agreedTerms: ImmutableSet<TermAgreement> = persistentSetOf(),
    val isAllRequiredAgreed: Boolean = false,
)

sealed interface LoginIntent {
    data object ClickKakaoLogin : LoginIntent
    data class SuccessLogin(val idToken: String) : LoginIntent
    data object FailLogin : LoginIntent

    // Term
    data object ClickAgreeAll : LoginIntent
    data class ClickAgreeTerm(val term: TermAgreement) : LoginIntent
    data class ClickTermNavigateUrl(val term: TermAgreement) : LoginIntent
    data object ClickNext : LoginIntent
    data object ClickBack : LoginIntent
    data object ResetTermState : LoginIntent
}

sealed interface LoginSideEffect {
    data object NavigateToTerm : LoginSideEffect
    data object NavigateToMain : LoginSideEffect
    data object NavigateBack : LoginSideEffect
    data object NavigateToKakaoRedirectingUri : LoginSideEffect
    data class NavigateUrl(val url: String) : LoginSideEffect
    data class ShowToastMessage(val message: String) : LoginSideEffect
}

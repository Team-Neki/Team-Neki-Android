package com.neki.android.feature.auth.impl

import com.neki.android.feature.auth.impl.term.model.TermAgreement
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

data class AuthState(
    val isLoading: Boolean = false,
    val kakaoIdToken: String = "",
    val agreedTerms: ImmutableSet<TermAgreement> = persistentSetOf(),
    val isAllRequiredAgreed: Boolean = false,
)

sealed interface AuthIntent {
    data object ClickKakaoLogin : AuthIntent
    data class SuccessLogin(val idToken: String) : AuthIntent
    data object FailLogin : AuthIntent

    // Term
    data object ClickAgreeAll : AuthIntent
    data class ClickAgreeTerm(val term: TermAgreement) : AuthIntent
    data class ClickTermNavigateUrl(val term: TermAgreement) : AuthIntent
    data object ClickNext : AuthIntent
    data object ClickBack : AuthIntent
    data object ResetTermState : AuthIntent
}

sealed interface AuthSideEffect {
    data object NavigateToTerm : AuthSideEffect
    data object NavigateToMain : AuthSideEffect
    data object NavigateBack : AuthSideEffect
    data object NavigateToKakaoRedirectingUri : AuthSideEffect
    data class NavigateUrl(val url: String) : AuthSideEffect
    data class ShowToastMessage(val message: String) : AuthSideEffect
}

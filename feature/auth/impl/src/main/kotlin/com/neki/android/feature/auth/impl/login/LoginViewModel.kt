package com.neki.android.feature.auth.impl.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.AuthRepository
import com.neki.android.core.dataapi.repository.TokenRepository
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.auth.impl.term.model.TermAgreement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    val store: MviIntentStore<LoginState, LoginIntent, LoginSideEffect> =
        mviIntentStore(
            initialState = LoginState(),
            onIntent = ::onIntent,
        )

    private fun onIntent(
        intent: LoginIntent,
        state: LoginState,
        reduce: (LoginState.() -> LoginState) -> Unit,
        postSideEffect: (LoginSideEffect) -> Unit,
    ) {
        when (intent) {
            LoginIntent.ClickKakaoLogin -> postSideEffect(LoginSideEffect.NavigateToKakaoRedirectingUri)
            is LoginIntent.SuccessLogin -> {
                reduce { copy(kakaoIdToken = intent.idToken) }
                postSideEffect(LoginSideEffect.NavigateToTerm)
            }

            LoginIntent.FailLogin -> postSideEffect(LoginSideEffect.ShowToastMessage("카카오 로그인에 실패했습니다."))

            LoginIntent.ClickAgreeAll -> {
                if (state.isAllRequiredAgreed) {
                    reduce { copy(agreedTerms = persistentSetOf(), isAllRequiredAgreed = false) }
                } else {
                    val allRequiredTerms = TermAgreement.entries.filter { it.isRequired }.toPersistentSet()
                    reduce { copy(agreedTerms = allRequiredTerms, isAllRequiredAgreed = true) }
                }
            }

            is LoginIntent.ClickAgreeTerm -> {
                val newAgreedTerms = if (intent.term in state.agreedTerms) {
                    (state.agreedTerms - intent.term).toPersistentSet()
                } else {
                    (state.agreedTerms + intent.term).toPersistentSet()
                }
                val isAllRequiredAgreed = TermAgreement.entries
                    .filter { it.isRequired }
                    .all { it in newAgreedTerms }
                reduce { copy(agreedTerms = newAgreedTerms, isAllRequiredAgreed = isAllRequiredAgreed) }
            }

            is LoginIntent.ClickTermNavigateUrl -> {
                postSideEffect(LoginSideEffect.NavigateUrl(intent.term.url))
            }

            LoginIntent.ClickNext -> {
                val idToken = state.kakaoIdToken
                if (state.isAllRequiredAgreed) {
                    loginWithKakao(idToken, reduce, postSideEffect)
                }
            }

            LoginIntent.ClickBack -> {
                postSideEffect(LoginSideEffect.NavigateBack)
            }

            LoginIntent.ResetTermState -> {
                reduce { copy(agreedTerms = persistentSetOf(), isAllRequiredAgreed = false) }
            }
        }
    }

    private fun loginWithKakao(
        idToken: String,
        reduce: (LoginState.() -> LoginState) -> Unit,
        postSideEffect: (LoginSideEffect) -> Unit,
    ) = viewModelScope.launch {
        reduce { copy(isLoading = true) }
        authRepository.loginWithKakao(idToken)
            .onSuccess {
                tokenRepository.saveTokens(
                    accessToken = it.accessToken,
                    refreshToken = it.refreshToken,
                )
                authRepository.setReadOnboarding(true)
                postSideEffect(LoginSideEffect.NavigateToMain)
            }
            .onFailure { exception ->
                Timber.e(exception)
                postSideEffect(LoginSideEffect.ShowToastMessage("로그인에 실패했습니다. 다시 시도해주세요."))
            }
        reduce { copy(isLoading = false) }
    }
}

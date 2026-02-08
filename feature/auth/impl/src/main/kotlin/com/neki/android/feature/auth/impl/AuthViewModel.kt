package com.neki.android.feature.auth.impl

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
class AuthViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    val store: MviIntentStore<AuthState, AuthIntent, AuthSideEffect> =
        mviIntentStore(
            initialState = AuthState(),
            onIntent = ::onIntent,
        )

    private fun onIntent(
        intent: AuthIntent,
        state: AuthState,
        reduce: (AuthState.() -> AuthState) -> Unit,
        postSideEffect: (AuthSideEffect) -> Unit,
    ) {
        when (intent) {
            AuthIntent.ClickKakaoLogin -> postSideEffect(AuthSideEffect.NavigateToKakaoRedirectingUri)
            is AuthIntent.SuccessLogin -> {
                reduce { copy(kakaoIdToken = intent.idToken) }
                postSideEffect(AuthSideEffect.NavigateToTerm)
            }
            AuthIntent.FailLogin -> postSideEffect(AuthSideEffect.ShowToastMessage("카카오 로그인에 실패했습니다."))

            // Term
            AuthIntent.ClickAgreeAll -> {
                if (state.isAllRequiredAgreed) {
                    reduce { copy(agreedTerms = persistentSetOf(), isAllRequiredAgreed = false) }
                } else {
                    val allRequiredTerms = TermAgreement.entries.filter { it.isRequired }.toPersistentSet()
                    reduce { copy(agreedTerms = allRequiredTerms, isAllRequiredAgreed = true) }
                }
            }
            is AuthIntent.ClickAgreeTerm -> {
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
            is AuthIntent.ClickTermNavigateUrl -> {
                postSideEffect(AuthSideEffect.NavigateUrl(intent.term.url))
            }
            AuthIntent.ClickNext -> {
                val idToken = state.kakaoIdToken
                if (state.isAllRequiredAgreed) {
                    loginWithKakao(idToken, reduce, postSideEffect)
                }
            }
            AuthIntent.ClickBack -> {
                postSideEffect(AuthSideEffect.NavigateBack)
            }
            AuthIntent.ResetTermState -> {
                reduce { copy(agreedTerms = persistentSetOf(), isAllRequiredAgreed = false) }
            }
        }
    }

    private fun loginWithKakao(
        idToken: String,
        reduce: (AuthState.() -> AuthState) -> Unit,
        postSideEffect: (AuthSideEffect) -> Unit,
    ) = viewModelScope.launch {
        reduce { copy(isLoading = true) }
        authRepository.loginWithKakao(idToken)
            .onSuccess {
                tokenRepository.saveTokens(
                    accessToken = it.accessToken,
                    refreshToken = it.refreshToken,
                )
                authRepository.setReadOnboarding(true)
                postSideEffect(AuthSideEffect.NavigateToMain)
            }
            .onFailure { exception ->
                Timber.e(exception)
            }
        reduce { copy(isLoading = false) }
    }
}

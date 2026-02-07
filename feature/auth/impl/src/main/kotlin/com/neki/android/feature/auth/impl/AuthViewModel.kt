package com.neki.android.feature.auth.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.auth.AuthEventManager
import com.neki.android.core.dataapi.repository.AuthRepository
import com.neki.android.core.dataapi.repository.TokenRepository
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.auth.impl.term.model.TermAgreement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authEventManager: AuthEventManager,
    private val tokenRepository: TokenRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    val store: MviIntentStore<AuthState, AuthIntent, AuthSideEffect> =
        mviIntentStore(
            initialState = AuthState(),
            onIntent = ::onIntent,
            initialFetchData = { store.onIntent(AuthIntent.EnterLoginScreen) },
        )

    private fun onIntent(
        intent: AuthIntent,
        state: AuthState,
        reduce: (AuthState.() -> AuthState) -> Unit,
        postSideEffect: (AuthSideEffect) -> Unit,
    ) {
        when (intent) {
            AuthIntent.EnterLoginScreen -> fetchInitialData(postSideEffect)
            AuthIntent.ClickKakaoLogin -> postSideEffect(AuthSideEffect.NavigateToKakaoRedirectingUri)
            is AuthIntent.SuccessLogin -> loginWithKakao(intent.idToken, reduce, postSideEffect)
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
                if (state.isAllRequiredAgreed) {
                    postSideEffect(AuthSideEffect.NavigateToMain)
                }
            }
            AuthIntent.ClickBack -> {
                postSideEffect(AuthSideEffect.NavigateBack)
            }
        }
    }

    private fun fetchInitialData(postSideEffect: (AuthSideEffect) -> Unit) = viewModelScope.launch {
        if (tokenRepository.isSavedTokens().first()) {
            authRepository.updateAccessToken(
                refreshToken = tokenRepository.getRefreshToken().first(),
            ).onSuccess {
                tokenRepository.saveTokens(it.accessToken, it.refreshToken)
                postSideEffect(AuthSideEffect.NavigateToTerm)
            }.onFailure { exception ->
                Timber.e(exception)
                authEventManager.emitTokenExpired()
            }
        } else {
            Timber.d("저장된 JWT 토큰이 없습니다.")
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
                postSideEffect(AuthSideEffect.NavigateToTerm)
            }
            .onFailure { exception ->
                Timber.e(exception)
            }
        reduce { copy(isLoading = false) }
    }
}

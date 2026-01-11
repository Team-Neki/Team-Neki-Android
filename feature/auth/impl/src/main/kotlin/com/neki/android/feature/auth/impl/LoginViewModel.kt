package com.neki.android.feature.auth.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.AuthRepository
import com.neki.android.core.dataapi.repository.DataStoreRepository
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val authRepository: AuthRepository,
): ViewModel() {
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
            LoginIntent.EnterLoginScreen -> checkLoginState(reduce, postSideEffect)
            LoginIntent.ClickKakaoLogin -> postSideEffect(LoginSideEffect.NavigateToKakaoRedirectingUri)
            is LoginIntent.SuccessLogin -> loginFromKakao(intent.idToken, reduce, postSideEffect)
            LoginIntent.FailLogin -> postSideEffect(LoginSideEffect.NavigateToKakaoRedirectingUri)
        }
    }

    private fun checkLoginState(
        reduce: (LoginState.() -> LoginState) -> Unit,
        postSideEffect: (LoginSideEffect) -> Unit,
    ) = viewModelScope.launch {
        if (dataStoreRepository.isSavedJwtTokens().first()) {
            Timber.d("JWT 토큰 O")
            authRepository.updateAccessToken(
                refreshToken = dataStoreRepository.getRefreshToken().first()!!
            ).onSuccess {
                postSideEffect(LoginSideEffect.NavigateToHome)
            }.onFailure {
                Timber.d(it.message.toString())
            }
        } else {
            Timber.d("JWT 토큰 X")
        }
    }

    private fun loginFromKakao(
        idToken: String,
        reduce: (LoginState.() -> LoginState) -> Unit,
        postSideEffect: (LoginSideEffect) -> Unit,
    ) = viewModelScope.launch {
        reduce { copy(isLoading = true) }
        authRepository.loginWithKakao(idToken)
            .onSuccess {
                dataStoreRepository.saveJwtTokens(
                    accessToken = it.accessToken,
                    refreshToken = it.refreshToken
                )

                postSideEffect(LoginSideEffect.NavigateToHome)
            }
            .onFailure {
                Timber.d(it.message.toString())
            }
    }


}

package com.neki.android.feature.auth.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.auth.AuthEventManager
import com.neki.android.core.dataapi.repository.AuthRepository
import com.neki.android.core.dataapi.repository.DataStoreRepository
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authEventManager: AuthEventManager,
    private val dataStoreRepository: DataStoreRepository,
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
            LoginIntent.EnterLoginScreen -> checkLoginState(reduce, postSideEffect)
            LoginIntent.ClickKakaoLogin -> postSideEffect(LoginSideEffect.NavigateToKakaoRedirectingUri)
            is LoginIntent.SuccessLogin -> loginFromKakao(intent.idToken, reduce, postSideEffect)
            LoginIntent.FailLogin -> postSideEffect(LoginSideEffect.ShowToastMessage("카카오 로그인에 실패했습니다."))
        }
    }

    private fun checkLoginState(
        reduce: (LoginState.() -> LoginState) -> Unit,
        postSideEffect: (LoginSideEffect) -> Unit,
    ) = viewModelScope.launch {
        if (dataStoreRepository.isSavedJwtTokens().first()) {
            Timber.d("JWT 토큰 O")
            authRepository.updateAccessToken(
                refreshToken = dataStoreRepository.getRefreshToken().firstOrNull() ?: "",
            ).onSuccess {
                postSideEffect(LoginSideEffect.NavigateToHome)
            }.onFailure {
                Timber.d(it.message.toString())
                authEventManager.emitTokenExpired()
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
                    refreshToken = it.refreshToken,
                )

                postSideEffect(LoginSideEffect.NavigateToHome)
            }
            .onFailure {
                Timber.d(it.message.toString())
            }
        reduce { copy(isLoading = false) }
    }
}

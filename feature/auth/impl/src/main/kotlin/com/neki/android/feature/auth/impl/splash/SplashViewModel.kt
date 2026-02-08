package com.neki.android.feature.auth.impl.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.AuthRepository
import com.neki.android.core.dataapi.repository.TokenRepository
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    val store: MviIntentStore<SplashState, SplashIntent, SplashSideEffect> =
        mviIntentStore(
            initialState = SplashState,
            onIntent = ::onIntent,
            initialFetchData = { store.onIntent(SplashIntent.EnterSplashScreen) },
        )

    private fun onIntent(
        intent: SplashIntent,
        state: SplashState,
        reduce: (SplashState.() -> SplashState) -> Unit,
        postSideEffect: (SplashSideEffect) -> Unit,
    ) {
        when (intent) {
            SplashIntent.EnterSplashScreen -> fetchAuthState(postSideEffect)
        }
    }

    private fun fetchAuthState(postSideEffect: (SplashSideEffect) -> Unit) {
        viewModelScope.launch {
            val hasCompletedOnboarding = authRepository.hasCompletedOnboarding().first()
            if (!hasCompletedOnboarding) {
                postSideEffect(SplashSideEffect.NavigateToOnboarding)
                return@launch
            }

            if (tokenRepository.isSavedTokens().first()) {
                authRepository.updateAccessToken(
                    refreshToken = tokenRepository.getRefreshToken().first(),
                ).onSuccess {
                    tokenRepository.saveTokens(it.accessToken, it.refreshToken)
                    postSideEffect(SplashSideEffect.NavigateToMain)
                }.onFailure { exception ->
                    Timber.e(exception)
                    postSideEffect(SplashSideEffect.NavigateToLogin)
                }
            } else {
                postSideEffect(SplashSideEffect.NavigateToLogin)
            }
        }
    }
}

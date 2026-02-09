package com.neki.android.feature.auth.impl.login

import androidx.lifecycle.ViewModel
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {
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
                postSideEffect(LoginSideEffect.NavigateToTerm(intent.idToken))
            }

            LoginIntent.FailLogin -> postSideEffect(LoginSideEffect.ShowToastMessage("카카오 로그인에 실패했습니다."))
        }
    }
}

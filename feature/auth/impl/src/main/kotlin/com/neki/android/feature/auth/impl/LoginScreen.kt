package com.neki.android.feature.auth.impl

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.auth.impl.component.LoginContent
import com.neki.android.feature.auth.impl.util.KakaoLoginHelper
import timber.log.Timber

@Composable
fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToMain: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val kakaoLoginHelper = remember { KakaoLoginHelper(context) }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            LoginSideEffect.NavigateToHome -> {
                Timber.d("홈 화면으로 이동하거나 API를 호출하거나")
            }

            LoginSideEffect.NavigateToKakaoRedirectingUri -> {
                kakaoLoginHelper.loginWithKakao(
                    onSuccess = { idToken ->
                        Timber.d("로그인 성공 $idToken")
                        navigateToMain() // 제거 예정
                        viewModel.store.onIntent(LoginIntent.SuccessLogin(idToken))
                    },
                    onFailure = { message ->
                        Timber.d("로그인 실패 $message")
                    },
                )
            }

            is LoginSideEffect.ShowToastMessage -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.store.onIntent(LoginIntent.EnterLoginScreen)
    }

    LoginScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
fun LoginScreen(
    uiState: LoginState = LoginState(),
    onIntent: (LoginIntent) -> Unit = {},
) {
    LoginContent(
        onClickKakaoLogin = { onIntent(LoginIntent.ClickKakaoLogin) },
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    NekiTheme {
        LoginScreen()
    }
}

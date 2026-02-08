package com.neki.android.feature.auth.impl.login

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.common.kakao.KakaoAuthHelper
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.auth.impl.login.component.LoginBackground
import com.neki.android.feature.auth.impl.login.component.LoginBottomContent
import timber.log.Timber

@Composable
internal fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToTerm: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val kakaoAuthHelper = remember { KakaoAuthHelper(context) }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            LoginSideEffect.NavigateToTerm -> navigateToTerm()
            LoginSideEffect.NavigateToKakaoRedirectingUri -> {
                kakaoAuthHelper.login(
                    onSuccess = { idToken ->
                        Timber.d("로그인 성공 $idToken")
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

            else -> {}
        }
    }

    LoginScreen(
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
private fun LoginScreen(
    onIntent: (LoginIntent) -> Unit = {},
) {
    Box {
        LoginBackground()
        LoginBottomContent(
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = { onIntent(LoginIntent.ClickKakaoLogin) },
        )
    }
}

@ComponentPreview
@Composable
private fun LoginScreenPreview() {
    NekiTheme {
        LoginScreen()
    }
}

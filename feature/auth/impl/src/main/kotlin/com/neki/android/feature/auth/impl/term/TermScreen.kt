package com.neki.android.feature.auth.impl.term

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.button.CTAButtonPrimary
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.auth.impl.AuthIntent
import com.neki.android.feature.auth.impl.AuthSideEffect
import com.neki.android.feature.auth.impl.AuthState
import com.neki.android.feature.auth.impl.AuthViewModel
import com.neki.android.feature.auth.impl.term.component.TermContent
import com.neki.android.feature.auth.impl.term.component.TermTopBar

@Composable
internal fun TermRoute(
    viewModel: AuthViewModel = hiltViewModel(),
    navigateToMain: () -> Unit,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.store.onIntent(AuthIntent.ResetTermState)
        }
    }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            AuthSideEffect.NavigateToMain -> navigateToMain()
            AuthSideEffect.NavigateBack -> navigateBack()
            is AuthSideEffect.NavigateUrl -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sideEffect.url))
                context.startActivity(intent)
            }
            else -> {}
        }
    }

    TermScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
private fun TermScreen(
    uiState: AuthState = AuthState(),
    onIntent: (AuthIntent) -> Unit = {},
) {
    Column {
        TermTopBar(
            onClickBack = { onIntent(AuthIntent.ClickBack) }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp, start = 20.dp, end = 20.dp, bottom = 34.dp)
        ) {
            TermContent(
                modifier = Modifier.weight(1f),
                agreedTerms = uiState.agreedTerms,
                isAllRequiredAgreed = uiState.isAllRequiredAgreed,
                onClickAgreeAll = { onIntent(AuthIntent.ClickAgreeAll) },
                onClickAgreeTerm = { onIntent(AuthIntent.ClickAgreeTerm(it)) },
                onClickTermDetail = { onIntent(AuthIntent.ClickTermNavigateUrl(it)) },
            )
            CTAButtonPrimary(
                modifier = Modifier.fillMaxWidth(),
                text = "다음으로",
                onClick = { onIntent(AuthIntent.ClickNext) },
                enabled = uiState.isAllRequiredAgreed,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun TermScreenPreview() {
    NekiTheme {
        TermScreen()
    }
}

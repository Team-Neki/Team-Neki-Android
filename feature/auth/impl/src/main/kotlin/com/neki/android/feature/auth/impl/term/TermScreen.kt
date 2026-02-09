package com.neki.android.feature.auth.impl.term

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.button.CTAButtonPrimary
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.auth.impl.term.component.TermContent
import com.neki.android.feature.auth.impl.term.component.TermTopBar

@Composable
internal fun TermRoute(
    viewModel: TermViewModel,
    navigateToMain: () -> Unit,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val nekiToast = remember { NekiToast(context) }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            TermSideEffect.NavigateToMain -> navigateToMain()
            TermSideEffect.NavigateBack -> navigateBack()
            is TermSideEffect.NavigateUrl -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sideEffect.url))
                context.startActivity(intent)
            }

            is TermSideEffect.ShowToastMessage -> {
                nekiToast.showToast(sideEffect.message)
            }
        }
    }

    TermScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
private fun TermScreen(
    uiState: TermState = TermState(),
    onIntent: (TermIntent) -> Unit = {},
) {
    Column {
        TermTopBar(
            onClickBack = { onIntent(TermIntent.ClickBack) },
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp, start = 20.dp, end = 20.dp, bottom = 34.dp),
        ) {
            TermContent(
                modifier = Modifier.weight(1f),
                terms = uiState.terms,
                isAllRequiredChecked = uiState.isAllRequiredChecked,
                onClickAgreeAll = { onIntent(TermIntent.ClickAgreeAll) },
                onClickAgreeTerm = { onIntent(TermIntent.ClickAgreeTerm(it)) },
                onClickTermDetail = { onIntent(TermIntent.ClickTermNavigateUrl(it)) },
            )
            CTAButtonPrimary(
                modifier = Modifier.fillMaxWidth(),
                text = "다음으로",
                onClick = { onIntent(TermIntent.ClickNext) },
                enabled = uiState.isAllRequiredChecked,
            )
        }
    }

    if (uiState.isLoading) {
        LoadingDialog()
    }
}

@ComponentPreview
@Composable
private fun TermScreenPreview() {
    NekiTheme {
        TermScreen()
    }
}

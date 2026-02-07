package com.neki.android.feature.auth.impl.term

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.button.CTAButtonPrimary
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.auth.impl.term.component.TermContent
import com.neki.android.feature.auth.impl.term.component.TermTopBar

@Composable
internal fun TermRoute(
    navigateToMain: () -> Unit,
) {
    TermScreen(
        onClickAgree = navigateToMain,
    )
}

@Composable
internal fun TermScreen(
    onClickAgree: () -> Unit = {},
) {
    Column {
        TermTopBar(
            onClickBack = {}
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp, start = 20.dp, end = 20.dp, bottom = 34.dp)
        ) {
            TermContent(
                modifier = Modifier.weight(1f)
            )
            CTAButtonPrimary(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "다음으로",
                onClick = onClickAgree,
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

package com.neki.android.feature.auth.impl.term.component

import androidx.compose.runtime.Composable
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.topbar.BackTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun TermTopBar(
    onClickBack: () -> Unit= {},
) {
    BackTitleTopBar(
        title = "이용약관",
        onBack = onClickBack,
    )
}

@ComponentPreview
@Composable
private fun TermTopBarPreview() {
    NekiTheme {
        TermTopBar()
    }
}

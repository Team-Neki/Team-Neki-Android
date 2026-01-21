package com.neki.android.feature.mypage.impl

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun PermissionRoute(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PermissionScreen(
        navigateBack = navigateBack,
        modifier = modifier,
    )
}

@Composable
fun PermissionScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {

    }
}

@ComponentPreview
@Composable
private fun PermissionScreenPreview() {
    NekiTheme {
        PermissionScreen(
            navigateBack = {},
        )
    }
}

package com.neki.android.feature.mypage.impl.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun ProfileRoute(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ProfileScreen(
        navigateBack = navigateBack,
        modifier = modifier,
    )
}

@Composable
fun ProfileScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "프로필 화면",
            style = NekiTheme.typography.title18SemiBold,
            color = NekiTheme.colorScheme.gray900,
        )
    }
}

@ComponentPreview
@Composable
private fun ProfileScreenPreview() {
    NekiTheme {
        ProfileScreen(
            navigateBack = {},
        )
    }
}

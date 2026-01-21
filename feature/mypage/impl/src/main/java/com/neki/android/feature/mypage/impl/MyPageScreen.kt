package com.neki.android.feature.mypage.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun MyPageRoute(
    modifier: Modifier = Modifier,
) {
    MyPageScreen(modifier = modifier)
}

@Composable
fun MyPageScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "마이페이지")
    }
}

@Preview
@Composable
private fun MyPageScreenPreview() {
    NekiTheme {
        MyPageScreen()
    }
}

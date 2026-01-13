package com.neki.android.feature.auth.impl.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    onClickKakaoLogin: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Button(
            onClick = onClickKakaoLogin,
        ) {
            Text(
                text = "카카오 로그인",
                fontSize = 22.sp,
            )
        }
    }
}

package com.neki.android.feature.auth.impl.login.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.clickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun LoginBottomContent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = NekiTheme.colorScheme.white,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            )
            .padding(top = 32.dp, bottom = 56.dp, start = 20.dp, end = 20.dp),
    ) {
        KakaoLoginButton(
            onClick = onClick,
        )
    }
}

@Composable
private fun KakaoLoginButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF9DB00),
            )
            .clip(RoundedCornerShape(12.dp))
            .clickableSingle(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Icon(
            modifier = Modifier.align(Alignment.CenterStart),
            imageVector = ImageVector.vectorResource(R.drawable.icon_kakao_talk),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "카카오로 계속하기",
            style = NekiTheme.typography.title18Bold,
            color = NekiTheme.colorScheme.gray900,
        )
    }
}

@ComponentPreview
@Composable
private fun LoginBottomContentPreview() {
    NekiTheme {
        LoginBottomContent()
    }
}

@ComponentPreview
@Composable
private fun KakaoLoginButtonPreview() {
    NekiTheme {
        KakaoLoginButton()
    }
}

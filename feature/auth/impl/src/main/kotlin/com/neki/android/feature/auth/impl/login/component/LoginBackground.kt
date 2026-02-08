package com.neki.android.feature.auth.impl.login.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.logo.WhiteNekiAppLogo
import com.neki.android.core.designsystem.logo.WhiteNekiTypoLogo
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.feature.auth.impl.component.GradientBackground

@Composable
internal fun LoginBackground() {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        GradientBackground()
        Column(
            modifier = Modifier.padding(start = 32.dp, top = 164.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(15.36.dp),
            ) {
                WhiteNekiAppLogo(
                    modifier = Modifier.size(width = 33.3.dp, height = 37.6.dp),
                )
                WhiteNekiTypoLogo(
                    modifier = Modifier.size(width = 93.8.dp, height = 36.6.dp),
                )
            }
            VerticalSpacer(24.dp)
            Text(
                text = "네컷의 순간이 \n이어지는 곳",
                style = NekiTheme.typography.title24Bold,
                fontSize = 32.sp,
                color = NekiTheme.colorScheme.white,
            )
            VerticalSpacer(12.dp)
            Text(
                text = "위치, 포즈, 아카이빙까지",
                style = NekiTheme.typography.body16SemiBold,
                color = NekiTheme.colorScheme.white,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun LoginBackgroundPreview() {
    NekiTheme {
        LoginBackground()
    }
}

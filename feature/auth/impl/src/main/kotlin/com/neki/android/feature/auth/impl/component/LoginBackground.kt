package com.neki.android.feature.auth.impl.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.VerticalSpacer

@Composable
internal fun LoginBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        NekiTheme.colorScheme.primary500,
                        NekiTheme.colorScheme.primary300,
                    ),
                ),
            )
    ) {
        BackgroundTitle(
            modifier = Modifier.padding(start = 32.dp, top = 164.dp)
        )
    }
}

@Composable
fun BackgroundTitle(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.36.dp)
        ) {
            Image(
                modifier = Modifier.width(33.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_neki_logo_white),
                contentDescription = null
            )
            Image(
                modifier = Modifier.width(94.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_neki_text_logo_white),
                contentDescription = null
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

@ComponentPreview
@Composable
private fun LoginBackgroundPreview() {
    NekiTheme {
        LoginBackground()
    }
}

@Preview(backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun BackgroundTitlePreview() {
    NekiTheme {
        BackgroundTitle()
    }
}

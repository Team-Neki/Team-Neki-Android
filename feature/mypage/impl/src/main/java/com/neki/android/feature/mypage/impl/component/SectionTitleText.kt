package com.neki.android.feature.mypage.impl.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun SectionTitleText(
    modifier: Modifier = Modifier,
    text: String,
    paddingTop: Dp = 12.dp,
    paddingBottom: Dp = 4.dp,
    paddingStart: Dp = 20.dp,
    paddingEnd: Dp = 20.dp,
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = paddingStart,
                end = paddingEnd,
                top = paddingTop,
                bottom = paddingBottom,
            ),
        text = text,
        style = NekiTheme.typography.caption12Medium,
        color = NekiTheme.colorScheme.gray400,
    )
}

@ComponentPreview
@Composable
private fun SectionTitleTextPreview() {
    SectionTitleText(
        text = "권한 설정",
    )
}

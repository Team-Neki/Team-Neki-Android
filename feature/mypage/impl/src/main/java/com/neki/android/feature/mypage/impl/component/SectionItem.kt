package com.neki.android.feature.mypage.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun SectionItem(
    text: String,
    onClick: () -> Unit = {},
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickableSingle(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = text,
            color = NekiTheme.colorScheme.gray900,
            style = NekiTheme.typography.body16Medium,
        )

        trailingContent?.invoke()
    }
}

@Composable
fun SectionArrowItem(
    text: String,
    onClick: () -> Unit = {},
) {
    SectionItem(
        text = text,
        onClick = onClick,
        trailingContent = {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_right),
                tint = NekiTheme.colorScheme.gray300,
                contentDescription = null,
            )
        },
    )
}

@Composable
fun SectionVersionItem(
    appVersion: String,
) {
    SectionItem(
        text = "앱 버전 정보",
        trailingContent = {
            Text(
                text = "v$appVersion",
                color = NekiTheme.colorScheme.gray500,
                style = NekiTheme.typography.body14Medium,
            )
        },
    )
}

@ComponentPreview
@Composable
private fun SectionItemPreview() {
    NekiTheme {
        SectionItem(text = "로그아웃")
    }
}

@ComponentPreview
@Composable
private fun SectionArrowItemPreview() {
    NekiTheme {
        SectionArrowItem(
            text = "기기 권한",
        )
    }
}

@ComponentPreview
@Composable
private fun SectionVersionItemPreview() {
    NekiTheme {
        SectionVersionItem(
            appVersion = "1.3.1",
        )
    }
}

package com.neki.android.feature.mypage.impl.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.clickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.HorizontalSpacer

@Composable
fun ProfileCard(
    profileImageUrl: String = "",
    name: String,
    loginType: String,
    onClickCard: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableSingle(onClick = onClickCard)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            modifier = Modifier
                .size(78.dp)
                .clip(CircleShape),
            model = profileImageUrl.ifEmpty { R.drawable.image_empty_profile_image },
            contentDescription = null,
        )
        HorizontalSpacer(16.dp)
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = name,
                style = NekiTheme.typography.title18SemiBold,
                color = NekiTheme.colorScheme.gray900,
            )
            Text(
                text = "$loginType 로그인",
                style = NekiTheme.typography.caption12Regular,
                color = NekiTheme.colorScheme.gray600,
            )
        }
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_right),
            contentDescription = null,
            tint = NekiTheme.colorScheme.gray400,
        )
    }
}

@ComponentPreview
@Composable
private fun ProfileCardPreview() {
    NekiTheme {
        ProfileCard(
            name = "오종석",
            loginType = "KAKAO"
        )
    }
}

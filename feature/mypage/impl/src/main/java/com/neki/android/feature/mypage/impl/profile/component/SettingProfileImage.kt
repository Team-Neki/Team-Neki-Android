package com.neki.android.feature.mypage.impl.profile.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.VerticalSpacer

@Composable
internal fun SettingProfileImage(
    nickname: String,
    profileImage: Any? = null,
    imageSize: Dp = 142.dp,
    onClickEdit: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(top = 20.dp, bottom = 27.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            modifier = Modifier
                .size(imageSize)
                .clip(CircleShape),
            model = profileImage ?: R.drawable.image_empty_profile_image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        VerticalSpacer(16.dp)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = nickname,
                style = NekiTheme.typography.title20Medium,
                color = NekiTheme.colorScheme.gray900,
            )
            Icon(
                modifier = Modifier.noRippleClickableSingle(onClick = onClickEdit),
                imageVector = ImageVector.vectorResource(R.drawable.icon_edit),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun SettingProfileImagePreview() {
    NekiTheme {
        SettingProfileImage(
            nickname = "네키네키",
            onClickEdit = {},
        )
    }
}

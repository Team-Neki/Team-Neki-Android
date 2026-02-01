package com.neki.android.feature.mypage.impl.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
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
fun ProfileImage(
    nickname: String = "",
    isEdit: Boolean = false,
    profileImage: Any? = null,
    imageSize: Dp = 142.dp,
    onClickCameraIcon: () -> Unit = {},
    onClickEdit: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            AsyncImage(
                modifier = Modifier
                    .size(imageSize)
                    .clip(CircleShape),
                model = profileImage ?: R.drawable.image_empty_profile_image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
            if (isEdit) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .border(
                            width = 1.dp,
                            shape = CircleShape,
                            color = NekiTheme.colorScheme.primary400,
                        )
                        .background(
                            color = NekiTheme.colorScheme.white,
                            shape = CircleShape,
                        )
                        .padding(8.dp)
                        .noRippleClickableSingle(onClick = onClickCameraIcon),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.icon_camera),
                        contentDescription = null,
                        tint = NekiTheme.colorScheme.primary400,
                    )
                }
            }
        }
        if (!isEdit) {
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
}

@ComponentPreview
@Composable
private fun ProfileImagePreview() {
    NekiTheme {
        ProfileImage(
            isEdit = false,
            nickname = "네키네키",
        )
    }
}

@ComponentPreview
@Composable
private fun IsEditProfileImagePreview() {
    NekiTheme {
        ProfileImage(isEdit = true)
    }
}

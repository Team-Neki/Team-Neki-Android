package com.neki.android.feature.mypage.impl.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun EditProfileImage(
    profileImage: Any? = null,
    imageSize: Dp = 142.dp,
    onClickCameraIcon: () -> Unit,
) {
    Box(
        modifier = Modifier.padding(top = 20.dp, bottom = 28.dp),
    ) {
        AsyncImage(
            modifier = Modifier
                .size(imageSize)
                .clip(CircleShape),
            model = profileImage ?: R.drawable.image_profile_empty,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.image_profile_empty),
            error = painterResource(R.drawable.image_profile_empty),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(
                    color = NekiTheme.colorScheme.gray700,
                    shape = CircleShape,
                )
                .noRippleClickableSingle(onClick = onClickCameraIcon)
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.icon_camera_filled),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun EditProfileImagePreview() {
    NekiTheme {
        EditProfileImage(
            onClickCameraIcon = {},
        )
    }
}

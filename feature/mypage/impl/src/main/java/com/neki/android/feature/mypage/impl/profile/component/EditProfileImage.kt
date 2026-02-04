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
            model = profileImage ?: R.drawable.image_empty_profile_image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
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

@ComponentPreview
@Composable
private fun EditProfileImagePreview() {
    NekiTheme {
        EditProfileImage(
            onClickCameraIcon = {},
        )
    }
}

package com.neki.android.feature.mypage.impl.profile.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.topbar.BackTitleTextButtonTopBar
import com.neki.android.core.designsystem.topbar.BackTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun ProfileSettingTopBar(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
) {
    BackTitleTopBar(
        modifier = modifier,
        title = "계정 설정",
        onBack = onBack,
    )
}

@Composable
fun ProfileEditTopBar(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onBack: () -> Unit = {},
    onClickComplete: () -> Unit = {},
) {
    BackTitleTextButtonTopBar(
        modifier = modifier,
        title = "프로필 편집",
        buttonLabel = "완료",
        enabled = enabled,
        onBack = onBack,
        onTextButtonClick = onClickComplete,
    )
}

@ComponentPreview
@Composable
private fun ProfileSettingTopBarPreview() {
    NekiTheme {
        ProfileSettingTopBar()
    }
}

@ComponentPreview
@Composable
private fun ProfileEditTopBarPreview() {
    NekiTheme {
        ProfileEditTopBar()
    }
}

@ComponentPreview
@Composable
private fun ProfileEditTopBarDisabledPreview() {
    NekiTheme {
        ProfileEditTopBar(enabled = false)
    }
}

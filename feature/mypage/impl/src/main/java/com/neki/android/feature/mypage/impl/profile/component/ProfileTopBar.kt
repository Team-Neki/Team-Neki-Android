package com.neki.android.feature.mypage.impl.profile.component

import androidx.compose.runtime.Composable
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.topbar.BackTitleTextButtonTopBar
import com.neki.android.core.designsystem.topbar.BackTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun ProfileSettingTopBar(
    onBack: () -> Unit = {},
) {
    BackTitleTopBar(
        title = "계정 설정",
        onBack = onBack,
    )
}

@Composable
internal fun ProfileEditTopBar(
    enabled: Boolean = true,
    onBack: () -> Unit = {},
    onClickComplete: () -> Unit = {},
) {
    BackTitleTextButtonTopBar(
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

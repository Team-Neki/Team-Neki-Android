package com.neki.android.feature.mypage.impl.profile.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.ui.component.SelectDialog
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import kotlinx.collections.immutable.toImmutableList

internal enum class ProfileImageOption(val label: String) {
    DEFAULT_PROFILE("기본 프로필로 바꾸기"),
    SELECT_PHOTO("사진 선택하기"),
    ;

    override fun toString(): String = label
}

@Composable
internal fun SelectProfileImageDialog(
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    onDismissRequest: () -> Unit = {},
    onSelect: (ProfileImageOption) -> Unit = {},
) {
    SelectDialog(
        options = ProfileImageOption.entries.toImmutableList(),
        onDismissRequest = onDismissRequest,
        onSelect = onSelect,
        properties = properties,
    )
}

@ComponentPreview
@Composable
private fun SelectProfileImageDialogPreview() {
    NekiTheme {
        SelectProfileImageDialog()
    }
}

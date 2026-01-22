package com.neki.android.feature.mypage.impl.permission

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.topbar.BackTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.mypage.impl.component.PermissionSectionItem
import com.neki.android.feature.mypage.impl.component.SectionTitleText

@Composable
fun PermissionRoute(
    navigateBack: () -> Unit = {},
) {
    PermissionScreen(
        navigateBack = navigateBack,
    )
}

@Composable
fun PermissionScreen(
    navigateBack: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BackTitleTopBar(
            title = "기기 권한",
            onBack = navigateBack
        )
        SectionTitleText(text = "권한 설정")
        PermissionSectionItem(
            title = "카메라",
            subTitle = "QR 촬영에 필요해요.",
            isGranted = false,
            onClick = {},
        )
        PermissionSectionItem(
            title = "위치",
            subTitle = "주변 포토부스 탐색에 필요해요.",
            isGranted = true,
            onClick = {},
        )
        PermissionSectionItem(
            title = "저장소",
            subTitle = "사진 저장 및 업로드에 필요해요.",
            isGranted = false,
            onClick = {},
        )
        PermissionSectionItem(
            title = "알림",
            subTitle = "저장 사진 및 추억 리마인드에 필요해요.",
            isGranted = true,
            onClick = {},
        )
    }
}

@ComponentPreview
@Composable
private fun PermissionScreenPreview() {
    NekiTheme {
        PermissionScreen()
    }
}

package com.neki.android.feature.mypage.impl.permission

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.dialog.DoubleButtonAlertDialog
import com.neki.android.core.designsystem.topbar.BackTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.mypage.impl.component.PermissionSectionItem
import com.neki.android.feature.mypage.impl.component.SectionTitleText
import com.neki.android.feature.mypage.impl.permission.const.NekiPermission
import com.neki.android.feature.mypage.impl.main.MyPageEffect
import com.neki.android.feature.mypage.impl.main.MyPageIntent
import com.neki.android.feature.mypage.impl.main.MyPageState
import com.neki.android.feature.mypage.impl.main.MyPageViewModel

@Composable
internal fun PermissionRoute(
    viewModel: MyPageViewModel = hiltViewModel(),
    navigateBack: () -> Unit = {},
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            MyPageEffect.NavigateBack -> navigateBack()
            is MyPageEffect.MoveAppSettings -> {
                // TODO: 앱 설정 화면으로 이동
            }

            else -> {}
        }
    }

    PermissionScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
fun PermissionScreen(
    uiState: MyPageState = MyPageState(),
    onIntent: (MyPageIntent) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BackTitleTopBar(
            title = "기기 권한",
            onBack = { onIntent(MyPageIntent.ClickBackIcon) },
        )
        SectionTitleText(text = "권한 설정")
        NekiPermission.entries.forEach { permission ->
            PermissionSectionItem(
                title = permission.title,
                subTitle = permission.subTitle,
                isGranted = when (permission) {
                    NekiPermission.CAMERA -> uiState.isGrantedCamera
                    NekiPermission.LOCATION -> uiState.isGrantedLocation
                    NekiPermission.NOTIFICATION -> uiState.isGrantedNotification
                },
                onClick = { onIntent(MyPageIntent.ClickPermissionItem(permission)) },
            )
        }
    }

    if (uiState.isShowPermissionDialog && uiState.selectedPermission != null) {
        DoubleButtonAlertDialog(
            title = uiState.selectedPermission.title,
            content = uiState.selectedPermission.subTitle,
            grayButtonText = "취소",
            primaryButtonText = "확인",
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { onIntent(MyPageIntent.DismissPermissionDialog) },
            onClickGrayButton = { onIntent(MyPageIntent.DismissPermissionDialog) },
            onClickPrimaryButton = { onIntent(MyPageIntent.ConfirmPermissionDialog) },
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

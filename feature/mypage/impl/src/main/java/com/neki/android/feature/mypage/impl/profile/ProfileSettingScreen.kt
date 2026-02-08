package com.neki.android.feature.mypage.impl.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.common.kakao.KakaoAuthHelper
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.dialog.DoubleButtonAlertDialog
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.mypage.impl.component.SectionItem
import com.neki.android.feature.mypage.impl.component.SectionTitleText
import com.neki.android.feature.mypage.impl.main.MyPageEffect
import com.neki.android.feature.mypage.impl.main.MyPageIntent
import com.neki.android.feature.mypage.impl.main.MyPageState
import com.neki.android.feature.mypage.impl.main.MyPageViewModel
import com.neki.android.feature.mypage.impl.profile.component.ProfileSettingTopBar
import com.neki.android.feature.mypage.impl.profile.component.SettingProfileImage
import timber.log.Timber

@Composable
internal fun ProfileSettingRoute(
    viewModel: MyPageViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateToLogin: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            MyPageEffect.NavigateBack -> navigateBack()
            MyPageEffect.NavigateToEditProfile -> navigateToEditProfile()
            MyPageEffect.NavigateToLogin -> navigateToLogin()
            MyPageEffect.LogoutWithKakao -> {
                KakaoAuthHelper.logout(
                    onSuccess = { navigateToLogin() },
                    onFailure = { Timber.e(it) },
                )
            }
            MyPageEffect.UnlinkWithKakao -> {
                KakaoAuthHelper.unlink(
                    onSuccess = { navigateToLogin() },
                    onFailure = { Timber.e(it) },
                )
            }
            else -> {}
        }
    }

    ProfileSettingScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
fun ProfileSettingScreen(
    uiState: MyPageState = MyPageState(),
    onIntent: (MyPageIntent) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProfileSettingTopBar(
            onBack = { onIntent(MyPageIntent.ClickBackIcon) },
        )
        SettingProfileImage(
            nickname = uiState.userInfo.nickname,
            profileImage = uiState.userInfo.profileImageUrl,
            onClickEdit = { onIntent(MyPageIntent.ClickEditIcon) },
        )
        SectionTitleText(text = "서비스 정보 및 지원")
        SectionItem(
            text = "로그아웃",
            onClick = { onIntent(MyPageIntent.ClickLogout) },
        )
        SectionItem(
            text = "탈퇴하기",
            onClick = { onIntent(MyPageIntent.ClickWithdraw) },
        )
    }

    if (uiState.isShowLogoutDialog) {
        DoubleButtonAlertDialog(
            title = "로그아웃을 하시겠습니까?",
            content = "다시 로그인해야 서비스를 이용할 수 있어요.",
            grayButtonText = "취소",
            primaryButtonText = "확인",
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { onIntent(MyPageIntent.DismissLogoutDialog) },
            onClickGrayButton = { onIntent(MyPageIntent.DismissLogoutDialog) },
            onClickPrimaryButton = { onIntent(MyPageIntent.ConfirmLogout) },
        )
    }

    if (uiState.isShowWithdrawDialog) {
        DoubleButtonAlertDialog(
            title = "정말 탈퇴하시겠어요?",
            content = "계정을 탈퇴하면 사진과 정보가 모두 삭제되며,\n삭제된 데이터는 복구할 수 없어요.",
            grayButtonText = "취소",
            primaryButtonText = "탈퇴 확정",
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { onIntent(MyPageIntent.DismissWithdrawDialog) },
            onClickGrayButton = { onIntent(MyPageIntent.DismissWithdrawDialog) },
            onClickPrimaryButton = { onIntent(MyPageIntent.ConfirmWithdraw) },
        )
    }

    if (uiState.isLoading) {
        LoadingDialog()
    }
}

@ComponentPreview
@Composable
private fun ProfileSettingScreenPreview() {
    NekiTheme {
        ProfileSettingScreen()
    }
}

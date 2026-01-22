package com.neki.android.feature.mypage.impl.main

import android.net.Uri

data class MyPageState(
    val isLoading: Boolean = false,
    val userName: String = "오종석",
    val profileImageUri: Uri? = null,
    val appVersion: String = "v1.3.1",
    val isShowLogoutDialog: Boolean = false,
    val isShowWithdrawDialog: Boolean = false,
    val profileMode: ProfileMode = ProfileMode.SETTING,
)

sealed interface MyPageIntent {
    // MyPage Main
    data object ClickNotificationIcon : MyPageIntent
    data object ClickProfileCard : MyPageIntent
    data object ClickPermission : MyPageIntent
    data object ClickInquiry : MyPageIntent
    data object ClickTermsOfService : MyPageIntent
    data object ClickPrivacyPolicy : MyPageIntent
    data object ClickOpenSourceLicense : MyPageIntent

    // Profile
    data object ClickBackIcon : MyPageIntent
    data object ClickEditIcon : MyPageIntent
    data class SelectProfileImage(val uri: Uri) : MyPageIntent
    data class ClickEditComplete(val nickname: String) : MyPageIntent
    data object ClickLogout : MyPageIntent
    data object DismissLogoutDialog : MyPageIntent
    data object ConfirmLogout : MyPageIntent
    data object CancelLogout : MyPageIntent
    data object DismissSignOutDialog : MyPageIntent
    data object ConfirmSignOut : MyPageIntent
}

sealed interface MyPageEffect {
    data object NavigateToNotification : MyPageEffect
    data object NavigateToProfile : MyPageEffect
    data object NavigateToPermission : MyPageEffect
    data object NavigateToInquiry : MyPageEffect
    data object NavigateToTermsOfService : MyPageEffect
    data object NavigateToPrivacyPolicy : MyPageEffect
    data object NavigateToOpenSourceLicense : MyPageEffect
    data object NavigateBack : MyPageEffect
    data object NavigateToLogin : MyPageEffect
}

enum class ProfileMode { SETTING, EDIT }

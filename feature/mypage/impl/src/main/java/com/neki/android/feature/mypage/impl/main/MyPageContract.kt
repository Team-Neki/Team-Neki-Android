package com.neki.android.feature.mypage.impl.main

import com.neki.android.core.model.UserInfo
import com.neki.android.feature.mypage.impl.main.const.ServiceInfoMenu
import com.neki.android.feature.mypage.impl.profile.model.EditProfileImageType

data class MyPageState(
    val isLoading: Boolean = false,
    val userInfo: UserInfo = UserInfo(),
    val appVersion: String = "",
    val profileImageState: EditProfileImageType = EditProfileImageType.OriginalImageUrl(""),
    val isShowLogoutDialog: Boolean = false,
    val isShowWithdrawDialog: Boolean = false,
    val isShowImageSelectDialog: Boolean = false,
)

sealed interface MyPageIntent {
    // Init
    data object EnterMypageScreen : MyPageIntent
    data class SetAppVersion(val appVersion: String) : MyPageIntent

    // MyPage Main
    data object ClickNotificationIcon : MyPageIntent
    data object ClickProfileCard : MyPageIntent
    data object ClickPermission : MyPageIntent
    data class ClickServiceInfoMenu(val menu: ServiceInfoMenu) : MyPageIntent
    data object ClickOpenSourceLicense : MyPageIntent

    // Profile
    data object ClickBackIcon : MyPageIntent
    data object ClickEditIcon : MyPageIntent
    data object ClickCameraIcon : MyPageIntent
    data object DismissImageSelectDialog : MyPageIntent
    data class SelectProfileImage(val image: EditProfileImageType) : MyPageIntent
    data class ClickEditComplete(val nickname: String) : MyPageIntent
    data object ClickLogout : MyPageIntent
    data object DismissLogoutDialog : MyPageIntent
    data object ConfirmLogout : MyPageIntent
    data object ClickWithdraw : MyPageIntent
    data object DismissWithdrawDialog : MyPageIntent
    data object ConfirmWithdraw : MyPageIntent
}

sealed interface MyPageEffect {
    data object NavigateToNotification : MyPageEffect
    data object NavigateToProfile : MyPageEffect
    data object NavigateToEditProfile : MyPageEffect
    data object NavigateToPermission : MyPageEffect
    data class OpenExternalLink(val url: String) : MyPageEffect
    data object NavigateBack : MyPageEffect
    data object OpenOssLicenses : MyPageEffect
    data object LogoutWithKakao : MyPageEffect
    data object UnlinkWithKakao : MyPageEffect
    data class PreloadImageAndNavigateBack(val url: String) : MyPageEffect
}

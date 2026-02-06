package com.neki.android.feature.mypage.impl.main

import android.net.Uri
import com.neki.android.core.model.UserInfo
import com.neki.android.feature.mypage.impl.main.const.ServiceInfoMenu
import com.neki.android.feature.mypage.impl.permission.const.NekiPermission
import com.neki.android.feature.mypage.impl.profile.model.SelectedProfileImage

data class MyPageState(
    val isLoading: Boolean = false,
    val userInfo: UserInfo = UserInfo(),
    val appVersion: String = "",
    val selectedProfileImage: SelectedProfileImage = SelectedProfileImage.NoChange,
    val isShowLogoutDialog: Boolean = false,
    val isShowWithdrawDialog: Boolean = false,
    val isShowImageChooseDialog: Boolean = false,
    // Permission
    val isGrantedCamera: Boolean = false,
    val isGrantedLocation: Boolean = false,
    val isGrantedNotification: Boolean = false,
    val isShowPermissionDialog: Boolean = false,
    val clickedPermission: NekiPermission? = null,
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
    data object DismissImageChooseDialog : MyPageIntent
    data class SelectProfileImage(val image: SelectedProfileImage) : MyPageIntent
    data class ClickEditComplete(
        val nickname: String,
        val uri: Uri?,
    ) : MyPageIntent
    data object ClickLogout : MyPageIntent
    data object DismissLogoutDialog : MyPageIntent
    data object ConfirmLogout : MyPageIntent
    data object ClickWithdraw : MyPageIntent
    data object DismissWithdrawDialog : MyPageIntent
    data object ConfirmWithdraw : MyPageIntent

    // Permission
    data class ClickPermissionItem(val permission: NekiPermission) : MyPageIntent
    data object DismissPermissionDialog : MyPageIntent
    data object ConfirmPermissionDialog : MyPageIntent
    data class UpdatePermissionState(val permission: NekiPermission, val isGranted: Boolean) : MyPageIntent
    data class ShowPermissionDeniedDialog(val permission: NekiPermission) : MyPageIntent
}

sealed interface MyPageEffect {
    data object NavigateToNotification : MyPageEffect
    data object NavigateToProfile : MyPageEffect
    data object NavigateToEditProfile : MyPageEffect
    data object NavigateToPermission : MyPageEffect
    data class OpenExternalLink(val url: String) : MyPageEffect
    data object NavigateBack : MyPageEffect
    data object NavigateToLogin : MyPageEffect
    data class MoveAppSettings(val permission: NekiPermission) : MyPageEffect
    data class RequestPermission(val permission: NekiPermission) : MyPageEffect
    data object OpenOssLicenses : MyPageEffect
    data object LogoutWithKakao : MyPageEffect
    data object UnlinkWithKakao : MyPageEffect
}

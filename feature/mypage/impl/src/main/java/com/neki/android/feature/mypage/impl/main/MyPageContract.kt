package com.neki.android.feature.mypage.impl.main

import android.net.Uri
import com.neki.android.feature.mypage.impl.main.const.ServiceInfoMenu
import com.neki.android.feature.mypage.impl.permission.const.NekiPermission

data class MyPageState(
    val isLoading: Boolean = false,
    val id: Long = 0,
    val nickname: String = "",
    val profileImageUrl: String = "",
    val loginType: String = "",
    val profileImageUri: Uri? = null,
    val isShowLogoutDialog: Boolean = false,
    val isShowSignOutDialog: Boolean = false,
    val isShowImageChooseDialog: Boolean = false,
    val profileMode: ProfileMode = ProfileMode.SETTING,
    // Permission
    val isGrantedCamera: Boolean = false,
    val isGrantedLocation: Boolean = false,
    val isGrantedNotification: Boolean = false,
    val isShowPermissionDialog: Boolean = false,
    val clickedPermission: NekiPermission? = null,
)

sealed interface MyPageIntent {
    // Init
    data object LoadUserInfo : MyPageIntent

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
    data class SelectProfileImage(val uri: Uri?) : MyPageIntent
    data class ClickEditComplete(val nickname: String) : MyPageIntent
    data object ClickLogout : MyPageIntent
    data object DismissLogoutDialog : MyPageIntent
    data object ConfirmLogout : MyPageIntent
    data object ClickSignOut : MyPageIntent
    data object DismissSignOutDialog : MyPageIntent
    data object ConfirmSignOut : MyPageIntent

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
    data object NavigateToPermission : MyPageEffect
    data class OpenExternalLink(val url: String) : MyPageEffect
    data object NavigateBack : MyPageEffect
    data object NavigateToLogin : MyPageEffect
    data class MoveAppSettings(val permission: NekiPermission) : MyPageEffect
    data class RequestPermission(val permission: NekiPermission) : MyPageEffect
    data object OpenOssLicenses : MyPageEffect
}

enum class ProfileMode { SETTING, EDIT }

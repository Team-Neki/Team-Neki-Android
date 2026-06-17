package com.neki.android.feature.mypage.impl.permission

import com.neki.android.core.common.permission.NekiPermission
import com.neki.android.core.model.UserInfo

data class PermissionState(
    val isLoading: Boolean = false,
    val userInfo: UserInfo = UserInfo(),
    val committedMarketingNotification: Boolean = false,
    val isGrantedCamera: Boolean = false,
    val isGrantedLocation: Boolean = false,
    val isGrantedNotification: Boolean = false,
    val isShowPermissionDialog: Boolean = false,
    val clickedPermission: NekiPermission? = null,
)

sealed interface PermissionIntent {
    data object EnterPermissionScreen : PermissionIntent

    data object ClickBackIcon : PermissionIntent
    data class ClickPermissionItem(val permission: NekiPermission) : PermissionIntent
    data object DismissPermissionDialog : PermissionIntent
    data object ConfirmPermissionDialog : PermissionIntent
    data class UpdatePermissionState(val permission: NekiPermission, val isGranted: Boolean) : PermissionIntent
    data class ShowPermissionDeniedDialog(val permission: NekiPermission) : PermissionIntent

    data object ToggleMarketingNotification : PermissionIntent
    data class MarketingNotificationCommitted(val newValue: Boolean, val toastMessage: String) : PermissionIntent
    data class RevertMarketingNotification(val originalValue: Boolean) : PermissionIntent
}

sealed interface PermissionEffect {
    data object NavigateBack : PermissionEffect
    data class RequestPermission(val permission: NekiPermission) : PermissionEffect
    data class MoveAppSettings(val permission: NekiPermission) : PermissionEffect
    data class ShowToast(val message: String) : PermissionEffect
}

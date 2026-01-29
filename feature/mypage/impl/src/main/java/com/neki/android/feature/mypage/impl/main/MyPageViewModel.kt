package com.neki.android.feature.mypage.impl.main

import androidx.lifecycle.ViewModel
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.mypage.impl.permission.const.NekiPermission
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class MyPageViewModel @Inject constructor() : ViewModel() {

    val store: MviIntentStore<MyPageState, MyPageIntent, MyPageEffect> =
        mviIntentStore(
            initialState = MyPageState(),
            onIntent = ::onIntent,
        )

    private fun onIntent(
        intent: MyPageIntent,
        state: MyPageState,
        reduce: (MyPageState.() -> MyPageState) -> Unit,
        postSideEffect: (MyPageEffect) -> Unit,
    ) {
        when (intent) {
            // MyPage Main
            MyPageIntent.ClickNotificationIcon -> postSideEffect(MyPageEffect.NavigateToNotification)
            MyPageIntent.ClickProfileCard -> postSideEffect(MyPageEffect.NavigateToProfile)
            MyPageIntent.ClickPermission -> postSideEffect(MyPageEffect.NavigateToPermission)
            is MyPageIntent.ClickServiceInfoMenu -> postSideEffect(MyPageEffect.OpenExternalLink(intent.menu.url))

            // Profile
            MyPageIntent.ClickBackIcon -> {
                if (state.profileMode == ProfileMode.EDIT) {
                    reduce { copy(profileMode = ProfileMode.SETTING) }
                } else {
                    postSideEffect(MyPageEffect.NavigateBack)
                }
            }
            MyPageIntent.ClickEditIcon -> reduce { copy(profileMode = ProfileMode.EDIT) }
            MyPageIntent.ClickCameraIcon -> reduce { copy(isShowImageChooseDialog = true) }
            MyPageIntent.DismissImageChooseDialog -> reduce { copy(isShowImageChooseDialog = false) }
            is MyPageIntent.SelectProfileImage -> reduce { copy(profileImageUri = intent.uri, isShowImageChooseDialog = false) }
            is MyPageIntent.ClickEditComplete -> {
                reduce { copy(userName = intent.nickname, profileMode = ProfileMode.SETTING) }
            }
            MyPageIntent.ClickLogout -> reduce { copy(isShowLogoutDialog = true) }
            MyPageIntent.DismissLogoutDialog -> reduce { copy(isShowLogoutDialog = false) }
            MyPageIntent.ConfirmLogout -> {
                reduce { copy(isShowLogoutDialog = false) }
                // TODO: 실제 로그아웃 처리
                postSideEffect(MyPageEffect.NavigateToLogin)
            }
            MyPageIntent.ClickSignOut -> reduce { copy(isShowSignOutDialog = true) }
            MyPageIntent.DismissSignOutDialog -> reduce { copy(isShowSignOutDialog = false) }
            MyPageIntent.ConfirmSignOut -> {
                reduce { copy(isShowSignOutDialog = false) }
                // TODO: 실제 탈퇴 처리
                postSideEffect(MyPageEffect.NavigateToLogin)
            }

            // Permission
            is MyPageIntent.ClickPermissionItem -> {
                postSideEffect(MyPageEffect.RequestPermission(intent.permission))
            }
            MyPageIntent.DismissPermissionDialog -> {
                reduce { copy(isShowPermissionDialog = false, clickedPermission = null) }
            }
            MyPageIntent.ConfirmPermissionDialog -> {
                val permission = state.clickedPermission
                reduce { copy(isShowPermissionDialog = false, clickedPermission = null) }
                if (permission != null) {
                    postSideEffect(MyPageEffect.MoveAppSettings(permission))
                }
            }
            is MyPageIntent.GrantedPermission -> {
                when (intent.permission) {
                    NekiPermission.CAMERA -> reduce { copy(isGrantedCamera = true) }
                    NekiPermission.LOCATION -> reduce { copy(isGrantedLocation = true) }
                    NekiPermission.NOTIFICATION -> reduce { copy(isGrantedNotification = true) }
                }
            }
            is MyPageIntent.DeniedPermission -> {
                reduce { copy(isShowPermissionDialog = true, clickedPermission = intent.permission) }
            }
        }
    }
}

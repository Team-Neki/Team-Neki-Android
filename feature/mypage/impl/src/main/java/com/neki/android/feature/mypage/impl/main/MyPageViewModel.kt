package com.neki.android.feature.mypage.impl.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.AuthRepository
import com.neki.android.core.dataapi.repository.TokenRepository
import com.neki.android.core.dataapi.repository.UserRepository
import com.neki.android.core.domain.usecase.UploadProfileImageUseCase
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.mypage.impl.permission.const.NekiPermission
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class MyPageViewModel @Inject constructor(
    private val uploadProfileImageUseCase: UploadProfileImageUseCase,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository,
) : ViewModel() {

    val store: MviIntentStore<MyPageState, MyPageIntent, MyPageEffect> =
        mviIntentStore(
            initialState = MyPageState(),
            onIntent = ::onIntent,
            initialFetchData = { store.onIntent(MyPageIntent.LoadUserInfo) }
        )

    private fun onIntent(
        intent: MyPageIntent,
        state: MyPageState,
        reduce: (MyPageState.() -> MyPageState) -> Unit,
        postSideEffect: (MyPageEffect) -> Unit,
    ) {
        when (intent) {
            MyPageIntent.LoadUserInfo -> loadUserInfo(reduce)
            // MyPage Main
            MyPageIntent.ClickNotificationIcon -> postSideEffect(MyPageEffect.NavigateToNotification)
            MyPageIntent.ClickProfileCard -> postSideEffect(MyPageEffect.NavigateToProfile)
            MyPageIntent.ClickPermission -> postSideEffect(MyPageEffect.NavigateToPermission)
            is MyPageIntent.ClickServiceInfoMenu -> postSideEffect(MyPageEffect.OpenExternalLink(intent.menu.url))
            MyPageIntent.ClickOpenSourceLicense -> postSideEffect(MyPageEffect.OpenOssLicenses)

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
            is MyPageIntent.SelectProfileImage -> reduce { copy(selectedImageUri = intent.uri, isShowImageChooseDialog = false) }
            is MyPageIntent.ClickEditComplete -> {
                updateProfile(intent.nickname, intent.imageBytes, reduce, postSideEffect)
            }
            MyPageIntent.ClickLogout -> reduce { copy(isShowLogoutDialog = true) }
            MyPageIntent.DismissLogoutDialog -> reduce { copy(isShowLogoutDialog = false) }
            MyPageIntent.ConfirmLogout -> {
                reduce { copy(isShowLogoutDialog = false) }
                logout(postSideEffect)
            }
            MyPageIntent.ClickSignOut -> reduce { copy(isShowSignOutDialog = true) }
            MyPageIntent.DismissSignOutDialog -> reduce { copy(isShowSignOutDialog = false) }
            MyPageIntent.ConfirmSignOut -> {
                reduce { copy(isShowSignOutDialog = false) }
                signOut(reduce, postSideEffect)
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
            is MyPageIntent.UpdatePermissionState -> {
                when (intent.permission) {
                    NekiPermission.CAMERA -> reduce { copy(isGrantedCamera = intent.isGranted) }
                    NekiPermission.LOCATION -> reduce { copy(isGrantedLocation = intent.isGranted) }
                    NekiPermission.NOTIFICATION -> reduce { copy(isGrantedNotification = intent.isGranted) }
                }
            }
            is MyPageIntent.ShowPermissionDeniedDialog -> {
                reduce { copy(isShowPermissionDialog = true, clickedPermission = intent.permission) }
            }
        }
    }

    private fun loadUserInfo(reduce: (MyPageState.() -> MyPageState) -> Unit) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }
            userRepository.getUserInfo()
                .onSuccess { user ->
                    reduce {
                        copy(
                            isLoading = false,
                            id = user.id,
                            nickname = user.nickname,
                            profileImageUrl = user.profileImageUrl,
                            loginType = user.loginType,
                        )
                    }
                }
                .onFailure {
                    reduce { copy(isLoading = false) }
                }
        }
    }

    private fun updateProfile(
        nickname: String,
        imageBytes: ByteArray?,
        reduce: (MyPageState.() -> MyPageState) -> Unit,
        postSideEffect: (MyPageEffect) -> Unit,
    ) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }

            if (imageBytes != null) {
                // 이미지가 있으면 이미지 업로드 후 프로필 업데이트
                uploadProfileImageUseCase(imageBytes = imageBytes, nickname = nickname)
                    .onSuccess {
                        reduce { copy(isLoading = false, profileMode = ProfileMode.SETTING, selectedImageUri = null) }
                        store.onIntent(MyPageIntent.LoadUserInfo)
                    }
                    .onFailure {
                        reduce { copy(isLoading = false) }
                    }
            } else {
                // 이미지가 없으면 닉네임만 업데이트
                userRepository.updateUserInfo(mediaId = null, nickname = nickname)
                    .onSuccess {
                        reduce { copy(isLoading = false, profileMode = ProfileMode.SETTING) }
                        store.onIntent(MyPageIntent.LoadUserInfo)
                    }
                    .onFailure {
                        reduce { copy(isLoading = false) }
                    }
            }
        }
    }

    private fun logout(postSideEffect: (MyPageEffect) -> Unit) {
        viewModelScope.launch {
            tokenRepository.clearTokens()
            postSideEffect(MyPageEffect.NavigateToLogin)
        }
    }

    private fun signOut(
        reduce: (MyPageState.() -> MyPageState) -> Unit,
        postSideEffect: (MyPageEffect) -> Unit,
    ) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }
            authRepository.signOut()
                .onSuccess {
                    tokenRepository.clearTokens()
                    postSideEffect(MyPageEffect.NavigateToLogin)
                }
                .onFailure {
                    Timber.e(it)
                    reduce { copy(isLoading = false) }
                }
        }
    }
}

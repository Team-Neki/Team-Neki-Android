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
import com.neki.android.feature.mypage.impl.profile.model.EditProfileImageType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
            initialFetchData = { store.onIntent(MyPageIntent.EnterMypageScreen) },
        )

    private fun onIntent(
        intent: MyPageIntent,
        state: MyPageState,
        reduce: (MyPageState.() -> MyPageState) -> Unit,
        postSideEffect: (MyPageEffect) -> Unit,
    ) {
        when (intent) {
            MyPageIntent.EnterMypageScreen -> fetchInitialData(reduce)
            is MyPageIntent.SetAppVersion -> reduce { copy(appVersion = intent.appVersion) }
            // MyPage Main
            MyPageIntent.ClickNotificationIcon -> postSideEffect(MyPageEffect.NavigateToNotification)
            MyPageIntent.ClickProfileCard -> postSideEffect(MyPageEffect.NavigateToProfile)
            MyPageIntent.ClickPermission -> postSideEffect(MyPageEffect.NavigateToPermission)
            is MyPageIntent.ClickServiceInfoMenu -> postSideEffect(MyPageEffect.OpenExternalLink(intent.menu.url))
            MyPageIntent.ClickOpenSourceLicense -> postSideEffect(MyPageEffect.OpenOssLicenses)

            // Profile
            MyPageIntent.ClickBackIcon -> {
                reduce { copy(profileImageState = EditProfileImageType.OriginalImageUrl(state.userInfo.profileImageUrl)) }
                postSideEffect(MyPageEffect.NavigateBack)
            }

            MyPageIntent.ClickEditIcon -> postSideEffect(MyPageEffect.NavigateToEditProfile)
            MyPageIntent.ClickCameraIcon -> reduce { copy(isShowImageChooseDialog = true) }
            MyPageIntent.DismissImageChooseDialog -> reduce { copy(isShowImageChooseDialog = false) }
            is MyPageIntent.SelectProfileImage -> reduce { copy(profileImageState = intent.image, isShowImageChooseDialog = false) }
            is MyPageIntent.ClickEditComplete -> {
                val isNicknameChanged = state.userInfo.nickname != intent.nickname
                val isProfileImageChanged = state.profileImageState !is EditProfileImageType.OriginalImageUrl
                updateProfile(state, intent.nickname, isNicknameChanged, isProfileImageChanged, reduce, postSideEffect)
            }

            MyPageIntent.ClickLogout -> reduce { copy(isShowLogoutDialog = true) }
            MyPageIntent.DismissLogoutDialog -> reduce { copy(isShowLogoutDialog = false) }
            MyPageIntent.ConfirmLogout -> {
                reduce { copy(isShowLogoutDialog = false) }
                logout(postSideEffect)
            }

            MyPageIntent.ClickWithdraw -> reduce { copy(isShowWithdrawDialog = true) }
            MyPageIntent.DismissWithdrawDialog -> reduce { copy(isShowWithdrawDialog = false) }
            MyPageIntent.ConfirmWithdraw -> {
                reduce { copy(isShowWithdrawDialog = false) }
                withdrawAccount(reduce, postSideEffect)
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

    private fun fetchInitialData(reduce: (MyPageState.() -> MyPageState) -> Unit) = viewModelScope.launch {
        reduce { copy(isLoading = true) }
        userRepository.getUserInfo()
            .onSuccess { user ->
                reduce {
                    copy(
                        isLoading = false,
                        userInfo = user,
                    )
                }
            }
            .onFailure {
                Timber.e(it)
                reduce { copy(isLoading = false) }
            }
    }

    private fun updateProfile(
        state: MyPageState,
        nickname: String,
        isNicknameChanged: Boolean,
        isProfileImageChanged: Boolean,
        reduce: (MyPageState.() -> MyPageState) -> Unit,
        postSideEffect: (MyPageEffect) -> Unit,
    ) = viewModelScope.launch {
        if (!isNicknameChanged && !isProfileImageChanged) {
            postSideEffect(MyPageEffect.NavigateBack)
            return@launch
        }

        reduce { copy(isLoading = true) }

        buildList {
            if (isNicknameChanged) add(async { userRepository.updateUserInfo(nickname = nickname) })
            if (isProfileImageChanged) {
                val uri = (state.profileImageState as? EditProfileImageType.ImageUri)?.uri
                add(async { uploadProfileImageUseCase(uri = uri) })
            }
        }.awaitAll()

        userRepository.getUserInfo()
            .onSuccess { user ->
                if (isProfileImageChanged) {
                    postSideEffect(MyPageEffect.PreloadImageAndNavigateBack(user.profileImageUrl))
                }
                reduce {
                    copy(
                        isLoading = false,
                        profileImageState = EditProfileImageType.OriginalImageUrl(user.profileImageUrl),
                        userInfo = user,
                    )
                }
            }
            .onFailure {
                Timber.e(it)
                reduce {
                    copy(
                        isLoading = false,
                        profileImageState = EditProfileImageType.OriginalImageUrl(state.userInfo.profileImageUrl),
                    )
                }
                postSideEffect(MyPageEffect.NavigateBack)
            }
    }

    private fun logout(postSideEffect: (MyPageEffect) -> Unit) = viewModelScope.launch {
        tokenRepository.clearTokens()
        postSideEffect(MyPageEffect.LogoutWithKakao)
    }

    private fun withdrawAccount(
        reduce: (MyPageState.() -> MyPageState) -> Unit,
        postSideEffect: (MyPageEffect) -> Unit,
    ) = viewModelScope.launch {
        reduce { copy(isLoading = true) }
        authRepository.withdrawAccount()
            .onSuccess {
                tokenRepository.clearTokens()
                reduce { copy(isLoading = false) }
                postSideEffect(MyPageEffect.UnlinkWithKakao)
            }
            .onFailure {
                Timber.e(it)
                reduce { copy(isLoading = false) }
            }
    }
}

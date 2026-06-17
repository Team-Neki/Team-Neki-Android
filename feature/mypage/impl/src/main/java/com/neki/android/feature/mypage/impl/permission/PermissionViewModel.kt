package com.neki.android.feature.mypage.impl.permission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.common.const.TermConst
import com.neki.android.core.common.coroutine.di.ApplicationScope
import com.neki.android.core.common.permission.NekiPermission
import com.neki.android.core.dataapi.repository.TermRepository
import com.neki.android.core.dataapi.repository.UserRepository
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
internal class PermissionViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val termRepository: TermRepository,
    @ApplicationScope private val applicationScope: CoroutineScope,
) : ViewModel() {

    private val marketingToggleRequests = MutableSharedFlow<Boolean>(extraBufferCapacity = 64)

    val store: MviIntentStore<PermissionState, PermissionIntent, PermissionEffect> =
        mviIntentStore(
            initialState = PermissionState(),
            onIntent = ::onIntent,
        )

    init {
        store.onIntent(PermissionIntent.EnterPermissionScreen)

        viewModelScope.launch {
            marketingToggleRequests
                .debounce(300)
                .collect { newValue ->
                    val committed = store.uiState.value.committedMarketingNotification
                    if (committed != newValue) {
                        termRepository.updateTermAgreement(TermConst.MARKETING_TERM_ID, newValue)
                            .onSuccess {
                                val message = if (newValue) {
                                    "마케팅 알림 수신에 동의했어요."
                                } else {
                                    "마케팅 알림 수신을 거부했어요.\n마이페이지에서 언제든지 변경할 수 있어요."
                                }
                                store.onIntent(PermissionIntent.MarketingNotificationCommitted(newValue, message))
                            }
                            .onFailure { e ->
                                Timber.e(e, "updateMarketingTerm failed")
                                store.onIntent(PermissionIntent.RevertMarketingNotification(committed))
                            }
                    }
                }
        }
    }

    private fun onIntent(
        intent: PermissionIntent,
        state: PermissionState,
        reduce: (PermissionState.() -> PermissionState) -> Unit,
        postSideEffect: (PermissionEffect) -> Unit,
    ) {
        when (intent) {
            PermissionIntent.EnterPermissionScreen -> fetchUserInfo(reduce)
            PermissionIntent.ClickBackIcon -> postSideEffect(PermissionEffect.NavigateBack)

            is PermissionIntent.ClickPermissionItem ->
                postSideEffect(PermissionEffect.RequestPermission(intent.permission))

            PermissionIntent.DismissPermissionDialog ->
                reduce { copy(isShowPermissionDialog = false, clickedPermission = null) }

            PermissionIntent.ConfirmPermissionDialog -> {
                val permission = state.clickedPermission
                reduce { copy(isShowPermissionDialog = false, clickedPermission = null) }
                if (permission != null) postSideEffect(PermissionEffect.MoveAppSettings(permission))
            }

            is PermissionIntent.UpdatePermissionState -> {
                when (intent.permission) {
                    NekiPermission.CAMERA -> reduce { copy(isGrantedCamera = intent.isGranted) }
                    NekiPermission.LOCATION -> reduce { copy(isGrantedLocation = intent.isGranted) }
                    NekiPermission.NOTIFICATION -> reduce { copy(isGrantedNotification = intent.isGranted) }
                }
            }

            is PermissionIntent.ShowPermissionDeniedDialog ->
                reduce { copy(isShowPermissionDialog = true, clickedPermission = intent.permission) }

            PermissionIntent.ToggleMarketingNotification -> {
                val newValue = !state.isMarketingTermAgreed
                reduce { copy(isMarketingTermAgreed = newValue) }
                viewModelScope.launch { marketingToggleRequests.emit(newValue) }
            }

            is PermissionIntent.MarketingNotificationCommitted -> {
                reduce { copy(committedMarketingNotification = intent.newValue) }
                postSideEffect(PermissionEffect.ShowToast(intent.toastMessage))
            }

            is PermissionIntent.RevertMarketingNotification ->
                reduce { copy(isMarketingTermAgreed = intent.originalValue) }
        }
    }

    private fun fetchUserInfo(reduce: (PermissionState.() -> PermissionState) -> Unit) =
        viewModelScope.launch {
            reduce { copy(isLoading = true) }
            userRepository.getUserInfo()
                .onSuccess { user ->
                    reduce {
                        copy(
                            isLoading = false,
                            isMarketingTermAgreed = user.isMarketingTermAgreed,
                            committedMarketingNotification = user.isMarketingTermAgreed,
                        )
                    }
                }
                .onFailure { e ->
                    Timber.e(e)
                    reduce { copy(isLoading = false) }
                }
        }

    override fun onCleared() {
        super.onCleared()
        val current = store.uiState.value.isMarketingTermAgreed
        val committed = store.uiState.value.committedMarketingNotification
        if (current != committed) {
            applicationScope.launch {
                termRepository.updateTermAgreement(TermConst.MARKETING_TERM_ID, current)
            }
        }
    }
}

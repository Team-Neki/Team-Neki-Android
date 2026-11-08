package com.neki.android.feature.archive.impl.main

import android.os.Build
import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.analytics.event.ArchiveAnalyticsEvent
import com.neki.android.core.analytics.logger.AnalyticsLogger
import com.neki.android.core.common.const.TermConst
import com.neki.android.core.common.permission.NotificationGuideType
import com.neki.android.core.common.permission.marketingAgreementToastMessage
import com.neki.android.core.dataapi.repository.FolderRepository
import com.neki.android.core.dataapi.repository.NotificationRepository
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.dataapi.repository.TermRepository
import com.neki.android.core.dataapi.repository.UserRepository
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

private const val DEFAULT_PHOTOS_SIZE = 20
private const val MARKETING_AGREEMENT_FAILED_MESSAGE = "알림 동의에 실패했어요"

@HiltViewModel
class ArchiveMainViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val folderRepository: FolderRepository,
    private val userRepository: UserRepository,
    private val termRepository: TermRepository,
    private val notificationRepository: NotificationRepository,
    private val analyticsLogger: AnalyticsLogger,
) : ViewModel() {

    val store: MviIntentStore<ArchiveMainState, ArchiveMainIntent, ArchiveMainSideEffect> =
        mviIntentStore(
            initialState = ArchiveMainState(),
            onIntent = ::onIntent,
        )
    private var fetchJob: Job? = null
    private var lastNotificationGuideCheckEpochDay = -1L
    private var pendingAgreementToastMessage: String? = null
    private var checkedVersionCodeBeforeGuide = -1L

    init {
        store.onIntent(ArchiveMainIntent.EnterArchiveMainScreen)
    }

    fun logArchivingView() {
        analyticsLogger.log(ArchiveAnalyticsEvent.ArchivingView)
    }

    private fun onIntent(
        intent: ArchiveMainIntent,
        state: ArchiveMainState,
        reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit,
        postSideEffect: (ArchiveMainSideEffect) -> Unit,
    ) {
        if (intent != ArchiveMainIntent.EnterArchiveMainScreen && intent !is ArchiveMainIntent.CheckNotificationGuide) {
            reduce { copy(isFirstEntered = false) }
        }
        when (intent) {
            ArchiveMainIntent.EnterArchiveMainScreen -> fetchInitialData(reduce)
            ArchiveMainIntent.RefreshArchiveMain -> viewModelScope.launch {
                awaitAll(
                    async { fetchFavoriteSummary(reduce) },
                    async { fetchPhotos(reduce) },
                    async { fetchFolders(reduce) },
                )
            }
            ArchiveMainIntent.ClickScreen -> reduce { copy(isFirstEntered = false) }
            ArchiveMainIntent.ClickGoToTopButton -> postSideEffect(ArchiveMainSideEffect.ScrollToTop)

            // TopBar Intent
            ArchiveMainIntent.DismissToolTipPopup -> {
                reduce { copy(isFirstEntered = false) }
                viewModelScope.launch { userRepository.setQRInfoToolTipShown() }
            }

            ArchiveMainIntent.ClickQRScanIcon -> postSideEffect(ArchiveMainSideEffect.NavigateToQRScan)
            ArchiveMainIntent.ClickNotificationIcon -> postSideEffect(ArchiveMainSideEffect.RequestNotificationPermission)

            // Notification Permission Intent
            ArchiveMainIntent.RequestedNotificationPermission ->
                viewModelScope.launch { userRepository.setNotificationPermissionRequested() }
            ArchiveMainIntent.GrantNotificationPermission -> postSideEffect(ArchiveMainSideEffect.NavigateToNotification)
            ArchiveMainIntent.DenyNotificationPermissionPermanent -> reduce { copy(showNotificationPermissionDeniedDialog = true) }
            ArchiveMainIntent.DismissNotificationPermissionDialog -> reduce {
                copy(showNotificationPermissionDeniedDialog = false, isNotificationDeniedDialogFromGuide = false)
            }
            ArchiveMainIntent.ClickMoveToNotificationAppSettings -> {
                reduce { copy(showNotificationPermissionDeniedDialog = false, isNotificationDeniedDialogFromGuide = false) }
                postSideEffect(
                    if (state.isNotificationDeniedDialogFromGuide) {
                        ArchiveMainSideEffect.MoveAppSettingsForNotificationGuide
                    } else {
                        ArchiveMainSideEffect.MoveAppSettingsForNotification
                    },
                )
            }

            // Album Intent
            ArchiveMainIntent.ClickAllAlbumText -> postSideEffect(ArchiveMainSideEffect.NavigateToAllAlbum)
            ArchiveMainIntent.ClickFavoriteAlbum -> postSideEffect(ArchiveMainSideEffect.NavigateToFavoriteAlbum(-1L))
            is ArchiveMainIntent.ClickAlbumItem -> postSideEffect(ArchiveMainSideEffect.NavigateToAlbumDetail(intent.albumId, intent.albumTitle))
            ArchiveMainIntent.ClickAddAlbum -> reduce { copy(isShowAddAlbumBottomSheet = true, albumNameTextState = TextFieldState()) }

            // Photo Intent
            ArchiveMainIntent.ClickAllPhotoText -> postSideEffect(ArchiveMainSideEffect.NavigateToAllPhoto)
            is ArchiveMainIntent.ClickPhotoItem -> postSideEffect(ArchiveMainSideEffect.NavigateToPhotoDetail(state.recentPhotos, intent.index))
            is ArchiveMainIntent.ClickFavoriteIcon -> handleFavoriteToggle(intent.photo, reduce)

            // Add Album BottomSheet Intent
            ArchiveMainIntent.DismissAddAlbumBottomSheet -> reduce { copy(isShowAddAlbumBottomSheet = false) }
            ArchiveMainIntent.ClickAddAlbumButton -> handleAddAlbum(state.albumNameTextState.text.trim().toString(), reduce, postSideEffect)

            // Notification Guide Intent
            is ArchiveMainIntent.CheckNotificationGuide -> checkNotificationGuide(intent, reduce)
            ArchiveMainIntent.ClickNotificationGuideConfirm -> handleNotificationGuideConfirm(state, reduce, postSideEffect)
            ArchiveMainIntent.DismissNotificationGuide -> reduce { copy(notificationGuideType = null) }
            ArchiveMainIntent.DenyNotificationGuidePermissionPermanent -> reduce {
                copy(showNotificationPermissionDeniedDialog = true, isNotificationDeniedDialogFromGuide = true)
            }
            is ArchiveMainIntent.CompleteNotificationGuidePermission -> completeNotificationGuidePermission(intent.isGranted, postSideEffect)
        }
    }

    private fun fetchInitialData(reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit) {
        if (fetchJob?.isActive == true) return

        fetchJob = viewModelScope.launch {
            reduce { copy(isLoading = true) }
            try {
                awaitAll(
                    async { checkFirstVisit(reduce) },
                    async { fetchFavoriteSummary(reduce) },
                    async { fetchPhotos(reduce) },
                    async { fetchFolders(reduce) },
                )
            } finally {
                reduce { copy(isLoading = false) }
            }
        }
    }

    private fun checkNotificationGuide(
        intent: ArchiveMainIntent.CheckNotificationGuide,
        reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit,
    ) {
        // 같은 화면 인스턴스에서는 하루 1번만 판정한다. 날짜가 바뀌면 재진입 시 다시 판정한다.
        val today = LocalDate.now().toEpochDay()
        if (lastNotificationGuideCheckEpochDay == today) return
        lastNotificationGuideCheckEpochDay = today

        viewModelScope.launch {
            val checkedVersionCode = userRepository.updateNotificationGuideVersionCode.first()
            val shouldCheck = NotificationGuideType.shouldCheckUpdateGuide(
                todayEpochDay = today,
                lastAutoGuideEpochDay = userRepository.lastAutoNotificationGuideEpochDay.first(),
                currentVersionCode = intent.appVersionCode,
                checkedVersionCode = checkedVersionCode,
            )
            if (!shouldCheck) return@launch
            checkedVersionCodeBeforeGuide = checkedVersionCode

            val userInfo = userRepository.getUserInfo()
                .onFailure { e -> Timber.e(e) }
                .getOrNull() ?: return@launch
            val guideType = NotificationGuideType.resolve(
                isNotificationGranted = intent.isNotificationGranted,
                isMarketingAgreed = userInfo.isMarketingTermAgreed,
                canRequestNotificationPermission = NotificationGuideType.canRequestNotificationPermission(
                    sdkInt = Build.VERSION.SDK_INT,
                    hasRequestedBefore = userRepository.hasRequestedNotificationPermission.first(),
                    shouldShowRationale = intent.shouldShowNotificationRationale,
                ),
            )

            userRepository.setUpdateNotificationGuideChecked(intent.appVersionCode)
            if (guideType == null) return@launch

            userRepository.setAutoNotificationGuideShown(today)
            reduce { copy(notificationGuideType = guideType, isMarketingTermAgreed = userInfo.isMarketingTermAgreed) }
        }
    }

    private fun handleNotificationGuideConfirm(
        state: ArchiveMainState,
        reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit,
        postSideEffect: (ArchiveMainSideEffect) -> Unit,
    ) {
        val guideType = state.notificationGuideType ?: return
        reduce { copy(notificationGuideType = null) }

        viewModelScope.launch {
            when (guideType) {
                NotificationGuideType.INTEGRATED -> {
                    pendingAgreementToastMessage = agreeMarketingTermForGuide()
                    requestNotificationGuidePermission(postSideEffect)
                }

                NotificationGuideType.ENABLE_NOTIFICATION -> requestNotificationGuidePermission(postSideEffect)

                NotificationGuideType.MARKETING_CONSENT -> {
                    postSideEffect(ArchiveMainSideEffect.ShowToastMessage(agreeMarketingTermForGuide()))
                }

                NotificationGuideType.OPEN_SETTINGS -> {
                    if (!state.isMarketingTermAgreed) pendingAgreementToastMessage = agreeMarketingTermForGuide()
                    postSideEffect(ArchiveMainSideEffect.MoveAppSettingsForNotificationGuide)
                }
            }
        }
    }

    /**
     * 알림 안내 모달에서 이벤트·혜택 알림 동의를 저장하고 띄울 토스트 문구를 반환한다.
     * 저장에 실패하면 판정 버전을 되돌려, 다음 판정 때 안내를 다시 띄울 수 있게 한다.
     */
    private suspend fun agreeMarketingTermForGuide(): String {
        if (agreeMarketingTerm()) return marketingAgreementToastMessage()
        userRepository.setUpdateNotificationGuideChecked(checkedVersionCodeBeforeGuide)
        return MARKETING_AGREEMENT_FAILED_MESSAGE
    }

    private suspend fun requestNotificationGuidePermission(postSideEffect: (ArchiveMainSideEffect) -> Unit) {
        userRepository.setNotificationPermissionRequested()
        postSideEffect(ArchiveMainSideEffect.RequestNotificationGuidePermission)
    }

    private fun completeNotificationGuidePermission(
        isGranted: Boolean,
        postSideEffect: (ArchiveMainSideEffect) -> Unit,
    ) {
        pendingAgreementToastMessage?.let { message ->
            pendingAgreementToastMessage = null
            postSideEffect(ArchiveMainSideEffect.ShowToastMessage(message))
        }
        updatePushAgreement(isGranted)
    }

    private fun updatePushAgreement(isGranted: Boolean) {
        viewModelScope.launch {
            val token = notificationRepository.getPushToken() ?: return@launch
            notificationRepository.updateNotification(token, isGranted)
                .onFailure { e -> Timber.e(e) }
        }
    }

    private suspend fun checkFirstVisit(reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit) {
        if (!userRepository.hasShownQRInfoToolTip.first()) {
            reduce { copy(isFirstEntered = true) }
        }
    }

    private suspend fun fetchFavoriteSummary(reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit) {
        photoRepository.getFavoriteSummary()
            .onSuccess { data ->
                reduce { copy(favoriteAlbum = data) }
            }
            .onFailure { e ->
                Timber.e(e)
            }
    }

    private suspend fun fetchPhotos(reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit, size: Int = DEFAULT_PHOTOS_SIZE) {
        photoRepository.getPhotos(size = size)
            .onSuccess { data ->
                reduce { copy(recentPhotos = data.toImmutableList()) }
            }
            .onFailure { e ->
                Timber.e(e)
            }
    }

    private suspend fun fetchFolders(reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit) {
        folderRepository.getFolders()
            .onSuccess { data ->
                reduce { copy(albums = data.toImmutableList()) }
            }
            .onFailure { e ->
                Timber.e(e)
            }
    }

    private fun handleFavoriteToggle(
        photo: Photo,
        reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit,
    ) {
        val newFavorite = !photo.isFavorite
        reduce {
            copy(
                recentPhotos = recentPhotos.map {
                    if (it.id == photo.id) it.copy(isFavorite = newFavorite) else it
                }.toImmutableList(),
            )
        }
        viewModelScope.launch {
            photoRepository.updateFavorite(photo.id, newFavorite)
                .onSuccess {
                    fetchFavoriteSummary(reduce)
                }
                .onFailure { e ->
                    Timber.e(e)
                    reduce {
                        copy(
                            recentPhotos = recentPhotos.map {
                                if (it.id == photo.id) it.copy(isFavorite = photo.isFavorite) else it
                            }.toImmutableList(),
                        )
                    }
                }
        }
    }

    private suspend fun agreeMarketingTerm(): Boolean =
        termRepository.updateTermAgreement(termId = TermConst.MARKETING_TERM_ID, agreed = true)
            .onFailure { e -> Timber.e(e) }
            .isSuccess

    private fun handleAddAlbum(
        albumName: String,
        reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit,
        postSideEffect: (ArchiveMainSideEffect) -> Unit,
    ) {
        viewModelScope.launch {
            folderRepository.createFolder(name = albumName)
                .onSuccess {
                    analyticsLogger.log(ArchiveAnalyticsEvent.AlbumCreate)
                    fetchFolders(reduce)
                    postSideEffect(ArchiveMainSideEffect.ShowToastMessage("새로운 앨범을 추가했어요"))
                }
                .onFailure { e ->
                    postSideEffect(ArchiveMainSideEffect.ShowToastMessage("앨범 추가에 실패했어요"))
                    Timber.e(e)
                }
            reduce { copy(isShowAddAlbumBottomSheet = false, albumNameTextState = TextFieldState()) }
        }
    }
}

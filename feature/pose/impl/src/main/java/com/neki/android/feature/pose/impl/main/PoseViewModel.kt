package com.neki.android.feature.pose.impl.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.neki.android.core.analytics.event.PoseAnalyticsEvent
import com.neki.android.core.analytics.logger.AnalyticsLogger
import com.neki.android.core.dataapi.repository.PoseRepository
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import com.neki.android.core.model.SortOrder
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.pose.api.PoseNavKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class PoseViewModel @Inject constructor(
    private val poseRepository: PoseRepository,
    private val analyticsLogger: AnalyticsLogger,
) : ViewModel() {

    private val _headCountFilter = MutableStateFlow<PeopleCount?>(null)
    private val _isBookmarkOnly = MutableStateFlow(false)
    private val updatedBookmarks = MutableStateFlow<Map<Long, Boolean>>(emptyMap())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val originalPagingData: Flow<PagingData<Pose>> = combine(
        _headCountFilter,
        _isBookmarkOnly,
    ) { headCount, isBookmarkOnly ->
        headCount to isBookmarkOnly
    }.flatMapLatest { (headCount, isBookmarkOnly) ->
        if (isBookmarkOnly) {
            poseRepository.getBookmarkedPosesFlow()
        } else {
            poseRepository.getPosesFlow(
                headCount = headCount,
                sortOrder = SortOrder.DESC,
            )
        }
    }.cachedIn(viewModelScope)

    val posePagingData: Flow<PagingData<Pose>> = combine(
        originalPagingData,
        updatedBookmarks,
    ) { pagingData, bookmarks ->
        val isBookmarkOnly = _isBookmarkOnly.value
        pagingData
            .map { pose ->
                bookmarks[pose.id]?.let { isBookmarked ->
                    pose.copy(isBookmarked = isBookmarked)
                } ?: pose
            }
            .let { data ->
                if (isBookmarkOnly) data.filter { it.isBookmarked } else data
            }
    }

    val store: MviIntentStore<PoseState, PoseIntent, PoseEffect> =
        mviIntentStore(
            initialState = PoseState(),
            onIntent = ::onIntent,
        )

    fun logPoseView() {
        analyticsLogger.log(PoseAnalyticsEvent.PoseView)
    }

    private fun onIntent(
        intent: PoseIntent,
        state: PoseState,
        reduce: (PoseState.() -> PoseState) -> Unit,
        postSideEffect: (PoseEffect) -> Unit,
    ) {
        when (intent) {
            // Pose Main
            PoseIntent.EnterPoseScreen -> {}
            PoseIntent.ClickAlarmIcon -> postSideEffect(PoseEffect.NavigateToNotification)
            PoseIntent.ClickQRScanIcon -> postSideEffect(PoseEffect.NavigateToQRScan)
            PoseIntent.ClickPeopleCountChip -> reduce { copy(isShowPeopleCountBottomSheet = true) }
            is PoseIntent.ClickPeopleCountSheetItem -> handlePeopleCountSheetItem(intent, state, reduce, postSideEffect)
            PoseIntent.DismissPeopleCountBottomSheet -> reduce { copy(isShowPeopleCountBottomSheet = false) }
            PoseIntent.DismissRandomPosePeopleCountBottomSheet -> reduce { copy(isShowRandomPosePeopleCountBottomSheet = false) }
            PoseIntent.ClickBookmarkChip -> {
                analyticsLogger.log(PoseAnalyticsEvent.PoseBookmarkFilter)
                updatePoseFilter(state, state.toggleBookmarkFilter(), reduce, postSideEffect)
            }

            is PoseIntent.ClickRelationTagChip -> {
                updatePoseFilter(state, state.toggleRelationTagFilter(intent.relationTag), reduce, postSideEffect)
            }

            is PoseIntent.ClickPoseItem -> {
                analyticsLogger.log(
                    PoseAnalyticsEvent.PoseDetailClick(
                        poseId = intent.item.id.toString(),
                        peopleCount = intent.item.peopleCount,
                        entryPoint = if (state.isShowBookmarkedPose) "bookmark" else "list",
                    ),
                )
                postSideEffect(
                    PoseEffect.NavigateToPoseDetail(
                        PoseNavKey.PoseDetail(
                            poseId = intent.item.id,
                            initialIndex = intent.index,
                            headCount = state.selectedPeopleCount,
                            sortOrder = SortOrder.DESC,
                            isBookmarkOnly = state.isShowBookmarkedPose,
                        ),
                    ),
                )
            }

            PoseIntent.ClickRandomPoseRecommendation -> reduce { copy(isShowRandomPosePeopleCountBottomSheet = true) }
            is PoseIntent.ClickRandomPosePeopleCountSheetItem -> reduce { copy(selectedRandomPosePeopleCount = intent.peopleCount) }
            PoseIntent.ClickRandomPoseBottomSheetSelectButton -> {
                val selectedCount = state.selectedRandomPosePeopleCount ?: return
                reduce {
                    copy(
                        selectedRandomPosePeopleCount = null,
                        isShowRandomPosePeopleCountBottomSheet = false,
                    )
                }
                postSideEffect(PoseEffect.NavigateToRandomPose(selectedCount))
            }

            is PoseIntent.ClickBookmarkIcon -> {
                analyticsLogger.log(PoseAnalyticsEvent.PoseBookmark)
                val pose = intent.pose
                val newBookmarked = !pose.isBookmarked
                updatedBookmarks.update { it + (pose.id to newBookmarked) }
                viewModelScope.launch {
                    poseRepository.updateBookmark(pose.id, newBookmarked)
                        .onFailure { e ->
                            Timber.e(e)
                            updatedBookmarks.update { it + (pose.id to pose.isBookmarked) }
                        }
                }
            }

            is PoseIntent.BookmarkChanged -> {
                updatedBookmarks.update { it + (intent.poseId to intent.isBookmarked) }
            }
        }
    }

    private fun handlePeopleCountSheetItem(
        intent: PoseIntent.ClickPeopleCountSheetItem,
        state: PoseState,
        reduce: (PoseState.() -> PoseState) -> Unit,
        postSideEffect: (PoseEffect) -> Unit,
    ) {
        if (intent.peopleCount != state.selectedPeopleCount) {
            analyticsLogger.log(PoseAnalyticsEvent.PoseFilterToggle(peopleCount = intent.peopleCount.value))
        }
        reduce { copy(isShowPeopleCountBottomSheet = false) }
        updatePoseFilter(state, state.togglePeopleCountFilter(intent.peopleCount), reduce, postSideEffect)
    }

    // 관계 태그는 서버 필터가 없어 목록 조회 조건에 포함하지 않는다
    private fun updatePoseFilter(
        state: PoseState,
        filteredState: PoseState,
        reduce: (PoseState.() -> PoseState) -> Unit,
        postSideEffect: (PoseEffect) -> Unit,
    ) {
        _headCountFilter.value = filteredState.selectedPeopleCount
        _isBookmarkOnly.value = filteredState.isShowBookmarkedPose
        reduce {
            copy(
                isShowBookmarkedPose = filteredState.isShowBookmarkedPose,
                selectedPeopleCount = filteredState.selectedPeopleCount,
                selectedRelationTag = filteredState.selectedRelationTag,
            )
        }
        val isQueryChanged = filteredState.selectedPeopleCount != state.selectedPeopleCount ||
            filteredState.isShowBookmarkedPose != state.isShowBookmarkedPose
        if (isQueryChanged) postSideEffect(PoseEffect.ScrollToTop)
    }
}

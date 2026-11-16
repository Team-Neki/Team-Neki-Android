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

    private val poseListQuery = MutableStateFlow(PoseListQuery())
    private val updatedBookmarks = MutableStateFlow<Map<Long, Boolean>>(emptyMap())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val originalPagingData: Flow<PagingData<Pose>> = poseListQuery
        .flatMapLatest { query ->
            if (query.isBookmarkOnly) {
                poseRepository.getBookmarkedPosesFlow()
            } else {
                poseRepository.getPosesFlow(
                    headCount = query.headCount,
                    relation = query.relation,
                    sortOrder = SortOrder.DESC,
                    onTotalCountLoaded = { totalCount ->
                        // 조건이 바뀐 뒤 늦게 도착한 이전 조건의 개수는 반영하지 않는다
                        if (poseListQuery.value == query) store.onIntent(PoseIntent.TotalPoseCountLoaded(totalCount))
                    },
                )
            }
        }.cachedIn(viewModelScope)

    val posePagingData: Flow<PagingData<Pose>> = combine(
        originalPagingData,
        updatedBookmarks,
    ) { pagingData, bookmarks ->
        val isBookmarkOnly = poseListQuery.value.isBookmarkOnly
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
                            relation = state.selectedRelationTag,
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

            is PoseIntent.TotalPoseCountLoaded -> reduce { copy(totalPoseCount = intent.totalPoseCount) }
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

    private fun updatePoseFilter(
        state: PoseState,
        filteredState: PoseState,
        reduce: (PoseState.() -> PoseState) -> Unit,
        postSideEffect: (PoseEffect) -> Unit,
    ) {
        val newQuery = filteredState.toPoseListQuery()
        // 조회 조건이 같으면 목록이 갱신되지 않아 ScrollToTop이 갱신을 기다리며 남으므로 보내지 않는다
        val isQueryChanged = newQuery != state.toPoseListQuery()
        poseListQuery.value = newQuery
        reduce {
            copy(
                isShowBookmarkedPose = filteredState.isShowBookmarkedPose,
                selectedPeopleCount = filteredState.selectedPeopleCount,
                selectedRelationTag = filteredState.selectedRelationTag,
                // 개수 영역이 깜빡이지 않도록 새 개수가 올 때까지 이전 값을 유지하고, 북마크 모드에서만 숨긴다
                totalPoseCount = if (newQuery.isBookmarkOnly) null else totalPoseCount,
            )
        }
        if (isQueryChanged) postSideEffect(PoseEffect.ScrollToTop)
    }
}

package com.neki.android.feature.pose.impl.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.neki.android.core.dataapi.repository.PoseRepository
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import com.neki.android.core.model.SortOrder
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class PoseViewModel @Inject constructor(
    private val poseRepository: PoseRepository,
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
        pagingData.map { pose ->
            bookmarks[pose.id]?.let { isBookmarked ->
                pose.copy(isBookmarked = isBookmarked)
            } ?: pose
        }
    }

    val store: MviIntentStore<PoseState, PoseIntent, PoseEffect> =
        mviIntentStore(
            initialState = PoseState(),
            onIntent = ::onIntent,
        )

    private fun onIntent(
        intent: PoseIntent,
        state: PoseState,
        reduce: (PoseState.() -> PoseState) -> Unit,
        postSideEffect: (PoseEffect) -> Unit,
    ) {
        when (intent) {
            // Pose Main
            PoseIntent.EnterPoseScreen -> Unit
            PoseIntent.ClickAlarmIcon -> postSideEffect(PoseEffect.NavigateToNotification)
            PoseIntent.ClickPeopleCountChip -> reduce { copy(isShowPeopleCountBottomSheet = true) }
            is PoseIntent.ClickPeopleCountSheetItem -> handlePeopleCountSheetItem(intent, state, reduce)
            PoseIntent.DismissPeopleCountBottomSheet -> reduce { copy(isShowPeopleCountBottomSheet = false) }
            PoseIntent.DismissRandomPosePeopleCountBottomSheet -> reduce { copy(isShowRandomPosePeopleCountBottomSheet = false) }
            PoseIntent.ClickBookmarkChip -> {
                val newValue = !state.isShowBookmarkedPose
                _isBookmarkOnly.value = newValue
                _headCountFilter.value = null
                reduce {
                    copy(
                        isShowBookmarkedPose = newValue,
                        selectedPeopleCount = null,
                    )
                }
            }

            is PoseIntent.ClickPoseItem -> {
                postSideEffect(PoseEffect.NavigateToPoseDetail(intent.item.id))
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

            is PoseIntent.BookmarkChanged -> {
                updatedBookmarks.update { it + (intent.poseId to intent.isBookmarked) }
            }
        }
    }

    private fun handlePeopleCountSheetItem(
        intent: PoseIntent.ClickPeopleCountSheetItem,
        state: PoseState,
        reduce: (PoseState.() -> PoseState) -> Unit,
    ) {
        _isBookmarkOnly.value = false
        if (intent.peopleCount == state.selectedPeopleCount) {
            _headCountFilter.value = null
            reduce {
                copy(
                    isShowBookmarkedPose = false,
                    isShowPeopleCountBottomSheet = false,
                    selectedPeopleCount = null,
                )
            }
        } else {
            _headCountFilter.value = intent.peopleCount
            reduce {
                copy(
                    isShowBookmarkedPose = false,
                    selectedPeopleCount = intent.peopleCount.takeIf { it != state.selectedPeopleCount },
                    isShowPeopleCountBottomSheet = false,
                )
            }
        }
    }
}

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
    private val _isScrapOnly = MutableStateFlow(false)
    private val updatedScraps = MutableStateFlow<Map<Long, Boolean>>(emptyMap())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val originalPagingData: Flow<PagingData<Pose>> = combine(
        _headCountFilter,
        _isScrapOnly,
    ) { headCount, isScrapOnly ->
        headCount to isScrapOnly
    }.flatMapLatest { (headCount, isScrapOnly) ->
        if (isScrapOnly) {
            poseRepository.getScrappedPosesFlow()
        } else {
            poseRepository.getPosesFlow(
                headCount = headCount,
                sortOrder = SortOrder.DESC,
            )
        }
    }.cachedIn(viewModelScope)

    val posePagingData: Flow<PagingData<Pose>> = combine(
        originalPagingData,
        updatedScraps,
    ) { pagingData, scraps ->
        pagingData.map { pose ->
            scraps[pose.id]?.let { isScrapped ->
                pose.copy(isScrapped = isScrapped)
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
            PoseIntent.ClickScrapChip -> {
                val newValue = !state.isShowScrappedPose
                _isScrapOnly.value = newValue
                _headCountFilter.value = null
                reduce {
                    copy(
                        isShowScrappedPose = newValue,
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

            is PoseIntent.ScrapChanged -> {
                updatedScraps.update { it + (intent.poseId to intent.isScrapped) }
            }
        }
    }

    private fun handlePeopleCountSheetItem(
        intent: PoseIntent.ClickPeopleCountSheetItem,
        state: PoseState,
        reduce: (PoseState.() -> PoseState) -> Unit,
    ) {
        _isScrapOnly.value = false
        if (intent.peopleCount == state.selectedPeopleCount) {
            _headCountFilter.value = null
            reduce {
                copy(
                    isShowScrappedPose = false,
                    isShowPeopleCountBottomSheet = false,
                    selectedPeopleCount = null,
                )
            }
        } else {
            _headCountFilter.value = intent.peopleCount
            reduce {
                copy(
                    isShowScrappedPose = false,
                    selectedPeopleCount = intent.peopleCount.takeIf { it != state.selectedPeopleCount },
                    isShowPeopleCountBottomSheet = false,
                )
            }
        }
    }
}

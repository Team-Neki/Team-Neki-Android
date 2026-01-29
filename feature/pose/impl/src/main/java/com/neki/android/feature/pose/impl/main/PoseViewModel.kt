package com.neki.android.feature.pose.impl.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.neki.android.core.dataapi.repository.PoseRepository
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import com.neki.android.core.model.PoseEffect
import com.neki.android.core.model.PoseIntent
import com.neki.android.core.model.PoseState
import com.neki.android.core.model.SortOrder
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
internal class PoseViewModel @Inject constructor(
    private val poseRepository: PoseRepository,
) : ViewModel() {

    private val _headCountFilter = MutableStateFlow<PeopleCount?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val posePagingData: Flow<PagingData<Pose>> = _headCountFilter
        .flatMapLatest { headCount ->
            poseRepository.getPosesFlow(
                headCount = headCount,
                sortOrder = SortOrder.DESC,
            )
        }
        .cachedIn(viewModelScope)

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
            is PoseIntent.ClickPeopleCountSheetItem -> {
                _headCountFilter.value = intent.peopleCount
                reduce {
                    copy(
                        isShowScrappedPose = false,
                        selectedPeopleCount = intent.peopleCount,
                        isShowPeopleCountBottomSheet = false,
                    )
                }
            }

            PoseIntent.DismissPeopleCountBottomSheet -> reduce { copy(isShowPeopleCountBottomSheet = false) }
            PoseIntent.DismissRandomPosePeopleCountBottomSheet -> reduce { copy(isShowRandomPosePeopleCountBottomSheet = false) }
            PoseIntent.ClickScrapChip -> {
                _headCountFilter.value = null
                reduce {
                    copy(
                        isShowScrappedPose = !isShowScrappedPose,
                        selectedPeopleCount = null,
                    )
                }
            }

            is PoseIntent.ClickPoseItem -> {
                postSideEffect(PoseEffect.NavigateToPoseDetail(intent.item))
            }

            PoseIntent.ClickRandomPoseRecommendation -> reduce { copy(isShowRandomPosePeopleCountBottomSheet = true) }
            is PoseIntent.ClickRandomPosePeopleCountSheetItem -> reduce { copy(selectedRandomPosePeopleCount = intent.peopleCount) }
            PoseIntent.ClickRandomPoseBottomSheetSelectButton -> {
                val selectedCount = state.selectedRandomPosePeopleCount ?: return
                reduce { copy(isShowRandomPosePeopleCountBottomSheet = false) }
                postSideEffect(PoseEffect.NavigateToRandomPose(selectedCount))
            }
        }
    }
}

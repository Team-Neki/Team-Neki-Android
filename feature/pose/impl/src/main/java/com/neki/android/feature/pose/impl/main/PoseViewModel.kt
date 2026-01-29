package com.neki.android.feature.pose.impl.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.PoseRepository
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.PoseEffect
import com.neki.android.core.model.PoseIntent
import com.neki.android.core.model.PoseState
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class PoseViewModel @Inject constructor(
    private val poseRepository: PoseRepository,
) : ViewModel() {

    val store: MviIntentStore<PoseState, PoseIntent, PoseEffect> =
        mviIntentStore(
            initialState = PoseState(),
            onIntent = ::onIntent,
            initialFetchData = { store.onIntent(PoseIntent.EnterPoseScreen) },
        )

    private fun onIntent(
        intent: PoseIntent,
        state: PoseState,
        reduce: (PoseState.() -> PoseState) -> Unit,
        postSideEffect: (PoseEffect) -> Unit,
    ) {
        when (intent) {
            // Pose Main
            PoseIntent.EnterPoseScreen -> fetchPoses(reduce, postSideEffect)
            PoseIntent.ClickAlarmIcon -> postSideEffect(PoseEffect.NavigateToNotification)
            PoseIntent.ClickPeopleCountChip -> reduce { copy(isShowPeopleCountBottomSheet = true) }
            is PoseIntent.ClickPeopleCountSheetItem -> {
                reduce {
                    copy(
                        isShowScrappedPose = false,
                        selectedPeopleCount = intent.peopleCount,
                        isShowPeopleCountBottomSheet = false,
                    )
                }
                fetchPoses(reduce, postSideEffect, intent.peopleCount)
            }

            PoseIntent.DismissPeopleCountBottomSheet -> reduce { copy(isShowPeopleCountBottomSheet = false) }
            PoseIntent.DismissRandomPosePeopleCountBottomSheet -> reduce { copy(isShowRandomPosePeopleCountBottomSheet = false) }
            PoseIntent.ClickScrapChip -> reduce {
                copy(
                    isShowScrappedPose = !isShowScrappedPose,
                    selectedPeopleCount = null,
                )
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

    private fun fetchPoses(
        reduce: (PoseState.() -> PoseState) -> Unit,
        postSideEffect: (PoseEffect) -> Unit,
        headCount: PeopleCount? = null,
    ) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }
            poseRepository.getPoses(headCount = headCount)
                .onSuccess { poses ->
                    reduce {
                        copy(
                            isLoading = false,
                            poseList = poses.toImmutableList(),
                        )
                    }
                }
                .onFailure { error ->
                    Timber.e(error)
                    reduce { copy(isLoading = false) }
                    postSideEffect(PoseEffect.ShowToast("포즈를 불러오는데 실패했어요"))
                }
        }
    }
}

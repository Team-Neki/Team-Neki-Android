package com.neki.android.feature.pose.impl.main

import androidx.lifecycle.ViewModel
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PoseViewModel @Inject constructor() : ViewModel() {

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
            PoseIntent.EnterPoseScreen -> fetchInitialData(reduce)
            PoseIntent.ClickAlarmIcon -> postSideEffect(PoseEffect.NavigateToNotification)
            PoseIntent.ClickPeopleCountChip -> reduce { copy(isShowPeopleCountBottomSheet = true) }
            is PoseIntent.ClickPeopleCountSheetItem -> reduce {
                copy(
                    isShowScrappedPose = false,
                    selectedPeopleCount = intent.peopleCount,
                    isShowPeopleCountBottomSheet = false,
                )
            }

            PoseIntent.DismissPeopleCountBottomSheet -> reduce { copy(isShowPeopleCountBottomSheet = false) }
            PoseIntent.ClickScrapChip -> reduce {
                copy(
                    isShowScrappedPose = !isShowScrappedPose,
                    selectedPeopleCount = null,
                )
            }

            is PoseIntent.ClickPoseItem -> {
                reduce { copy(selectedPose = intent.item) }
                postSideEffect(PoseEffect.NavigateToPoseDetail)
            }

            PoseIntent.ClickRandomPoseRecommendation -> {}

            // Pose Detail
            PoseIntent.ClickBackIcon -> postSideEffect(PoseEffect.NavigateBack)
            PoseIntent.ClickScrapIcon -> reduce { copy(selectedPose = selectedPose.copy(isScrapped = !selectedPose.isScrapped)) }
        }
    }

    private fun fetchInitialData(reduce: (PoseState.() -> PoseState) -> Unit) {
        reduce { copy() }
    }
}

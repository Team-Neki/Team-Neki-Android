package com.neki.android.feature.pose.impl

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
            PoseIntent.ClickNumberOfPeopleChip -> reduce { copy(isShowNumberOfPeopleBottomSheet = true) }
            is PoseIntent.ClickNumberOfPeopleSheetItem -> reduce {
                copy(
                    isShowScrappedPose = false,
                    selectedNumberOfPeople = intent.numberOfPeople,
                    isShowNumberOfPeopleBottomSheet = false,
                )
            }

            PoseIntent.DismissNumberOfPeopleBottomSheet -> reduce { copy(isShowNumberOfPeopleBottomSheet = false) }
            PoseIntent.ClickScrapChip -> reduce {
                copy(
                    isShowScrappedPose = !isShowScrappedPose,
                    selectedNumberOfPeople = NumberOfPeople.UNSELECTED,
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

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
            PoseIntent.ClickAlarmIcon -> postSideEffect(PoseEffect.NavigateToNotification)
            PoseIntent.ClickNumberOfPeopleChip -> reduce { copy(showNumberOfPeopleBottomSheet = true) }
            is PoseIntent.ClickNumberOfPeopleSheetItem -> reduce {
                copy(
                    showScrappedPose = false,
                    selectedNumberOfPeople = intent.numberOfPeople,
                    showNumberOfPeopleBottomSheet = false,
                )
            }
            PoseIntent.DismissNumberOfPeopleBottomSheet -> reduce { copy(showNumberOfPeopleBottomSheet = false) }
            PoseIntent.ClickScrapChip -> reduce {
                copy(
                    showScrappedPose = !showScrappedPose,
                    selectedNumberOfPeople = NumberOfPeople.UNSELECTED,
                )
            }

            is PoseIntent.ClickPoseItem -> {
                reduce { copy(selectedPose = intent.imageUrl) }
                postSideEffect(PoseEffect.NavigateToPoseDetail)
            }

            PoseIntent.ClickRandomPoseRecommendation -> {}

            // Pose Detail
            PoseIntent.ClickBackIcon -> postSideEffect(PoseEffect.NavigateBack)
            PoseIntent.ClickScrapIcon -> TODO()
        }
    }
}

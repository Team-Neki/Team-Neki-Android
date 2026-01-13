package com.neki.android.feature.pose.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.model.Pose
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class PoseViewModel @Inject constructor() : ViewModel() {

    val store: MviIntentStore<PoseState, PoseIntent, PoseEffect> =
        mviIntentStore(
            initialState = PoseState(),
            onIntent = ::onIntent,
        )

    val uiState = store.uiState.onStart {
        store.onIntent(PoseIntent.EnterPoseScreen)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PoseState(),
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
                reduce { copy(selectedPose = intent.item) }
                postSideEffect(PoseEffect.NavigateToPoseDetail)
            }

            PoseIntent.ClickRandomPoseRecommendation -> {}

            // Pose Detail
            PoseIntent.ClickBackIcon -> postSideEffect(PoseEffect.NavigateBack)
            PoseIntent.ClickScrapIcon -> reduce { copy(selectedPose = selectedPose.copy(isScrapped = !selectedPose.isScrapped)) }
        }
    }

    private fun fetchInitialData(reduce: ((PoseState) -> PoseState) -> Unit) {
        reduce {
            copy(
                randomPoseList = PoseState.dummy.map {
                    Pose(poseImageUrl = it)
                }.toImmutableList(),
            )
        }
    }
}

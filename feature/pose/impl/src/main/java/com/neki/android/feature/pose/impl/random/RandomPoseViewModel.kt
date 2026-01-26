package com.neki.android.feature.pose.impl.random

import androidx.lifecycle.ViewModel
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf

@HiltViewModel(assistedFactory = RandomPoseViewModel.Factory::class)
internal class RandomPoseViewModel @AssistedInject constructor(
    @Assisted private val peopleCount: PeopleCount,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(peopleCount: PeopleCount): RandomPoseViewModel
    }

    private val dummyPoseList = persistentListOf(
        Pose(id = 1, poseImageUrl = "https://picsum.photos/seed/random1/400/520", peopleCount = peopleCount.value),
        Pose(id = 2, poseImageUrl = "https://picsum.photos/seed/random2/400/520", peopleCount = peopleCount.value),
        Pose(id = 3, poseImageUrl = "https://picsum.photos/seed/random3/400/520", peopleCount = peopleCount.value),
    )

    val store: MviIntentStore<RandomPoseUiState, RandomPoseIntent, RandomPoseEffect> =
        mviIntentStore(
            initialState = RandomPoseUiState(),
            onIntent = ::onIntent,
            initialFetchData = { store.onIntent(RandomPoseIntent.EnterRandomPoseScreen) },
        )

    private fun onIntent(
        intent: RandomPoseIntent,
        state: RandomPoseUiState,
        reduce: (RandomPoseUiState.() -> RandomPoseUiState) -> Unit,
        postSideEffect: (RandomPoseEffect) -> Unit,
    ) {
        when (intent) {
            RandomPoseIntent.EnterRandomPoseScreen -> fetchInitialData(reduce)

            // 튜토리얼
            RandomPoseIntent.ClickLeftSwipe -> Unit
            RandomPoseIntent.ClickRightSwipe -> Unit
            RandomPoseIntent.ClickStartRandomPose -> reduce { copy(isShowTutorial = false) }

            // 기본화면
            RandomPoseIntent.ClickCloseIcon -> postSideEffect(RandomPoseEffect.NavigateBack)
            RandomPoseIntent.ClickGoToDetailIcon -> {
                if (state.currentPose.id != 0L) {
                    postSideEffect(RandomPoseEffect.NavigateToDetail(state.currentPose))
                }
            }

            RandomPoseIntent.ClickScrapIcon -> reduce {
                copy(currentPose = currentPose.copy(isScrapped = !currentPose.isScrapped))
            }
        }
    }

    private fun fetchInitialData(reduce: (RandomPoseUiState.() -> RandomPoseUiState) -> Unit) {
        reduce {
            copy(
                randomPoseList = dummyPoseList,
                currentPose = dummyPoseList.firstOrNull() ?: Pose(),
            )
        }
    }
}

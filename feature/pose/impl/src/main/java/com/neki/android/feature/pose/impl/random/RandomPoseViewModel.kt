package com.neki.android.feature.pose.impl.random

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.PoseRepository
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = RandomPoseViewModel.Factory::class)
internal class RandomPoseViewModel @AssistedInject constructor(
    @Suppress("UnusedPrivateProperty") @Assisted private val peopleCount: PeopleCount,
    private val poseRepository: PoseRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(peopleCount: PeopleCount): RandomPoseViewModel
    }

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
            RandomPoseIntent.EnterRandomPoseScreen -> fetchInitialPoses(reduce, postSideEffect)

            // 튜토리얼
            RandomPoseIntent.ClickLeftSwipe -> Unit
            RandomPoseIntent.ClickRightSwipe -> Unit
            RandomPoseIntent.ClickStartRandomPose -> reduce { copy(isShowTutorial = false) }

            // 기본화면
            RandomPoseIntent.ClickCloseIcon -> postSideEffect(RandomPoseEffect.NavigateBack)
            RandomPoseIntent.ClickGoToDetailIcon -> {
                state.currentPose?.let { pose ->
                    postSideEffect(RandomPoseEffect.NavigateToDetail(pose))
                }
            }

            RandomPoseIntent.ClickScrapIcon -> {
                state.currentPose?.let { currentPose ->
                    reduce {
                        copy(
                            poseList = poseList.map { pose ->
                                if (pose.id == currentPose.id) {
                                    pose.copy(isScrapped = !pose.isScrapped)
                                } else {
                                    pose
                                }
                            }.toImmutableList(),
                        )
                    }
                }
            }

            RandomPoseIntent.SwipeLeft -> {
                if (state.hasPrevious) {
                    reduce { copy(currentIndex = currentIndex - 1) }
                }
            }

            RandomPoseIntent.SwipeRight -> {
                if (state.hasNext) {
                    reduce { copy(currentIndex = currentIndex + 1) }
                    // 마지막 포즈에 도달하면 다음 포즈 미리 로드
                    if (state.currentIndex + 1 >= state.poseList.lastIndex) {
                        fetchNextPose(reduce, postSideEffect)
                    }
                }
            }
        }
    }

    private fun fetchInitialPoses(
        reduce: (RandomPoseUiState.() -> RandomPoseUiState) -> Unit,
        postSideEffect: (RandomPoseEffect) -> Unit,
    ) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }

            val poses = mutableListOf<Pose>()

            // 초기에 2개 로드
            repeat(2) {
                poseRepository.getRandomPose()
                    .onSuccess { pose -> poses.add(pose) }
                    .onFailure { error -> Timber.e(error) }
            }

            if (poses.isNotEmpty()) {
                reduce {
                    copy(
                        isLoading = false,
                        poseList = poses.toImmutableList(),
                        currentIndex = 0,
                    )
                }
            } else {
                reduce { copy(isLoading = false) }
                postSideEffect(RandomPoseEffect.ShowToast("포즈를 불러오는데 실패했어요"))
            }
        }
    }

    private fun fetchNextPose(
        reduce: (RandomPoseUiState.() -> RandomPoseUiState) -> Unit,
        postSideEffect: (RandomPoseEffect) -> Unit,
    ) {
        viewModelScope.launch {
            poseRepository.getRandomPose()
                .onSuccess { pose ->
                    reduce {
                        copy(poseList = (poseList + pose).toImmutableList())
                    }
                }
                .onFailure { error ->
                    Timber.e(error)
                }
        }
    }
}

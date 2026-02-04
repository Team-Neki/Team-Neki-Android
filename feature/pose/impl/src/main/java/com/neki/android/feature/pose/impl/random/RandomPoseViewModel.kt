package com.neki.android.feature.pose.impl.random

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.common.coroutine.di.ApplicationScope
import com.neki.android.core.dataapi.repository.PoseRepository
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import com.neki.android.feature.pose.impl.const.PoseConst
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = RandomPoseViewModel.Factory::class)
internal class RandomPoseViewModel @AssistedInject constructor(
    @Assisted private val peopleCount: PeopleCount,
    private val poseRepository: PoseRepository,
    @ApplicationScope private val applicationScope: CoroutineScope,
) : ViewModel() {

    private val scrapJobs = mutableMapOf<Long, Job>()

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
            RandomPoseIntent.ClickLeftSwipe -> handleMovePrevious(state, reduce, postSideEffect)
            RandomPoseIntent.ClickRightSwipe -> handleMoveNext(state, reduce, postSideEffect)
            RandomPoseIntent.ClickStartRandomPose -> reduce { copy(isShowTutorial = false) }

            // 기본화면
            RandomPoseIntent.ClickCloseIcon -> postSideEffect(RandomPoseEffect.NavigateBack)
            RandomPoseIntent.ClickGoToDetailIcon -> {
                state.currentPose?.let { pose ->
                    postSideEffect(RandomPoseEffect.NavigateToDetail(pose.id))
                }
            }

            RandomPoseIntent.ClickScrapIcon -> handleScrapToggle(state, reduce)
        }
    }

    private fun handleScrapToggle(
        state: RandomPoseUiState,
        reduce: (RandomPoseUiState.() -> RandomPoseUiState) -> Unit,
    ) {
        state.currentPose?.let { currentPose ->
            val poseId = currentPose.id
            val newScrapStatus = !currentPose.isScrapped

            // UI 즉시 업데이트
            reduce {
                copy(
                    poseList = poseList.map { pose ->
                        if (pose.id == poseId) {
                            pose.copy(isScrapped = newScrapStatus)
                        } else {
                            pose
                        }
                    }.toImmutableList(),
                )
            }

            // 해당 포즈의 이전 Job 취소 후 새로운 Job 시작
            scrapJobs[poseId]?.cancel()
            scrapJobs[poseId] = viewModelScope.launch {
                delay(500)
                val committedScrap = store.uiState.value.committedScraps[poseId]
                if (committedScrap == newScrapStatus || committedScrap == null) return@launch

                poseRepository.updateScrap(poseId, newScrapStatus)
                    .onSuccess {
                        Timber.d("updateScrap success for poseId: $poseId")
                        reduce {
                            copy(committedScraps = committedScraps + (poseId to newScrapStatus))
                        }
                    }
                    .onFailure { error ->
                        Timber.e(error, "updateScrap failed for poseId: $poseId")
                        reduce {
                            copy(
                                poseList = poseList.map { pose ->
                                    if (pose.id == poseId) {
                                        pose.copy(isScrapped = committedScrap)
                                    } else {
                                        pose
                                    }
                                }.toImmutableList(),
                            )
                        }
                    }
            }
        }
    }

    private fun handleMovePrevious(
        state: RandomPoseUiState,
        reduce: (RandomPoseUiState.() -> RandomPoseUiState) -> Unit,
        postSideEffect: (RandomPoseEffect) -> Unit,
    ) {
        if (state.hasPrevious) {
            reduce { copy(currentIndex = currentIndex - 1) }
        } else {
            postSideEffect(RandomPoseEffect.ShowToast("첫번째 포즈입니다."))
        }
    }

    private fun handleMoveNext(
        state: RandomPoseUiState,
        reduce: (RandomPoseUiState.() -> RandomPoseUiState) -> Unit,
        postSideEffect: (RandomPoseEffect) -> Unit,
    ) {
        if (state.currentIndex >= state.poseList.lastIndex) return

        reduce { copy(currentIndex = currentIndex + 1) }

        // 여분 포즈가 POSE_PREFETCH_THRESHOLD 이하이면 다음 포즈 미리 캐싱
        if (state.poseList.size - state.currentIndex <= PoseConst.POSE_PREFETCH_THRESHOLD) {
            fetchNextPose(reduce, postSideEffect)
        }
    }

    private fun fetchNextPose(
        reduce: (RandomPoseUiState.() -> RandomPoseUiState) -> Unit,
        postSideEffect: (RandomPoseEffect) -> Unit,
    ) {
        viewModelScope.launch {
            poseRepository.getRandomPose(headCount = peopleCount)
                .onSuccess { pose ->
                    reduce {
                        copy(
                            poseList = (poseList + pose).toImmutableList(),
                            committedScraps = committedScraps + (pose.id to pose.isScrapped),
                        )
                    }
                    postSideEffect(RandomPoseEffect.RequestImageBuilder(pose.poseImageUrl))
                }
                .onFailure { error ->
                    Timber.e(error)
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

            // 초기에 INITIAL_POSE_LOAD_COUNT개 로드
            repeat(PoseConst.INITIAL_POSE_LOAD_COUNT) {
                poseRepository.getRandomPose(headCount = peopleCount)
                    .onSuccess { pose -> poses.add(pose) }
                    .onFailure { error -> Timber.e(error) }
            }

            if (poses.isNotEmpty()) {
                reduce {
                    copy(
                        isLoading = false,
                        poseList = poses.toImmutableList(),
                        committedScraps = poses.associate { it.id to it.isScrapped },
                        currentIndex = 0,
                    )
                }
            } else {
                reduce { copy(isLoading = false) }
                postSideEffect(RandomPoseEffect.ShowToast("포즈를 불러오는데 실패했어요"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        val state = store.uiState.value
        state.poseList.forEach { pose ->
            val currentScrap = pose.isScrapped
            val committedScrap = state.committedScraps[pose.id]

            if (committedScrap != null && currentScrap != committedScrap) {
                applicationScope.launch {
                    poseRepository.updateScrap(pose.id, currentScrap)
                }
            }
        }
    }
}

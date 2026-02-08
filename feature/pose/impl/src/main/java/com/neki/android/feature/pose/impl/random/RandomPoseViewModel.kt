package com.neki.android.feature.pose.impl.random

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.common.coroutine.di.ApplicationScope
import com.neki.android.core.common.exception.RandomPoseRetryExhaustedException
import com.neki.android.core.dataapi.repository.PoseRepository
import com.neki.android.core.dataapi.repository.UserRepository
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.pose.impl.const.PoseConst
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = RandomPoseViewModel.Factory::class)
internal class RandomPoseViewModel @AssistedInject constructor(
    @Assisted private val peopleCount: PeopleCount,
    private val poseRepository: PoseRepository,
    private val userRepository: UserRepository,
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
            RandomPoseIntent.EnterRandomPoseScreen -> fetchInitialData(state, reduce, postSideEffect)

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

            RandomPoseIntent.ClickScrapIcon -> {
                val currentPost = state.currentPose ?: return
                handleScrapToggle(currentPost.id, !currentPost.isScrapped, reduce)
            }

            is RandomPoseIntent.ScrapChanged -> handleScrapToggle(intent.poseId, intent.isScrapped, reduce)
        }
    }

    private fun handleScrapToggle(
        poseId: Long,
        newScrapStatus: Boolean,
        reduce: (RandomPoseUiState.() -> RandomPoseUiState) -> Unit,
    ) {
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

    private fun handleMovePrevious(
        state: RandomPoseUiState,
        reduce: (RandomPoseUiState.() -> RandomPoseUiState) -> Unit,
        postSideEffect: (RandomPoseEffect) -> Unit,
    ) {
        if (state.hasPrevious) {
            val previousIndex = state.currentIndex - 1
            reduce { copy(currentIndex = previousIndex) }
            postSideEffect(RandomPoseEffect.SwipePoseImage(previousIndex))
        } else {
            postSideEffect(RandomPoseEffect.ShowToast("첫번째 포즈입니다."))
        }
    }

    private fun handleMoveNext(
        state: RandomPoseUiState,
        reduce: (RandomPoseUiState.() -> RandomPoseUiState) -> Unit,
        postSideEffect: (RandomPoseEffect) -> Unit,
    ) {
        // 마지막 인덱스에 도달
        if (state.currentIndex >= state.poseList.lastIndex) {
            postSideEffect(RandomPoseEffect.ShowToast("모든 포즈를 불러왔어요"))
            return
        }

        val nextIndex = state.currentIndex + 1
        reduce { copy(currentIndex = nextIndex) }
        postSideEffect(RandomPoseEffect.SwipePoseImage(nextIndex))

        // 여분 포즈가 POSE_PREFETCH_THRESHOLD 이하이면 다음 포즈 미리 캐싱
        if (state.poseList.lastIndex - nextIndex < PoseConst.POSE_PREFETCH_THRESHOLD) {
            viewModelScope.launch {
                poseRepository.getSingleRandomPose(
                    headCount = peopleCount,
                    excludeIds = state.randomPoseIds,
                    maxRetry = PoseConst.MAXIMUM_RANDOM_POSE_RETRY_COUNT,
                ).onSuccess { pose ->
                    reduce { copy(poseList = (poseList + pose).toImmutableList()) }
                }.onFailure { error ->
                    if (error is RandomPoseRetryExhaustedException)
                        Timber.e(error, "중복 포즈")
                    else Timber.e(error)
                }
            }
        }
    }

    private fun fetchInitialData(
        state: RandomPoseUiState,
        reduce: (RandomPoseUiState.() -> RandomPoseUiState) -> Unit,
        postSideEffect: (RandomPoseEffect) -> Unit,
    ) {
        checkFirstVisit(reduce)
        fetchInitialPoses(state, reduce, postSideEffect)
    }

    private fun checkFirstVisit(
        reduce: (RandomPoseUiState.() -> RandomPoseUiState) -> Unit,
    ) {
        viewModelScope.launch {
            if (userRepository.hasVisitedRandomPose.first()) {
                userRepository.markRandomPoseAsVisited()
            } else {
                reduce { copy(isShowTutorial = false) }
            }
        }
    }

    private fun fetchInitialPoses(
        state: RandomPoseUiState,
        reduce: (RandomPoseUiState.() -> RandomPoseUiState) -> Unit,
        postSideEffect: (RandomPoseEffect) -> Unit,
    ) {
        if (state.poseList.isNotEmpty()) return

        viewModelScope.launch {
            reduce { copy(isLoading = true) }

            // 초기에 INITIAL_POSE_LOAD_COUNT개 로드
            poseRepository.getMultipleRandomPose(
                headCount = peopleCount,
                excludeIds = emptySet(),
                poseSize = PoseConst.INITIAL_POSE_LOAD_COUNT,
                maxRetry = PoseConst.MAXIMUM_RANDOM_POSE_RETRY_COUNT,
            ).onSuccess { data ->
                reduce {
                    copy(
                        isLoading = false,
                        poseList = data.toImmutableList(),
                        committedScraps = data.associate { it.id to it.isScrapped },
                        currentIndex = 0,
                    )
                }
            }.onFailure { error ->
                reduce { copy(isLoading = false) }
                if (error is RandomPoseRetryExhaustedException)
                    Timber.e(error, "중복 포즈")
                else Timber.e(error)
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

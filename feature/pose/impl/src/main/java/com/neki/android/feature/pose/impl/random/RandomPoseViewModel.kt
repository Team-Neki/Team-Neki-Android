package com.neki.android.feature.pose.impl.random

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.common.coroutine.di.ApplicationScope
import com.neki.android.core.common.exception.ApiErrorCode.NO_MORE_RANDOM_POSE
import com.neki.android.core.common.exception.NoMorePoseException
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

    private val bookmarkJobs = mutableMapOf<Long, Job>()

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

            RandomPoseIntent.ClickBookmarkIcon -> {
                val currentPost = state.currentPose ?: return
                handleBookmarkToggle(currentPost.id, !currentPost.isBookmarked, reduce)
            }

            is RandomPoseIntent.BookmarkChanged -> handleBookmarkToggle(intent.poseId, intent.isBookmarked, reduce)
        }
    }

    private fun handleBookmarkToggle(
        poseId: Long,
        newBookmarkStatus: Boolean,
        reduce: (RandomPoseUiState.() -> RandomPoseUiState) -> Unit,
    ) {
        // UI 즉시 업데이트
        reduce {
            copy(
                poseList = poseList.map { pose ->
                    if (pose.id == poseId) {
                        pose.copy(isBookmarked = newBookmarkStatus)
                    } else {
                        pose
                    }
                }.toImmutableList(),
            )
        }

        // 해당 포즈의 이전 Job 취소 후 새로운 Job 시작
        bookmarkJobs[poseId]?.cancel()
        bookmarkJobs[poseId] = viewModelScope.launch {
            delay(500)
            val committedBookmark = store.uiState.value.committedBookmarks[poseId]
            if (committedBookmark == newBookmarkStatus || committedBookmark == null) return@launch

            poseRepository.updateBookmark(poseId, newBookmarkStatus)
                .onSuccess {
                    Timber.d("updateBookmark success for poseId: $poseId")
                    reduce {
                        copy(committedBookmarks = committedBookmarks + (poseId to newBookmarkStatus))
                    }
                }
                .onFailure { error ->
                    Timber.e(error, "updateBookmark failed for poseId: $poseId")
                    reduce {
                        copy(
                            poseList = poseList.map { pose ->
                                if (pose.id == poseId) {
                                    pose.copy(isBookmarked = committedBookmark)
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
        if (state.poseList.lastIndex - nextIndex < PoseConst.POSE_PREFETCH_THRESHOLD && state.hasNewPose) {
            viewModelScope.launch {
                poseRepository.getSingleRandomPose(
                    headCount = peopleCount,
                    excludeIds = state.randomPoseIds,
                ).onSuccess { pose ->
                    reduce {
                        copy(
                            poseList = (poseList + pose).toImmutableList(),
                            committedBookmarks = committedBookmarks + (pose.id to pose.isBookmarked),
                        )
                    }
                }.onFailure { error ->
                    if (error is NoMorePoseException && error.code == NO_MORE_RANDOM_POSE) {
                        reduce { copy(hasNewPose = false) }
                        postSideEffect(RandomPoseEffect.ShowToast("모든 포즈를 불러왔어요"))
                    } else Timber.e(error)
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
            if (!userRepository.hasVisitedRandomPose.first()) {
                userRepository.setRandomPoseVisited()
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
            ).onSuccess { data ->
                reduce {
                    copy(
                        isLoading = false,
                        poseList = data.toImmutableList(),
                        committedBookmarks = data.associate { it.id to it.isBookmarked },
                        currentIndex = 0,
                    )
                }
            }.onFailure { error ->
                reduce { copy(isLoading = false) }
                postSideEffect(RandomPoseEffect.ShowToast("포즈를 불러오는데 실패했어요"))
                Timber.e(error)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        val state = store.uiState.value
        state.poseList.forEach { pose ->
            val currentBookmark = pose.isBookmarked
            val committedBookmark = state.committedBookmarks[pose.id]

            if (committedBookmark != null && currentBookmark != committedBookmark) {
                applicationScope.launch {
                    poseRepository.updateBookmark(pose.id, currentBookmark)
                }
            }
        }
    }
}

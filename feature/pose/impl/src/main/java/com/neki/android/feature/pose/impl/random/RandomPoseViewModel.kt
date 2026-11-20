package com.neki.android.feature.pose.impl.random

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.analytics.event.PoseAnalyticsEvent
import com.neki.android.core.analytics.logger.AnalyticsLogger
import com.neki.android.core.common.exception.ApiErrorCode.NO_MORE_RANDOM_POSE
import com.neki.android.core.common.exception.NoMorePoseException
import com.neki.android.core.dataapi.repository.PendingUpdate
import com.neki.android.core.dataapi.repository.committedBoolean
import com.neki.android.core.dataapi.repository.OptimisticUpdateScheduler
import com.neki.android.core.dataapi.repository.PoseRepository
import com.neki.android.core.dataapi.repository.UpdateKind
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = RandomPoseViewModel.Factory::class)
internal class RandomPoseViewModel @AssistedInject constructor(
    @Assisted private val peopleCount: PeopleCount,
    private val poseRepository: PoseRepository,
    private val userRepository: UserRepository,
    private val analyticsLogger: AnalyticsLogger,
    private val optimisticUpdateScheduler: OptimisticUpdateScheduler,
) : ViewModel() {

    private var totalSwipeCount = 0

    @AssistedFactory
    interface Factory {
        fun create(peopleCount: PeopleCount): RandomPoseViewModel
    }

    val store: MviIntentStore<RandomPoseState, RandomPoseIntent, RandomPoseEffect> =
        mviIntentStore(
            initialState = RandomPoseState(),
            onIntent = ::onIntent,
        )

    init {
        store.onIntent(RandomPoseIntent.EnterRandomPoseScreen)
    }

    private fun onIntent(
        intent: RandomPoseIntent,
        state: RandomPoseState,
        reduce: (RandomPoseState.() -> RandomPoseState) -> Unit,
        postSideEffect: (RandomPoseEffect) -> Unit,
    ) {
        when (intent) {
            RandomPoseIntent.EnterRandomPoseScreen -> {
                analyticsLogger.log(PoseAnalyticsEvent.PoseRandomStart)
                fetchInitialData(state, reduce, postSideEffect)
            }

            // 튜토리얼
            RandomPoseIntent.ClickLeftSwipe -> {
                if (state.currentPage > 0) {
                    postSideEffect(RandomPoseEffect.AnimateToPage(state.currentPage - 1))
                }
            }

            RandomPoseIntent.ClickRightSwipe -> {
                postSideEffect(RandomPoseEffect.AnimateToPage(state.currentPage + 1))
            }

            is RandomPoseIntent.PageChanged -> {
                if (intent.page != state.currentPage) totalSwipeCount++
                reduce { copy(currentPage = intent.page) }
                prefetchIfNeeded(reduce)
            }

            RandomPoseIntent.ClickStartRandomPose -> reduce { copy(isShowTutorial = false) }

            // 기본화면
            RandomPoseIntent.ClickCloseIcon -> postSideEffect(RandomPoseEffect.NavigateBack)

            RandomPoseIntent.ClickBookmarkIcon -> {
                analyticsLogger.log(PoseAnalyticsEvent.PoseBookmark)
                val currentPost = state.currentPose ?: return
                handleBookmarkToggle(currentPost.id, !currentPost.isBookmarked, reduce)
            }

            is RandomPoseIntent.BookmarkChanged -> handleBookmarkToggle(intent.poseId, intent.isBookmarked, reduce)
        }
    }

    private fun handleBookmarkToggle(
        poseId: Long,
        newBookmarkStatus: Boolean,
        reduce: (RandomPoseState.() -> RandomPoseState) -> Unit,
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

        // Persist each desired value immediately; WorkManager replaces pending work per pose.
        viewModelScope.launch {
            val committedBookmark = store.uiState.value.committedBookmarks[poseId]
            if (committedBookmark == null) return@launch

            optimisticUpdateScheduler.update(
                PendingUpdate(
                    UpdateKind.POSE_BOOKMARK,
                    poseId.toString(),
                    newBookmarkStatus.toString(),
                    previousValue = committedBookmark.toString(),
                ),
            )
                .onSuccess {
                    Timber.d("updateBookmark success for poseId: $poseId")
                    reduce {
                        copy(committedBookmarks = committedBookmarks + (poseId to newBookmarkStatus))
                    }
                }
                .onFailure { e ->
                    Timber.e(e, "updateBookmark failed for poseId: $poseId")
                    reduce {
                        copy(
                            poseList = poseList.map { pose ->
                                if (pose.id == poseId) {
                                    pose.copy(isBookmarked = e.committedBoolean(committedBookmark))
                                } else {
                                    pose
                                }
                            }.toImmutableList(),
                        )
                    }
                }
        }
    }

    private fun prefetchIfNeeded(
        reduce: (RandomPoseState.() -> RandomPoseState) -> Unit,
    ) {
        val state = store.uiState.value
        if (state.poseList.isEmpty()) return
        if (state.currentIndex >= state.poseList.size - PoseConst.POSE_PREFETCH_THRESHOLD && state.hasNewPose) {
            viewModelScope.launch {
                poseRepository.getSingleRandomPose(
                    headCount = peopleCount,
                    excludeIds = store.uiState.value.randomPoseIds,
                ).onSuccess { pose ->
                    reduce {
                        copy(
                            poseList = (poseList + pose).toImmutableList(),
                            committedBookmarks = committedBookmarks + (pose.id to pose.isBookmarked),
                        )
                    }
                }.onFailure { e ->
                    if (e is NoMorePoseException && e.code == NO_MORE_RANDOM_POSE) {
                        reduce { copy(hasNewPose = false) }
                    } else Timber.e(e)
                }
            }
        }
    }

    private fun fetchInitialData(
        state: RandomPoseState,
        reduce: (RandomPoseState.() -> RandomPoseState) -> Unit,
        postSideEffect: (RandomPoseEffect) -> Unit,
    ) {
        checkFirstVisit(reduce)
        fetchInitialPoses(state, reduce, postSideEffect)
    }

    private fun checkFirstVisit(
        reduce: (RandomPoseState.() -> RandomPoseState) -> Unit,
    ) {
        viewModelScope.launch {
            if (!userRepository.hasVisitedRandomPose.first()) {
                reduce { copy(isShowTutorial = true) }
                userRepository.setRandomPoseVisited()
            }
        }
    }

    private fun fetchInitialPoses(
        state: RandomPoseState,
        reduce: (RandomPoseState.() -> RandomPoseState) -> Unit,
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
                        currentPage = 0,
                    )
                }
            }.onFailure { e ->
                reduce { copy(isLoading = false) }
                postSideEffect(RandomPoseEffect.ShowToast("포즈를 불러오는데 실패했어요"))
                Timber.e(e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        analyticsLogger.log(PoseAnalyticsEvent.PoseRandomSessionEnd(totalSwipeCount = totalSwipeCount))
    }
}

package com.neki.android.feature.pose.impl.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.analytics.event.PoseAnalyticsEvent
import com.neki.android.core.analytics.logger.AnalyticsLogger
import com.neki.android.core.dataapi.repository.PendingUpdate
import com.neki.android.core.dataapi.repository.committedBoolean
import com.neki.android.core.dataapi.repository.OptimisticUpdateScheduler
import com.neki.android.core.dataapi.repository.PoseRepository
import com.neki.android.core.dataapi.repository.UpdateKind
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.pose.api.PoseNavKey
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = PoseDetailViewModel.Factory::class)
class PoseDetailViewModel @AssistedInject constructor(
    @param:Assisted private val key: PoseNavKey.PoseDetail,
    private val poseRepository: PoseRepository,
    private val analyticsLogger: AnalyticsLogger,
    private val optimisticUpdateScheduler: OptimisticUpdateScheduler,
) : ViewModel() {

    private val committedBookmarks = key.poses.associate { it.id to it.isBookmarked }.toMutableMap()
    private var hasNext: Boolean = key.hasNext
    private var nextPage: Int = key.poses.size / PAGE_SIZE
    private var isLoadingMore: Boolean = false

    @AssistedFactory
    interface Factory {
        fun create(key: PoseNavKey.PoseDetail): PoseDetailViewModel
    }

    val store: MviIntentStore<PoseDetailState, PoseDetailIntent, PoseDetailSideEffect> =
        mviIntentStore(
            initialState = PoseDetailState(
                poses = key.poses,
                currentPage = key.initialIndex,
            ),
            onIntent = ::onIntent,
        )

    init {
        store.onIntent(PoseDetailIntent.EnterPoseDetailScreen)
    }

    private fun onIntent(
        intent: PoseDetailIntent,
        state: PoseDetailState,
        reduce: (PoseDetailState.() -> PoseDetailState) -> Unit,
        postSideEffect: (PoseDetailSideEffect) -> Unit,
    ) {
        when (intent) {
            PoseDetailIntent.EnterPoseDetailScreen -> {
                if (state.poses.isEmpty()) {
                    fetchPoseData(reduce)
                }
            }
            PoseDetailIntent.ClickBackIcon -> postSideEffect(PoseDetailSideEffect.NavigateBack)
            is PoseDetailIntent.PageChanged -> {
                reduce { copy(currentPage = intent.page) }
                preloadIfNeeded(reduce)
            }
            PoseDetailIntent.ClickBookmarkIcon -> handleBookmarkToggle(state, reduce, postSideEffect)
            is PoseDetailIntent.BookmarkCommitted -> {
                committedBookmarks[intent.poseId] = intent.newBookmark
            }
            is PoseDetailIntent.RevertBookmark -> {
                reduce {
                    copy(
                        poses = poses.map { pose ->
                            if (pose.id == intent.poseId) pose.copy(isBookmarked = intent.originalBookmark) else pose
                        },
                    )
                }
                postSideEffect(PoseDetailSideEffect.NotifyBookmarkChanged(intent.poseId, intent.originalBookmark))
                postSideEffect(PoseDetailSideEffect.ShowToast("북마크 변경에 실패했어요"))
            }
        }
    }

    private fun handleBookmarkToggle(
        state: PoseDetailState,
        reduce: (PoseDetailState.() -> PoseDetailState) -> Unit,
        postSideEffect: (PoseDetailSideEffect) -> Unit,
    ) {
        analyticsLogger.log(PoseAnalyticsEvent.PoseBookmark)
        val currentPose = state.pose
        if (currentPose.id == 0L) return

        val newBookmarkStatus = !currentPose.isBookmarked
        viewModelScope.launch {
            optimisticUpdateScheduler.update(
                PendingUpdate(
                    UpdateKind.POSE_BOOKMARK,
                    currentPose.id.toString(),
                    newBookmarkStatus.toString(),
                    500,
                    committedBookmarks[currentPose.id]?.toString(),
                ),
            )
                .onSuccess { store.onIntent(PoseDetailIntent.BookmarkCommitted(currentPose.id, newBookmarkStatus)) }
                .onFailure { e ->
                    val original = committedBookmarks[currentPose.id] ?: return@onFailure
                    store.onIntent(PoseDetailIntent.RevertBookmark(currentPose.id, e.committedBoolean(original)))
                }
        }
        reduce {
            copy(
                poses = poses.map { pose ->
                    if (pose.id == currentPose.id) pose.copy(isBookmarked = newBookmarkStatus) else pose
                },
            )
        }
        postSideEffect(PoseDetailSideEffect.NotifyBookmarkChanged(currentPose.id, newBookmarkStatus))
    }

    private fun fetchPoseData(reduce: (PoseDetailState.() -> PoseDetailState) -> Unit) {
        viewModelScope.launch {
            poseRepository.getPose(poseId = key.poseId)
                .onSuccess { data ->
                    committedBookmarks[data.id] = data.isBookmarked
                    reduce {
                        copy(
                            poses = listOf(data),
                            currentPage = 0,
                        )
                    }
                }
                .onFailure { e ->
                    Timber.e(e)
                }
        }
    }

    private fun preloadIfNeeded(
        reduce: (PoseDetailState.() -> PoseDetailState) -> Unit,
    ) {
        val latestState = store.uiState.value
        if (latestState.currentIndex >= latestState.poses.size - PRELOAD_THRESHOLD && hasNext && !isLoadingMore) {
            loadMorePoses(reduce)
        }
    }

    private fun loadMorePoses(
        reduce: (PoseDetailState.() -> PoseDetailState) -> Unit,
    ) {
        isLoadingMore = true
        viewModelScope.launch {
            try {
                val result = if (key.isBookmarkOnly) {
                    poseRepository.getBookmarkedPosesPage(
                        page = nextPage,
                        size = PAGE_SIZE,
                        sortOrder = key.sortOrder,
                    )
                } else {
                    poseRepository.getPosesPage(
                        headCount = key.headCount,
                        page = nextPage,
                        size = PAGE_SIZE,
                        sortOrder = key.sortOrder,
                    )
                }

                result
                    .onSuccess { page ->
                        reduce {
                            copy(
                                poses = poses + page.poses,
                            )
                        }
                        hasNext = page.hasNext
                        nextPage++
                        page.poses.forEach { committedBookmarks[it.id] = it.isBookmarked }
                    }
                    .onFailure { e ->
                        Timber.e(e, "loadMorePoses failed")
                    }
            } finally {
                isLoadingMore = false
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
        private const val PRELOAD_THRESHOLD = 5
    }
}

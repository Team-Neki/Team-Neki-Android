package com.neki.android.feature.pose.impl.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.analytics.event.PoseAnalyticsEvent
import com.neki.android.core.analytics.logger.AnalyticsLogger
import com.neki.android.core.common.coroutine.di.ApplicationScope
import com.neki.android.core.dataapi.repository.PoseRepository
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.pose.api.PoseNavKey
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(FlowPreview::class)
@HiltViewModel(assistedFactory = PoseDetailViewModel.Factory::class)
class PoseDetailViewModel @AssistedInject constructor(
    @param:Assisted private val key: PoseNavKey.PoseDetail,
    private val poseRepository: PoseRepository,
    @param:ApplicationScope private val applicationScope: CoroutineScope,
    private val analyticsLogger: AnalyticsLogger,
) : ViewModel() {

    private val bookmarkRequests = MutableSharedFlow<Pair<Long, Boolean>>(extraBufferCapacity = 64)
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
        viewModelScope.launch {
            bookmarkRequests
                .debounce(500)
                .collect { (poseId, newBookmark) ->
                    val committedBookmark = committedBookmarks[poseId] ?: return@collect
                    if (committedBookmark != newBookmark) {
                        poseRepository.updateBookmark(poseId, newBookmark)
                            .onSuccess {
                                Timber.d("updateBookmark success")
                                store.onIntent(PoseDetailIntent.BookmarkCommitted(poseId, newBookmark))
                            }
                            .onFailure { e ->
                                Timber.e(e, "updateBookmark failed")
                                store.onIntent(PoseDetailIntent.RevertBookmark(poseId, committedBookmark))
                            }
                    }
                }
        }
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
        viewModelScope.launch { bookmarkRequests.emit(currentPose.id to newBookmarkStatus) }
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

    override fun onCleared() {
        super.onCleared()

        val state = store.uiState.value
        state.poses.forEach { pose ->
            val committedBookmark = committedBookmarks[pose.id]
            if (committedBookmark != null && pose.isBookmarked != committedBookmark) {
                applicationScope.launch {
                    poseRepository.updateBookmark(pose.id, pose.isBookmarked)
                }
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
        private const val PRELOAD_THRESHOLD = 5
    }
}

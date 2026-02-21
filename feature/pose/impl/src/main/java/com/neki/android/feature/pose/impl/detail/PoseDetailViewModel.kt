package com.neki.android.feature.pose.impl.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.common.coroutine.di.ApplicationScope
import com.neki.android.core.dataapi.repository.PoseRepository
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
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
    @Assisted private val id: Long,
    private val poseRepository: PoseRepository,
    @ApplicationScope private val applicationScope: CoroutineScope,
) : ViewModel() {

    private val bookmarkRequests = MutableSharedFlow<Boolean>(extraBufferCapacity = 64)

    @AssistedFactory
    interface Factory {
        fun create(id: Long): PoseDetailViewModel
    }

    val store: MviIntentStore<PoseDetailState, PoseDetailIntent, PoseDetailSideEffect> =
        mviIntentStore(
            initialState = PoseDetailState(),
            onIntent = ::onIntent,
            initialFetchData = { store.onIntent(PoseDetailIntent.EnterPoseDetailScreen) },
        )

    init {
        viewModelScope.launch {
            bookmarkRequests
                .debounce(500)
                .collect { newBookmark ->
                    val committedBookmark = store.uiState.value.committedBookmark
                    if (committedBookmark != newBookmark) {
                        poseRepository.updateBookmark(id, newBookmark)
                            .onSuccess {
                                Timber.d("updateBookmark success")
                                store.onIntent(PoseDetailIntent.BookmarkCommitted(newBookmark))
                            }
                            .onFailure { e ->
                                Timber.e(e, "updateBookmark failed")
                                store.onIntent(PoseDetailIntent.RevertBookmark(committedBookmark))
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
            PoseDetailIntent.EnterPoseDetailScreen -> fetchPoseData(reduce)
            PoseDetailIntent.ClickBackIcon -> postSideEffect(PoseDetailSideEffect.NavigateBack)
            PoseDetailIntent.ClickBookmarkIcon -> handleBookmarkToggle(state, reduce)
            is PoseDetailIntent.BookmarkCommitted -> {
                reduce { copy(committedBookmark = intent.newBookmark) }
                postSideEffect(PoseDetailSideEffect.NotifyBookmarkChanged(id, intent.newBookmark))
            }
            is PoseDetailIntent.RevertBookmark -> reduce { copy(pose = pose.copy(isBookmarked = intent.originalBookmark)) }
        }
    }

    private fun handleBookmarkToggle(
        state: PoseDetailState,
        reduce: (PoseDetailState.() -> PoseDetailState) -> Unit,
    ) {
        val newBookmarkStatus = !state.pose.isBookmarked
        viewModelScope.launch { bookmarkRequests.emit(newBookmarkStatus) }
        reduce { copy(pose = pose.copy(isBookmarked = newBookmarkStatus)) }
    }

    private fun fetchPoseData(reduce: (PoseDetailState.() -> PoseDetailState) -> Unit) {
        viewModelScope.launch {
            poseRepository.getPose(poseId = id)
                .onSuccess { data ->
                    reduce { copy(pose = data, committedBookmark = data.isBookmarked) }
                }
                .onFailure { e ->
                    Timber.e(e)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()

        val currentBookmark = store.uiState.value.pose.isBookmarked
        val committedBookmark = store.uiState.value.committedBookmark

        if (currentBookmark != committedBookmark) {
            applicationScope.launch {
                poseRepository.updateBookmark(id, currentBookmark)
            }
        }
    }
}

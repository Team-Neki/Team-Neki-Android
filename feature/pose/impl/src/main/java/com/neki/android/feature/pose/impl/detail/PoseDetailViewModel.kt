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

    private val scrapRequests = MutableSharedFlow<Boolean>(extraBufferCapacity = 64)

    @AssistedFactory
    interface Factory {
        fun create(id: Long): PoseDetailViewModel
    }

    val store: MviIntentStore<PoseDetailState, PoseDetailIntent, PoseDetailSideEffect> =
        mviIntentStore(
            initialState = PoseDetailState(),
            onIntent = ::onIntent,
        )

    init {
        viewModelScope.launch {
            scrapRequests
                .debounce(500)
                .collect { newScrap ->
                    val committedScrap = store.uiState.value.committedScrap
                    if (committedScrap != newScrap) {
                        poseRepository.updateScrap(id, newScrap)
                            .onSuccess {
                                Timber.d("updateScrap success")
                                store.onIntent(PoseDetailIntent.ScrapCommitted(newScrap))
                            }
                            .onFailure { error ->
                                Timber.e(error, "updateScrap failed")
                                store.onIntent(PoseDetailIntent.RevertScrap(committedScrap))
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
            PoseDetailIntent.ClickScrapIcon -> handleScrapToggle(state, reduce)
            is PoseDetailIntent.ScrapCommitted -> reduce { copy(committedScrap = intent.newScrap) }
            is PoseDetailIntent.RevertScrap -> reduce { copy(pose = pose.copy(isScrapped = intent.originalScrap)) }
        }
    }

    private fun handleScrapToggle(
        state: PoseDetailState,
        reduce: (PoseDetailState.() -> PoseDetailState) -> Unit,
    ) {
        val newScrapStatus = !state.pose.isScrapped
        viewModelScope.launch { scrapRequests.emit(newScrapStatus) }
        reduce { copy(pose = pose.copy(isScrapped = newScrapStatus)) }
    }

    private fun fetchPoseData(reduce: (PoseDetailState.() -> PoseDetailState) -> Unit) {
        viewModelScope.launch {
            poseRepository.getPose(poseId = id)
                .onSuccess { data ->
                    reduce { copy(pose = data, committedScrap = data.isScrapped) }
                }
                .onFailure { error ->
                    Timber.e(error)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()

        val currentScrap = store.uiState.value.pose.isScrapped
        val committedScrap = store.uiState.value.committedScrap

        if (currentScrap != committedScrap) {
            applicationScope.launch {
                poseRepository.updateScrap(id, currentScrap)
            }
        }
    }
}

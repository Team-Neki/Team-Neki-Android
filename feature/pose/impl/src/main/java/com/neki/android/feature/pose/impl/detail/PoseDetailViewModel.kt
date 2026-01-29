package com.neki.android.feature.pose.impl.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.PoseRepository
import com.neki.android.core.model.Pose
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = PoseDetailViewModel.Factory::class)
class PoseDetailViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    private val poseRepository: PoseRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(id: Long): PoseDetailViewModel
    }

    val store: MviIntentStore<PoseDetailState, PoseDetailIntent, PoseDetailSideEffect> =
        mviIntentStore(
            initialState = PoseDetailState(),
            onIntent = ::onIntent,
        )

    private fun onIntent(
        intent: PoseDetailIntent,
        state: PoseDetailState,
        reduce: (PoseDetailState.() -> PoseDetailState) -> Unit,
        postSideEffect: (PoseDetailSideEffect) -> Unit,
    ) {
        when (intent) {
            PoseDetailIntent.EnterPoseDetailScreen -> fetchPoseData(reduce)
            PoseDetailIntent.ClickBackIcon -> postSideEffect(PoseDetailSideEffect.NavigateBack)
            PoseDetailIntent.ClickScrapIcon -> {
                // TODO: API 연동 시 실제 스크랩 토글 구현
                reduce {
                    copy(pose = state.pose.copy(isScrapped = !state.pose.isScrapped))
                }
            }
        }
    }

    private fun fetchPoseData(reduce: (PoseDetailState.() -> PoseDetailState) -> Unit) {
        viewModelScope.launch {
            poseRepository.getPose(poseId = id)
                .onSuccess { data ->
                    reduce { copy(pose = data) }
                }
                .onFailure { error ->
                    Timber.e(error)
                }
        }
    }
}

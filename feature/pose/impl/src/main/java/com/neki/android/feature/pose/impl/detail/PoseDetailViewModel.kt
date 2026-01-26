package com.neki.android.feature.pose.impl.detail

import androidx.lifecycle.ViewModel
import com.neki.android.core.model.Pose
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = PoseDetailViewModel.Factory::class)
class PoseDetailViewModel @AssistedInject constructor(
    @Assisted private val pose: Pose,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(pose: Pose): PoseDetailViewModel
    }

    val store: MviIntentStore<PoseDetailState, PoseDetailIntent, PoseDetailSideEffect> =
        mviIntentStore(
            initialState = PoseDetailState(pose = pose),
            onIntent = ::onIntent,
        )

    private fun onIntent(
        intent: PoseDetailIntent,
        state: PoseDetailState,
        reduce: (PoseDetailState.() -> PoseDetailState) -> Unit,
        postSideEffect: (PoseDetailSideEffect) -> Unit,
    ) {
        when (intent) {
            PoseDetailIntent.ClickBackIcon -> postSideEffect(PoseDetailSideEffect.NavigateBack)
            PoseDetailIntent.ClickScrapIcon -> {
                // TODO: API 연동 시 실제 스크랩 토글 구현
                reduce {
                    copy(pose = state.pose.copy(isScrapped = !state.pose.isScrapped))
                }
            }
        }
    }
}

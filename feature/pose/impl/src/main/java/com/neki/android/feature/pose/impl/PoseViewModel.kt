package com.neki.android.feature.pose.impl

import androidx.lifecycle.ViewModel
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PoseViewModel @Inject constructor() : ViewModel() {

    val store: MviIntentStore<PoseState, PoseIntent, PoseEffect> =
        mviIntentStore(
            initialState = PoseState(),
            onIntent = ::onIntent,
        )

    private fun onIntent(
        intent: PoseIntent,
        state: PoseState,
        reduce: (PoseState.() -> PoseState) -> Unit,
        poseSideEffect: (PoseEffect) -> Unit,
    ) {
        when(intent) {
            PoseIntent.ClickAlarmIcon -> TODO()
            PoseIntent.ClickBackIcon -> TODO()
            PoseIntent.ClickNumberOfPeople -> TODO()
            is PoseIntent.ClickNumberOfPeopleSheetItem -> TODO()
            is PoseIntent.ClickPoseItem -> TODO()
            PoseIntent.ClickRandomPoseRecommendation -> TODO()
            PoseIntent.ClickScrap -> TODO()
            PoseIntent.ClickScrapIcon -> TODO()
        }
    }
}

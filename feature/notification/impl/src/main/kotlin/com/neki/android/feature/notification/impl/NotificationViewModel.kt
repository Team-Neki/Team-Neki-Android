package com.neki.android.feature.notification.impl

import androidx.lifecycle.ViewModel
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class NotificationViewModel @Inject constructor() : ViewModel() {

    val store: MviIntentStore<NotificationState, NotificationIntent, NotificationEffect> =
        mviIntentStore(
            initialState = NotificationState(),
            onIntent = ::onIntent,
        )

    private fun onIntent(
        intent: NotificationIntent,
        state: NotificationState,
        reduce: (NotificationState.() -> NotificationState) -> Unit,
        postSideEffect: (NotificationEffect) -> Unit,
    ) {
        when (intent) {
            NotificationIntent.ClickBack -> postSideEffect(NotificationEffect.NavigateBack)
            is NotificationIntent.ClickNotification -> Unit
        }
    }
}

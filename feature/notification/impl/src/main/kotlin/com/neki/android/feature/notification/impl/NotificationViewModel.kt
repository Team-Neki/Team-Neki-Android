package com.neki.android.feature.notification.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.NotificationRepository
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    val store: MviIntentStore<NotificationState, NotificationIntent, NotificationEffect> =
        mviIntentStore(
            initialState = NotificationState(),
            onIntent = ::onIntent,
        )

    init {
        store.onIntent(NotificationIntent.EnterNotificationScreen)
    }

    private fun onIntent(
        intent: NotificationIntent,
        state: NotificationState,
        reduce: (NotificationState.() -> NotificationState) -> Unit,
        postSideEffect: (NotificationEffect) -> Unit,
    ) {
        when (intent) {
            NotificationIntent.EnterNotificationScreen -> fetchRecentNotifications(reduce)
            NotificationIntent.ClickBack -> postSideEffect(NotificationEffect.NavigateBack)
            is NotificationIntent.ClickNotification -> Unit
        }
    }

    private fun fetchRecentNotifications(
        reduce: (NotificationState.() -> NotificationState) -> Unit,
    ) = viewModelScope.launch {
        notificationRepository.getRecentNotifications()
            .onSuccess { notifications ->
                reduce {
                    copy(
                        isLoading = false,
                        notifications = notifications.toImmutableList(),
                    )
                }
            }
            .onFailure { e ->
                Timber.e(e)
                reduce { copy(isLoading = false) }
            }
    }
}

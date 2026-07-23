package com.neki.android.feature.notification.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.notification.impl.component.NotificationEmptyContent
import com.neki.android.feature.notification.impl.component.NotificationListItem
import com.neki.android.feature.notification.impl.component.NotificationTopBar

@Composable
internal fun NotificationRoute(
    viewModel: NotificationViewModel = hiltViewModel(),
    navigateBack: () -> Unit = {},
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()

    viewModel.store.sideEffects.collectWithLifecycle { effect ->
        when (effect) {
            NotificationEffect.NavigateBack -> navigateBack()
        }
    }

    NotificationScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
internal fun NotificationScreen(
    uiState: NotificationState,
    onIntent: (NotificationIntent) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        NotificationTopBar(
            onClickBack = { onIntent(NotificationIntent.ClickBack) },
        )

        if (uiState.notifications.isEmpty()) {
            NotificationEmptyContent(modifier = Modifier.weight(1f))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = "새로운 알림",
                    style = NekiTheme.typography.body14SemiBold,
                    color = NekiTheme.colorScheme.gray700,
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(uiState.notifications) { notification ->
                        NotificationListItem(
                            notification = notification,
                            onClick = { onIntent(NotificationIntent.ClickNotification(notification.id)) },
                        )
                    }
                }
            }
        }
    }
}

@ComponentPreview
@Composable
private fun NotificationEmptyScreenPreview() {
    NekiTheme {
        NotificationScreen(NotificationState())
    }
}

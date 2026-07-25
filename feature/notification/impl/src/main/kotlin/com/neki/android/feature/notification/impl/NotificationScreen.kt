package com.neki.android.feature.notification.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.neki.android.core.model.Notification
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.notification.impl.component.NotificationEmptyContent
import com.neki.android.feature.notification.impl.component.NotificationListItem
import com.neki.android.feature.notification.impl.component.NotificationTopBar
import kotlinx.collections.immutable.persistentListOf

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

        if (uiState.isLoading) {
            LoadingDialog()
        } else if (uiState.notifications.isEmpty()) {
            NotificationEmptyContent(modifier = Modifier.weight(1f))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "새로운 알림",
                    style = NekiTheme.typography.body14SemiBold,
                    color = NekiTheme.colorScheme.gray700,
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(32.dp),
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
        NotificationScreen(NotificationState(isLoading = false))
    }
}

@ComponentPreview
@Composable
private fun NotificationScreenPreview() {
    NekiTheme {
        NotificationScreen(
            NotificationState(
                isLoading = false,
                notifications = persistentListOf(
                    Notification(
                        id = 1L,
                        type = "ARCHIVE",
                        title = "이번 주말엔 어디서 찍을까요?",
                        body = "약속 전에 근처 포토부스를 미리 찾아보세요.",
                        link = "neki://archive/123",
                        createdAt = "1시간 전",
                    ),
                    Notification(
                        id = 2L,
                        type = "POSE",
                        title = "새로운 포즈를 추천해 드릴게요.새로운 포즈를 추천해 드릴게요.새로운 포즈를 추천해 드릴게요.",
                        body = "오늘의 포즈를 확인해 보세요.오늘의 포즈를 확인해 보세요.오늘의 포즈를 확인해 보세요.오늘의 포즈를 확인해 보세요.",
                        link = "neki://pose/456",
                        createdAt = "어제",
                    ),
                    Notification(
                        id = 3L,
                        type = "ARCHIVE",
                        title = "내 주변 포토부스를 찾아봤어요",
                        body = "지금 위치에서 가까운 포토부스를 확인해 보세요.지금 위치에서 가까운 포토부스를 확인해 보세요.지금 위치에서 가까운 포토부스를 확인해 보세요.지금 위치에서 가까운 포토부스를 확인해 보세요.지금 위치에서 가까운 포토부스를 확인해 보세요.",
                        link = "neki://archive/789",
                        createdAt = "어제",
                    ),
                    Notification(
                        id = 4L,
                        type = "POSE",
                        title = "친구와 함께 찍기 좋은 포즈",
                        body = "둘이서 남기기 좋은 포즈를 준비했어요.",
                        link = "neki://pose/101",
                        createdAt = "2일 전",
                    ),
                    Notification(
                        id = 5L,
                        type = "ARCHIVE",
                        title = "새로운 포토부스가 등록됐어요",
                        body = "이번 주말에 방문해 보세요.",
                        link = "neki://archive/102",
                        createdAt = "3일 전",
                    ),
                    Notification(
                        id = 6L,
                        type = "POSE",
                        title = "이번 주 인기 포즈를 확인해 보세요",
                        body = "많은 네키 유저가 저장한 포즈예요.",
                        link = "neki://pose/103",
                        createdAt = "4일 전",
                    ),
                    Notification(
                        id = 7L,
                        type = "ARCHIVE",
                        title = "사진을 남길 새로운 장소를 발견했어요",
                        body = "마음에 드는 포토부스를 저장해 보세요.",
                        link = "neki://archive/104",
                        createdAt = "5일 전",
                    ),
                ),
            ),
        )
    }
}

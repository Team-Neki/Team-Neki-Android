package com.neki.android.feature.notification.impl

import com.neki.android.core.model.Notification
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

data class NotificationState(
    // 더미데이터 30개, API 연동하며 제거할 예정
    val notifications: ImmutableList<Notification> = List(30) { index ->
        val notificationNumber = index + 1
        Notification(
            id = notificationNumber.toLong(),
            type = "ARCHIVE",
            title = if (notificationNumber % 3 == 0) "이번 주말 어디서 찍을까요?" else "일주일 전 사진이 있어요",
            body = if (notificationNumber % 3 == 0) {
                "약속 전에 근처 포토부스를 미리 찾아보세요."
            } else {
                "네키에 저장한 네컷을 다시 확인해보세요."
            },
            link = "neki://archive/$notificationNumber",
            createdAt = "${notificationNumber}시간 전",
        )
    }.toPersistentList(),
)

sealed interface NotificationIntent {
    data object ClickBack : NotificationIntent
    data class ClickNotification(val id: Long) : NotificationIntent
}

sealed interface NotificationEffect {
    data object NavigateBack : NotificationEffect
}

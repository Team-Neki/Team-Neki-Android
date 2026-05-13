package com.neki.android.core.analytics.event

sealed interface PoseAnalyticsEvent : AnalyticsEvent {

    data object PoseView : PoseAnalyticsEvent {
        override val name = "pose_view"
    }

    data object PoseRandomStart : PoseAnalyticsEvent {
        override val name = "pose_random_start"
    }

    data class PoseRandomSessionEnd(val totalSwipeCount: Int) : PoseAnalyticsEvent {
        override val name = "pose_random_session_end"
        override val params = mapOf("total_swipe_count" to totalSwipeCount)
    }

    data class PoseFilterToggle(val peopleCount: Int) : PoseAnalyticsEvent {
        override val name = "pose_filter_toggle"
        override val params = mapOf("people_count" to peopleCount)
    }

    data object PoseBookmarkFilter : PoseAnalyticsEvent {
        override val name = "pose_bookmark_filter"
    }

    data object PoseBookmark : PoseAnalyticsEvent {
        override val name = "pose_bookmark"
    }
}

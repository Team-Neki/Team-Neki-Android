package com.neki.android.core.analytics.event

sealed interface GlobalAnalyticsEvent : AnalyticsEvent {

    data object AppOpen : GlobalAnalyticsEvent {
        override val name = "app_open"
    }
}

package com.neki.android.core.analytics.event

sealed interface AnalyticsEvent {

    val name: String
    val params: Map<String, String>
        get() = emptyMap()
}

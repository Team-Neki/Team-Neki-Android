package com.neki.android.core.analytics.event

sealed interface MapAnalyticsEvent : AnalyticsEvent {

    data object MapView : MapAnalyticsEvent {
        override val name = "map_view"
    }

    data class MapReSearch(val hasFilter: Boolean, val regionChanged: Boolean) : MapAnalyticsEvent {
        override val name = "map_re_search"
        override val params = mapOf(
            "has_filter" to hasFilter.toString(),
            "region_changed" to regionChanged.toString(),
        )
    }

    data class MapBrandFilterToggle(
        val action: String,
        val selectedCount: Int,
        val brandName: String,
    ) : MapAnalyticsEvent {
        override val name = "map_brand_filter_toggle"
        override val params = mapOf(
            "action" to action,
            "selected_count" to selectedCount.toString(),
            "brand_name" to brandName,
        )
    }

    data class BoothSelect(val entryPoint: String, val brandName: String) : MapAnalyticsEvent {
        override val name = "booth_select"
        override val params = mapOf(
            "entry_point" to entryPoint,
            "brand_name" to brandName,
        )
    }

    data class MapRouteClick(val mapType: String) : MapAnalyticsEvent {
        override val name = "map_route_click"
        override val params = mapOf("map_type" to mapType)
    }
}

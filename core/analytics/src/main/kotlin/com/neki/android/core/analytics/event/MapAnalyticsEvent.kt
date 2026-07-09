package com.neki.android.core.analytics.event

sealed interface MapAnalyticsEvent : AnalyticsEvent {

    data object MapView : MapAnalyticsEvent {
        override val name = "map_view"
    }

    data class MapReSearch(val hasFilter: Boolean, val regionChanged: Boolean) : MapAnalyticsEvent {
        override val name = "map_re_search"
        override val params = mapOf(
            "has_filter" to hasFilter,
            "region_changed" to regionChanged,
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
            "selected_count" to selectedCount,
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

    data class BrandOrderSave(
        val priorityBrand1: String,
        val priorityBrand2: String,
        val priorityBrand3: String,
    ) : MapAnalyticsEvent {
        override val name = "brand_order_save"
        override val params = mapOf(
            "priority_brand_1" to priorityBrand1,
            "priority_brand_2" to priorityBrand2,
            "priority_brand_3" to priorityBrand3,
        )
    }

    data class FavoriteBoothView(val favoriteBoothCount: Int) : MapAnalyticsEvent {
        override val name = "favorite_booth_view"
        override val params = mapOf("favorite_booth_count" to favoriteBoothCount)
    }

    data class FavoriteBoothFilterOn(val favoriteBoothCount: Int) : MapAnalyticsEvent {
        override val name = "favorite_booth_filter_on"
        override val params = mapOf("favorite_booth_count" to favoriteBoothCount)
    }

    data object FavoriteBoothFilterOff : MapAnalyticsEvent {
        override val name = "favorite_booth_filter_off"
    }

    data class BoothFavoriteAdd(val boothName: String, val brandName: String) : MapAnalyticsEvent {
        override val name = "booth_favorite_add"
        override val params = mapOf(
            "booth_name" to boothName,
            "brand_name" to brandName,
        )
    }

    data class BoothFavoriteRemove(val boothName: String, val brandName: String) : MapAnalyticsEvent {
        override val name = "booth_favorite_remove"
        override val params = mapOf(
            "booth_name" to boothName,
            "brand_name" to brandName,
        )
    }
}

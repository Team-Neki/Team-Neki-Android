package com.neki.android.core.analytics.event

sealed interface MapAnalyticsEvent : AnalyticsEvent {

    data object MapView : MapAnalyticsEvent {
        override val name = "map_view"
    }

    data object MapSearchView : MapAnalyticsEvent {
        override val name = "map_search_view"
    }

    data class MapSearchResult(val query: String, val resultStatus: String) : MapAnalyticsEvent {
        override val name = "map_search_result"
        override val params = mapOf(
            "query" to query,
            "result_status" to resultStatus,
        )
    }

    data class MapSearchCandidateSelect(
        val query: String,
        val candidateType: String,
        val candidateName: String,
    ) : MapAnalyticsEvent {
        override val name = "map_search_candidate_select"
        override val params = mapOf(
            "query" to query,
            "candidate_type" to candidateType,
            "candidate_name" to candidateName,
        )
    }

    data class MapSearchLoadMore(
        val query: String,
        val candidateType: String,
        val page: Int,
        val pageResultCount: Int,
        val hasNextPage: Boolean,
    ) : MapAnalyticsEvent {
        override val name = "map_search_load_more"
        override val params = mapOf(
            "query" to query,
            "candidate_type" to candidateType,
            "page" to page,
            "page_result_count" to pageResultCount,
            "has_next_page" to hasNextPage,
        )
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
        val brandId: String,
    ) : MapAnalyticsEvent {
        override val name = "map_brand_filter_toggle"
        override val params = mapOf(
            "action" to action,
            "selected_count" to selectedCount,
            "brand_name" to brandName,
            "brand_id" to brandId,
        )
    }

    data class BoothSelect(
        val entryPoint: String,
        val brandName: String,
        val brandId: String?,
        val source: String = "map",
    ) : MapAnalyticsEvent {
        override val name = "booth_select"
        override val params = buildMap {
            put("entry_point", entryPoint)
            put("brand_name", brandName)
            brandId?.let { put("brand_id", it) }
            put("source", source)
        }
    }

    data class MapRouteClick(val mapType: String) : MapAnalyticsEvent {
        override val name = "map_route_click"
        override val params = mapOf("map_type" to mapType)
    }

    data class BrandOrderSave(
        val priorityBrand1: String,
        val priorityBrand2: String,
        val priorityBrand3: String,
        val priorityBrandId1: String,
        val priorityBrandId2: String,
        val priorityBrandId3: String,
    ) : MapAnalyticsEvent {
        override val name = "brand_order_save"
        override val params = mapOf(
            "priority_brand_1" to priorityBrand1,
            "priority_brand_2" to priorityBrand2,
            "priority_brand_3" to priorityBrand3,
            "priority_brand_id_1" to priorityBrandId1,
            "priority_brand_id_2" to priorityBrandId2,
            "priority_brand_id_3" to priorityBrandId3,
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

    data class BoothFavoriteAdd(val boothName: String, val brandName: String, val brandId: String?) : MapAnalyticsEvent {
        override val name = "booth_favorite_add"
        override val params = buildMap {
            put("booth_name", boothName)
            put("brand_name", brandName)
            brandId?.let { put("brand_id", it) }
        }
    }

    data class BoothFavoriteRemove(val boothName: String, val brandName: String, val brandId: String?) : MapAnalyticsEvent {
        override val name = "booth_favorite_remove"
        override val params = buildMap {
            put("booth_name", boothName)
            put("brand_name", brandName)
            brandId?.let { put("brand_id", it) }
        }
    }
}

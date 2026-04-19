package com.neki.android.core.analytics

sealed interface AnalyticsEvent {

    val name: String
    val params: Map<String, String>
        get() = emptyMap()

    data object AppOpen : AnalyticsEvent {
        override val name = "app_open"
    }

    sealed interface Archive : AnalyticsEvent {

        data object ArchivingView : Archive {
            override val name = "archiving_view"
        }

        data class PhotoUpload(val method: String, val count: Int) : Archive {
            override val name = "photo_upload"
            override val params = mapOf(
                "method" to method,
                "count" to count.toString(),
            )
        }

        data object AlbumCreate : Archive {
            override val name = "album_create"
        }

        data class AlbumAddFromDetail(val albumCount: Int) : Archive {
            override val name = "album_add_from_detail"
            override val params = mapOf("album_count" to albumCount.toString())
        }

        data class AlbumAddFromMulti(val photoCount: Int, val albumCount: Int) : Archive {  // 흠
            override val name = "album_add_from_multi"
            override val params = mapOf(
                "photo_count" to photoCount.toString(),
                "album_count" to albumCount.toString(),
            )
        }

        data object PhotoMove : Archive {
            override val name = "photo_move"
        }

        data object PhotoCopy : Archive {
            override val name = "photo_copy"
        }

        data object PhotoDetailView : Archive {
            override val name = "photo_detail_view"
        }

        data object PhotoMemoCreate : Archive {
            override val name = "photo_memo_create"
        }
    }

    sealed interface Pose : AnalyticsEvent {
        data object PoseView : Pose {
            override val name = "pose_view"
        }

        data object PoseRandomStart : Pose {
            override val name = "pose_random_start"
        }


        data class PoseRandomSessionEnd(val totalSwipeCount: Int) : Pose {
            override val name = "pose_random_session_end"
            override val params = mapOf("total_swipe_count" to totalSwipeCount.toString())
        }

        data class PoseFilterToggle(val peopleCount: Int) : Pose {
            override val name = "pose_filter_toggle"
            override val params = mapOf("people_count" to peopleCount.toString())
        }

        data object PoseBookmarkFilter : Pose {
            override val name = "pose_bookmark_filter"
        }

        data object PoseBookmark : Pose {
            override val name = "pose_bookmark"
        }
    }

    sealed interface FourCutMap : AnalyticsEvent {

        data object MapView : FourCutMap {
            override val name = "map_view"
        }

        data class MapReSearch(val hasFilter: Boolean, val regionChanged: Boolean) : FourCutMap {
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
        ) : FourCutMap {
            override val name = "map_brand_filter_toggle"
            override val params = mapOf(
                "action" to action,
                "selected_count" to selectedCount.toString(),
                "brand_name" to brandName,
            )
        }

        data class BoothSelect(val entryPoint: String, val brandName: String) : FourCutMap {
            override val name = "booth_select"
            override val params = mapOf(
                "entry_point" to entryPoint,
                "brand_name" to brandName,
            )
        }

        data class MapRouteClick(val mapType: String) : FourCutMap {
            override val name = "map_route_click"
            override val params = mapOf("map_type" to mapType)
        }
    }

    sealed interface MyPage : AnalyticsEvent
}

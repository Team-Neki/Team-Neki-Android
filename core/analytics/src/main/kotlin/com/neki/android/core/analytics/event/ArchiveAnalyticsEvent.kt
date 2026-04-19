package com.neki.android.core.analytics.event

sealed interface ArchiveAnalyticsEvent : AnalyticsEvent {

    data object ArchivingView : ArchiveAnalyticsEvent {
        override val name = "archiving_view"
    }

    data class PhotoUpload(val method: String, val count: Int) : ArchiveAnalyticsEvent {
        override val name = "photo_upload"
        override val params = mapOf(
            "method" to method,
            "count" to count.toString(),
        )
    }

    data object AlbumCreate : ArchiveAnalyticsEvent {
        override val name = "album_create"
    }

    data class AlbumAddFromDetail(val albumCount: Int) : ArchiveAnalyticsEvent {
        override val name = "album_add_from_detail"
        override val params = mapOf("album_count" to albumCount.toString())
    }

    data class AlbumAddFromMulti(val photoCount: Int, val albumCount: Int) : ArchiveAnalyticsEvent {
        override val name = "album_add_from_multi"
        override val params = mapOf(
            "photo_count" to photoCount.toString(),
            "album_count" to albumCount.toString(),
        )
    }

    data object PhotoMove : ArchiveAnalyticsEvent {
        override val name = "photo_move"
    }

    data object PhotoCopy : ArchiveAnalyticsEvent {
        override val name = "photo_copy"
    }

    data object PhotoDetailView : ArchiveAnalyticsEvent {
        override val name = "photo_detail_view"
    }

    data object PhotoMemoCreate : ArchiveAnalyticsEvent {
        override val name = "photo_memo_create"
    }
}

package com.neki.android.core.analytics.logger

import com.neki.android.core.analytics.event.AnalyticsEvent
import com.neki.android.core.analytics.event.ArchiveAnalyticsEvent
import com.neki.android.core.analytics.event.GlobalAnalyticsEvent
import com.neki.android.core.analytics.event.MapAnalyticsEvent
import com.neki.android.core.analytics.event.MypageAnalyticsEvent
import com.neki.android.core.analytics.event.PoseAnalyticsEvent
import org.junit.Assert.assertEquals
import org.junit.Test

class AmplitudeAnalyticsLoggerTest {

    @Test
    fun `모든 기존 이벤트의 이름과 속성을 그대로 전달한다`() {
        val client = RecordingAmplitudeAnalyticsClient()
        val logger = AmplitudeAnalyticsLogger(client)

        existingEvents.forEach(logger::log)

        assertEquals(31, existingEvents.size)
        assertEquals(31, existingEvents.map(AnalyticsEvent::name).toSet().size)
        assertEquals(
            existingEvents.map { TrackedEvent(it.name, it.params) },
            client.trackedEvents,
        )
    }

    @Test
    fun `사용자 ID 설정과 해제를 전달한다`() {
        val client = RecordingAmplitudeAnalyticsClient()
        val logger = AmplitudeAnalyticsLogger(client)

        logger.setUserId("123")
        logger.clearUserId()

        assertEquals(listOf("123", null), client.userIds)
    }

    private class RecordingAmplitudeAnalyticsClient : AmplitudeAnalyticsClient {
        val trackedEvents = mutableListOf<TrackedEvent>()
        val userIds = mutableListOf<String?>()

        override fun track(eventName: String, eventProperties: Map<String, Any?>) {
            trackedEvents += TrackedEvent(eventName, eventProperties)
        }

        override fun setUserId(userId: String?) {
            userIds += userId
        }
    }

    private data class TrackedEvent(
        val name: String,
        val properties: Map<String, Any?>,
    )

    private companion object {
        val existingEvents = listOf(
            GlobalAnalyticsEvent.AppOpen,
            GlobalAnalyticsEvent.NotificationClick,
            ArchiveAnalyticsEvent.ArchivingView,
            ArchiveAnalyticsEvent.PhotoUpload(method = "gallery", count = 2),
            ArchiveAnalyticsEvent.AlbumCreate,
            ArchiveAnalyticsEvent.AlbumAddFromDetail(albumCount = 2),
            ArchiveAnalyticsEvent.AlbumAddFromMulti(photoCount = 3, albumCount = 2),
            ArchiveAnalyticsEvent.PhotoMove,
            ArchiveAnalyticsEvent.PhotoCopy,
            ArchiveAnalyticsEvent.PhotoDetailView,
            ArchiveAnalyticsEvent.PhotoMemoCreate,
            ArchiveAnalyticsEvent.PhotoAddToAlbum(photoCount = 3, albumCount = 1),
            MapAnalyticsEvent.MapView,
            MapAnalyticsEvent.MapReSearch(hasFilter = true, regionChanged = false),
            MapAnalyticsEvent.MapBrandFilterToggle(
                action = "select",
                selectedCount = 1,
                brandName = "인생네컷",
            ),
            MapAnalyticsEvent.BoothSelect(entryPoint = "map", brandName = "인생네컷"),
            MapAnalyticsEvent.MapRouteClick(mapType = "kakao_map"),
            MapAnalyticsEvent.BrandOrderSave(
                priorityBrand1 = "인생네컷",
                priorityBrand2 = "포토이즘",
                priorityBrand3 = "포토그레이",
            ),
            MapAnalyticsEvent.FavoriteBoothView(favoriteBoothCount = 3),
            MapAnalyticsEvent.FavoriteBoothFilterOn(favoriteBoothCount = 3),
            MapAnalyticsEvent.FavoriteBoothFilterOff,
            MapAnalyticsEvent.BoothFavoriteAdd(boothName = "건대점", brandName = "인생네컷"),
            MapAnalyticsEvent.BoothFavoriteRemove(boothName = "건대점", brandName = "인생네컷"),
            PoseAnalyticsEvent.PoseView,
            PoseAnalyticsEvent.PoseRandomStart,
            PoseAnalyticsEvent.PoseRandomSessionEnd(totalSwipeCount = 4),
            PoseAnalyticsEvent.PoseFilterToggle(peopleCount = 2),
            PoseAnalyticsEvent.PoseBookmarkFilter,
            PoseAnalyticsEvent.PoseBookmark,
            MypageAnalyticsEvent.Logout,
            MypageAnalyticsEvent.Withdraw,
        )
    }
}

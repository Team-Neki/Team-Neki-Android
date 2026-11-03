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

        assertEquals(32, existingEvents.size)
        assertEquals(32, existingEvents.map(AnalyticsEvent::name).toSet().size)
        assertEquals(
            existingEvents.map { TrackedEvent(it.name, it.params) },
            client.trackedEvents,
        )
    }

    @Test
    fun `브랜드 ID를 모르면 brand_id 속성을 보내지 않는다`() {
        val events = listOf(
            MapAnalyticsEvent.BoothSelect(entryPoint = "map", brandName = "인생네컷", brandId = null),
            MapAnalyticsEvent.BoothFavoriteAdd(boothName = "건대점", brandName = "인생네컷", brandId = null),
            MapAnalyticsEvent.BoothFavoriteRemove(boothName = "건대점", brandName = "인생네컷", brandId = null),
        )

        events.forEach { assertEquals(false, "brand_id" in it.params) }
    }

    @Test
    fun `사용자 ID 설정과 해제를 전달한다`() {
        val client = RecordingAmplitudeAnalyticsClient()
        val logger = AmplitudeAnalyticsLogger(client)

        logger.setUserId("123")
        logger.clearUserId()

        assertEquals(listOf("123", null), client.userIds)
    }

    @Test
    fun `로그아웃하거나 다른 회원으로 바뀌면 새 세션을 열고 로그인은 세션을 잇는다`() {
        val client = RecordingAmplitudeAnalyticsClient()
        val logger = AmplitudeAnalyticsLogger(client)

        logger.setUserId("A")
        logger.setUserId("A")
        assertEquals(0, client.newSessions)
        logger.clearUserId()
        logger.clearUserId()
        assertEquals(1, client.newSessions)
        logger.setUserId("B")
        assertEquals(1, client.newSessions)
        logger.setUserId("C")
        assertEquals(2, client.newSessions)
    }

    private class RecordingAmplitudeAnalyticsClient : AmplitudeAnalyticsClient {
        val trackedEvents = mutableListOf<TrackedEvent>()
        val userIds = mutableListOf<String?>()
        var newSessions = 0

        override fun track(eventName: String, eventProperties: Map<String, Any?>) {
            trackedEvents += TrackedEvent(eventName, eventProperties)
        }

        override fun currentUserId() = userIds.lastOrNull()

        override fun startNewSession() {
            newSessions++
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
                brandId = "12",
            ),
            MapAnalyticsEvent.BoothSelect(entryPoint = "map", brandName = "인생네컷", brandId = "12"),
            MapAnalyticsEvent.MapRouteClick(mapType = "kakao_map"),
            MapAnalyticsEvent.BrandOrderSave(
                priorityBrand1 = "인생네컷",
                priorityBrand2 = "포토이즘",
                priorityBrand3 = "포토그레이",
                priorityBrandId1 = "12",
                priorityBrandId2 = "3",
                priorityBrandId3 = "",
            ),
            MapAnalyticsEvent.FavoriteBoothView(favoriteBoothCount = 3),
            MapAnalyticsEvent.FavoriteBoothFilterOn(favoriteBoothCount = 3),
            MapAnalyticsEvent.FavoriteBoothFilterOff,
            MapAnalyticsEvent.BoothFavoriteAdd(boothName = "건대점", brandName = "인생네컷", brandId = "12"),
            MapAnalyticsEvent.BoothFavoriteRemove(boothName = "건대점", brandName = "인생네컷", brandId = "12"),
            PoseAnalyticsEvent.PoseView,
            PoseAnalyticsEvent.PoseRandomStart,
            PoseAnalyticsEvent.PoseRandomSessionEnd(totalSwipeCount = 4),
            PoseAnalyticsEvent.PoseFilterToggle(peopleCount = 2),
            PoseAnalyticsEvent.PoseBookmarkFilter,
            PoseAnalyticsEvent.PoseBookmark,
            PoseAnalyticsEvent.PoseDetailClick(poseId = "128", peopleCount = 2, entryPoint = "list"),
            MypageAnalyticsEvent.Logout,
            MypageAnalyticsEvent.Withdraw,
        )
    }
}

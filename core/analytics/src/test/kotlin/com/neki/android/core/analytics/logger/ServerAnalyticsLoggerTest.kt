package com.neki.android.core.analytics.logger

import com.amplitude.core.events.BaseEvent
import com.neki.android.core.analytics.event.GlobalAnalyticsEvent
import com.neki.android.core.analytics.event.MypageAnalyticsEvent
import com.neki.android.core.dataapi.repository.ClientLogRepository
import com.neki.android.core.model.analytics.ClientLogDraft
import com.neki.android.core.model.analytics.ClientLogEvent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class ServerAnalyticsLoggerTest {
    private val sessions = AmplitudeSessionRegistry()

    @Test
    fun `Amplitude에는 즉시 전달하고 서버 로그는 발생 시점 사용자와 시각을 보존한다`() = runTest {
        val repository = RecordingRepository()
        val client = RecordingClient()
        var time = 1_000L
        val serverLogger = serverLogger(client, repository)
        val logger = NekiAnalyticsLogger(AmplitudeAnalyticsLogger(client), serverLogger, sessions) { time }
        logger.log(GlobalAnalyticsEvent.AppOpen)
        logger.setUserId("A")
        time = 2_000
        logger.log(GlobalAnalyticsEvent.AppOpen)
        logger.clearUserId()
        time = 3_000
        client.session = 600L
        logger.log(GlobalAnalyticsEvent.AppOpen)
        assertEquals(listOf(null, "A", null), client.occurrenceUsers)
        assertTrue(repository.events.isEmpty())
        runCurrent()
        assertEquals(listOf(null, "A", null), repository.events.map { it.userIdAtOccurrence })
        assertEquals(
            listOf("1970-01-01T00:00:01Z", "1970-01-01T00:00:02Z", "1970-01-01T00:00:03Z"),
            repository.events.map { it.occurredAt },
        )
        assertTrue(repository.events.all { it.deviceId == "sdk-device" })
        assertEquals(repository.events.map { it.eventId }, client.insertIds)
        assertEquals(listOf(1_000L, 2_000L, 3_000L), client.timestamps)
        assertEquals(listOf(500L, 500L, 600L), repository.events.map { it.sessionId })
    }

    @Test
    fun `플러그인은 Amplitude가 정한 세션을 같은 이벤트 ID로 넘긴다`() = runTest {
        sessions.expect("event-1")
        AmplitudeSessionPlugin(sessions).execute(
            BaseEvent().apply {
                insertId = "event-1"
                sessionId = 777L
            },
        )
        assertEquals(777L, sessions.await("event-1"))
    }

    @Test
    fun `Amplitude가 세션을 알려주지 않으면 2초 뒤 Amplitude 현재 세션으로 저장한다`() = runTest {
        val repository = RecordingRepository()
        val client = RecordingClient().apply {
            reportsSession = false
            currentSession = 900L
        }
        val serverLogger = serverLogger(client, repository)
        val logger = NekiAnalyticsLogger(AmplitudeAnalyticsLogger(client), serverLogger, sessions)
        logger.log(GlobalAnalyticsEvent.AppOpen)
        logger.log(GlobalAnalyticsEvent.AppOpen)
        runCurrent()
        assertTrue(repository.events.isEmpty())
        advanceTimeBy(2_001)
        runCurrent()
        assertEquals(listOf(900L, 900L), repository.events.map { it.sessionId })
    }

    @Test
    fun `사용자 복원 전 이벤트는 Amplitude에 저장된 사용자 ID를 쓰고 명시적 해제 후에는 null로 남긴다`() = runTest {
        val repository = RecordingRepository()
        val client = RecordingClient().apply { restoredUser = "A" }
        val logger = serverLogger(client, repository)
        logger.log(GlobalAnalyticsEvent.NotificationClick)
        logger.clearUserId()
        logger.log(GlobalAnalyticsEvent.AppOpen)
        runCurrent()
        assertEquals(listOf("A", null), repository.events.map { it.userIdAtOccurrence })
    }

    @Test
    fun `사용자 복원 전에 탈퇴해도 Amplitude에 저장된 회원의 남은 로그를 지운다`() = runTest {
        val repository = RecordingRepository()
        val logger = serverLogger(RecordingClient().apply { restoredUser = "A" }, repository)
        logger.log(GlobalAnalyticsEvent.AppOpen)
        logger.deletePendingUserEvents()
        logger.log(MypageAnalyticsEvent.Withdraw)
        runCurrent()
        assertEquals(listOf("mypage_withdraw"), repository.events.map { it.eventType })
        assertEquals(listOf<String?>("A"), repository.events.map { it.userIdAtOccurrence })
    }

    @Test
    fun `영속 저장 실패가 기존 Amplitude 전송을 막지 않는다`() = runTest {
        val repository = RecordingRepository().apply { failWrites = true }
        val client = RecordingClient()
        val serverLogger = serverLogger(client, repository)
        val logger = NekiAnalyticsLogger(AmplitudeAnalyticsLogger(client), serverLogger, sessions)
        logger.log(GlobalAnalyticsEvent.AppOpen)
        runCurrent()
        assertEquals(listOf("app_open"), client.names)
        assertTrue(repository.events.isEmpty())
    }

    @Test
    fun `로그아웃 flush는 이전 이벤트 저장을 기다린 뒤 전송한다`() = runTest {
        val repository = RecordingRepository()
        val logger = serverLogger(RecordingClient(), repository)
        logger.log(GlobalAnalyticsEvent.AppOpen)
        val flush = launch { logger.flush() }
        runCurrent()
        assertTrue(flush.isCompleted)
        assertEquals(1, repository.countAtFlush)
    }

    @Test
    fun `전송이 멈춰도 로그아웃 flush는 3초 후 반환한다`() = runTest {
        val repository = RecordingRepository().apply { flushGate = CompletableDeferred() }
        val logger = serverLogger(RecordingClient(), repository)
        logger.log(GlobalAnalyticsEvent.AppOpen)
        val flush = launch { logger.flush() }
        runCurrent()
        assertFalse(flush.isCompleted)
        advanceTimeBy(3_001)
        runCurrent()
        assertTrue(flush.isCompleted)
        assertEquals(1, repository.events.size)
    }

    @Test
    fun `탈퇴하면 그 회원의 남은 로그는 지우고 탈퇴 이벤트만 남긴다`() = runTest {
        val repository = RecordingRepository()
        val logger = serverLogger(RecordingClient(), repository)
        logger.setUserId("A")
        logger.log(GlobalAnalyticsEvent.AppOpen)
        logger.deletePendingUserEvents()
        logger.log(MypageAnalyticsEvent.Withdraw)
        logger.clearUserId()
        logger.log(GlobalAnalyticsEvent.AppOpen)
        runCurrent()
        assertEquals(listOf("mypage_withdraw", "app_open"), repository.events.map { it.eventType })
        assertEquals(listOf("A", null), repository.events.map { it.userIdAtOccurrence })
    }

    private fun TestScope.serverLogger(client: RecordingClient, repository: RecordingRepository) = ServerAnalyticsLogger(
        client,
        sessions,
        lazyOf(repository),
        lazyOf("1.3.8"),
        lazyOf(JsonObject(emptyMap())),
        backgroundScope,
    )

    private inner class RecordingClient : AmplitudeAnalyticsClient {
        var session = 500L
        var reportsSession = true
        var currentSession = -1L
        val names = mutableListOf<String>()
        val occurrenceUsers = mutableListOf<String?>()
        val insertIds = mutableListOf<String>()
        val timestamps = mutableListOf<Long>()
        var user: String? = null
        override fun track(eventName: String, eventProperties: Map<String, Any?>) {
            names += eventName
            occurrenceUsers += user
        }
        override fun track(eventName: String, eventProperties: Map<String, Any?>, insertId: String, timestamp: Long) {
            insertIds += insertId
            timestamps += timestamp
            track(eventName, eventProperties)
            // Stands in for AmplitudeSessionPlugin.
            if (reportsSession) sessions.complete(insertId, session)
        }
        override fun currentSessionId() = currentSession
        var restoredUser: String? = null
        override suspend fun awaitDeviceId() = "sdk-device"
        override suspend fun awaitUserId() = restoredUser
        override fun setUserId(userId: String?) { user = userId }
    }

    private class RecordingRepository : ClientLogRepository {
        val events = mutableListOf<ClientLogEvent>()
        var failWrites = false
        var countAtFlush = 0
        var flushGate: CompletableDeferred<Unit>? = null
        override val retryAtMillis = 0L
        override fun setUserId(userId: String?) = Unit
        override suspend fun enqueue(draft: ClientLogDraft): Result<ClientLogEvent> {
            if (failWrites) return Result.failure(IllegalStateException("disk unavailable"))
            return Result.success(
                ClientLogEvent(
                    draft.eventId, draft.eventType, draft.eventProperties, Instant.ofEpochMilli(draft.occurredAtMillis).toString(),
                    draft.sessionId, events.size + 1, draft.deviceId, draft.userIdAtOccurrence, draft.appVersion, draft.context,
                ).also { events += it },
            )
        }
        override suspend fun pendingCount() = events.size
        override suspend fun deleteUserEvents(userId: String) {
            events.removeAll { it.userIdAtOccurrence == userId }
        }
        override suspend fun flush(ignoreBackoff: Boolean): Result<Unit> {
            countAtFlush = events.size
            flushGate?.await()
            return Result.success(Unit)
        }
    }
}

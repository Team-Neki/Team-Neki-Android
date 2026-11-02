package com.neki.android.core.data.analytics

import com.neki.android.core.data.local.model.ClientLogStore
import com.neki.android.core.data.local.model.LOG_RETENTION_MILLIS
import com.neki.android.core.data.local.model.MAX_STORED_LOGS
import com.neki.android.core.model.analytics.ClientLogDraft
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class ClientLogStoreTest {
    @Test
    fun `재시작 후 세션과 순번을 복원하고 JSON 타입을 유지한다`() {
        val first = ClientLogStore().append(draft(1_000))
        val restored = Json.decodeFromString<ClientLogStore>(Json.encodeToString(first.first))
        val second = restored.append(draft(2_000))
        assertEquals(first.second.sessionId, second.second.sessionId)
        assertEquals(2, second.second.sequenceNumber)
        assertEquals(JsonPrimitive(true), second.second.eventProperties["enabled"])
        assertEquals(JsonPrimitive(2), second.second.eventProperties["count"])
        assertEquals("1970-01-01T00:00:02Z", second.second.occurredAt)
    }

    @Test
    fun `Amplitude 세션이 바뀌면 순번을 1부터 다시 센다`() {
        val first = ClientLogStore().append(draft(1_000, session = 500)).first
        val same = first.append(draft(2_000, session = 500))
        assertEquals(2, same.second.sequenceNumber)
        val next = same.first.append(draft(3_000, session = 600))
        assertEquals(600L, next.second.sessionId)
        assertEquals(1, next.second.sequenceNumber)
    }

    @Test
    fun `순번이 넘치면 1부터 다시 센다`() {
        val first = ClientLogStore().append(draft(1_000)).first
        val overflow = first.copy(sequenceNumber = Int.MAX_VALUE).append(draft(2_000))
        assertEquals(1, overflow.second.sequenceNumber)
    }

    @Test
    fun `세션 계산을 옮기기 전에 저장한 큐도 읽는다`() = kotlinx.coroutines.runBlocking {
        val legacy = """{"events":[],"sessionId":1000,"sequenceNumber":3,"lastActiveAt":5000}"""
        val store = com.neki.android.core.data.local.serializer.ClientLogStoreSerializer.readFrom(legacy.byteInputStream())
        assertEquals(3, store.sequenceNumber)
    }

    @Test
    fun `7일 또는 5000건을 넘긴 오래된 이벤트를 제거한다`() {
        val event = ClientLogStore().append(draft(1_000)).second
        val store = ClientLogStore(events = List(MAX_STORED_LOGS + 1) { event.copy(eventId = "$it") })
        assertEquals(MAX_STORED_LOGS, store.prune(1_001).events.size)
        assertEquals("1", store.prune(1_001).events.first().eventId)
        assertTrue(store.prune(1_001 + LOG_RETENTION_MILLIS).events.isEmpty())
    }

    companion object {
        fun draft(
            timestamp: Long = System.currentTimeMillis(),
            user: String? = null,
            session: Long = 1_000,
        ): ClientLogDraft = ClientLogDraft(
            eventId = UUID.randomUUID().toString(),
            eventType = "test_event",
            eventProperties = JsonObject(mapOf("enabled" to JsonPrimitive(true), "count" to JsonPrimitive(2))),
            occurredAtMillis = timestamp,
            sessionId = session,
            deviceId = "stable-device",
            userIdAtOccurrence = user,
            appVersion = "1.3.8-dev",
            context = JsonObject(mapOf("osName" to JsonPrimitive("Android"))),
        )
    }
}

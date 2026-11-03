package com.neki.android.core.data.analytics

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.neki.android.core.data.analytics.ClientLogStoreTest.Companion.draft
import com.neki.android.core.data.local.model.ClientLogStore
import com.neki.android.core.data.local.serializer.ClientLogStoreSerializer
import com.neki.android.core.data.remote.api.ClientLogService
import com.neki.android.core.data.remote.model.request.ClientLogRequest
import com.neki.android.core.data.repository.impl.ClientLogRepositoryImpl
import com.neki.android.core.dataapi.repository.TokenRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ClientLogRepositoryTest {
    @get:Rule val directory = TemporaryFolder()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val requests = mutableListOf<HttpRequestData>()
    private lateinit var store: DataStore<ClientLogStore>
    private lateinit var client: HttpClient
    private var status: suspend (HttpRequestData) -> HttpStatusCode = { HttpStatusCode.OK }
    private var badRequestCode = "D-01"
    private var accessToken: String? = null
    private val tokenRepository = object : TokenRepository {
        override suspend fun saveTokens(accessToken: String, refreshToken: String) {
            this@ClientLogRepositoryTest.accessToken = accessToken
        }
        override fun hasTokens() = flowOf(accessToken != null)
        override fun getAccessToken() = flowOf(accessToken.orEmpty())
        override fun getRefreshToken() = flowOf("")
        override suspend fun clearTokensWithAuthCache() {
            accessToken = null
        }
    }

    @Before
    fun setup() {
        store = DataStoreFactory.create(serializer = ClientLogStoreSerializer, scope = scope) {
            directory.newFile("queue.json").also { it.delete() }
        }
        client = HttpClient(MockEngine { request ->
            requests += request
            val code = status(request)
            val resultCode = when (code) {
                HttpStatusCode.OK -> "D-0"
                HttpStatusCode.BadRequest -> badRequestCode
                else -> "D-15"
            }
            val body = """{"resultCode":"$resultCode","message":"test","data":null}"""
            respond(body, code, headersOf(HttpHeaders.ContentType, "application/json"))
        }) {
            install(ContentNegotiation) { json(clientLogJson) }
            expectSuccess = true
        }
    }

    @After
    fun cleanup() {
        scope.cancel()
        client.close()
    }

    private fun repository() = ClientLogRepositoryImpl(store, ClientLogService(client), tokenRepository)
    private fun HttpRequestData.logs() = clientLogJson.decodeFromString<ClientLogRequest>((body as TextContent).text).logs

    @Test
    fun `계정별로 배치를 나누고 로그인하지 않은 계정의 이벤트는 토큰 없이 보낸다`() = runBlocking {
        val repository = repository()
        tokenRepository.saveTokens("token-A", "refresh-A")
        repository.setUserId("A")
        repository.enqueue(draft(user = null)).getOrThrow()
        repository.enqueue(draft(user = "A")).getOrThrow()
        repository.enqueue(draft(user = "B")).getOrThrow()
        repository.flush().getOrThrow()
        assertEquals(3, requests.size)
        assertEquals(null, requests[0].headers[HttpHeaders.Authorization])
        assertEquals("Bearer token-A", requests[1].headers[HttpHeaders.Authorization])
        assertEquals(null, requests[2].headers[HttpHeaders.Authorization])
        assertEquals(listOf<String?>("B"), requests[2].logs().map { it.userIdAtOccurrence })
        assertTrue(store.data.first().events.isEmpty())
    }

    @Test
    fun `탈퇴 후 남은 이벤트는 발생 당시 사용자 ID를 유지한 채 토큰 없이 보낸다`() = runBlocking {
        val repository = repository()
        tokenRepository.saveTokens("token-A", "refresh-A")
        repository.setUserId("A")
        repository.enqueue(draft(user = "A")).getOrThrow()
        tokenRepository.clearTokensWithAuthCache()
        repository.setUserId(null)
        repository.flush().getOrThrow()
        assertEquals(null, requests.single().headers[HttpHeaders.Authorization])
        assertEquals(listOf<String?>("A"), requests.single().logs().map { it.userIdAtOccurrence })
        assertTrue(store.data.first().events.isEmpty())
    }

    @Test
    fun `503 후 재시작해 재전송해도 발생 정보와 ID를 바꾸지 않는다`() = runBlocking {
        val repository = repository()
        val event = repository.enqueue(draft()).getOrThrow()
        status = { HttpStatusCode.ServiceUnavailable }
        assertTrue(repository.flush().isFailure)
        assertEquals(listOf(event), store.data.first().events)
        assertTrue(repository.retryAtMillis > System.currentTimeMillis())
        status = { HttpStatusCode.OK }
        repository().flush().getOrThrow()
        assertEquals(requests[0].logs(), requests[1].logs())
        assertTrue(store.data.first().events.isEmpty())
    }

    @Test
    fun `재시도 대기 중에는 보내지 않지만 백그라운드 전환 때는 바로 보낸다`() = runBlocking {
        val repository = repository()
        repository.enqueue(draft()).getOrThrow()
        status = { HttpStatusCode.ServiceUnavailable }
        assertTrue(repository.flush().isFailure)
        status = { HttpStatusCode.OK }
        repository.flush().getOrThrow()
        assertEquals(1, requests.size)
        repository.flush(ignoreBackoff = true).getOrThrow()
        assertEquals(2, requests.size)
        assertTrue(store.data.first().events.isEmpty())
    }

    @Test
    fun `413 배치를 분할하고 원래 순번을 보존한다`() = runBlocking {
        val repository = repository()
        repeat(3) { repository.enqueue(draft()).getOrThrow() }
        status = { if (it.logs().size > 1) HttpStatusCode.PayloadTooLarge else HttpStatusCode.OK }
        repository.flush().getOrThrow()
        val delivered = requests.filter { it.logs().size == 1 }.flatMap { it.logs() }
        assertEquals(listOf(1, 2, 3), delivered.map { it.sequenceNumber })
        assertEquals(3, delivered.map { it.eventId }.toSet().size)
        assertTrue(store.data.first().events.isEmpty())
    }

    @Test
    fun `400 단일 불량 이벤트만 버리고 다음 이벤트를 전송한다`() = runBlocking {
        val repository = repository()
        val bad = repository.enqueue(draft()).getOrThrow()
        val good = repository.enqueue(draft()).getOrThrow()
        status = { if (it.logs().any { event -> event.eventId == bad.eventId }) HttpStatusCode.BadRequest else HttpStatusCode.OK }
        repository.flush().getOrThrow()
        assertEquals(listOf(good), requests.last().logs())
        assertTrue(store.data.first().events.isEmpty())
    }

    @Test
    fun `토큰만 있고 사용자 ID 복원 전이면 계정 이벤트는 보류하고 익명 이벤트만 보낸다`() = runBlocking {
        val repository = repository()
        tokenRepository.saveTokens("token-A", "refresh-A")
        repository.enqueue(draft(user = "A")).getOrThrow()
        repository.enqueue(draft()).getOrThrow()
        repository.flush().getOrThrow()
        assertEquals(listOf<String?>(null), requests.single().logs().map { it.userIdAtOccurrence })
        assertEquals(listOf<String?>("A"), store.data.first().events.map { it.userIdAtOccurrence })
        repository.setUserId("A")
        repository.flush().getOrThrow()
        assertEquals("Bearer token-A", requests.last().headers[HttpHeaders.Authorization])
        assertTrue(store.data.first().events.isEmpty())
    }

    @Test
    fun `탈퇴 회원 로그를 지워도 익명과 다른 회원의 로그는 남긴다`() = runBlocking {
        val repository = repository()
        repository.enqueue(draft(user = "A")).getOrThrow()
        repository.enqueue(draft(user = "B")).getOrThrow()
        repository.enqueue(draft()).getOrThrow()
        repository.deleteUserEvents("A")
        assertEquals(listOf("B", null), store.data.first().events.map { it.userIdAtOccurrence })
    }

    @Test
    fun `D01이 아닌 400은 서버 오류로 보고 이벤트를 버리지 않고 나중에 재시도한다`() = runBlocking {
        val repository = repository()
        val events = List(2) { repository.enqueue(draft()).getOrThrow() }
        status = { HttpStatusCode.BadRequest }
        badRequestCode = "D-99"
        assertTrue(repository.flush().isFailure)
        assertEquals(1, requests.size)
        assertEquals(events, store.data.first().events)
        assertTrue(repository.retryAtMillis > System.currentTimeMillis())
    }

    @Test
    fun `401이면 토큰만 빼고 같은 로그를 바로 다시 보내고 그 토큰은 더 쓰지 않는다`() = runBlocking {
        val repository = repository()
        tokenRepository.saveTokens("token-A", "refresh-A")
        repository.setUserId("A")
        val event = repository.enqueue(draft(user = "A")).getOrThrow()
        status = { if (it.headers[HttpHeaders.Authorization] != null) HttpStatusCode.Unauthorized else HttpStatusCode.OK }
        repository.flush().getOrThrow()
        assertEquals(listOf("Bearer token-A", null), requests.map { it.headers[HttpHeaders.Authorization] })
        assertEquals(listOf(event), requests[1].logs())
        assertTrue(store.data.first().events.isEmpty())
        repository.enqueue(draft(user = "A")).getOrThrow()
        repository.flush().getOrThrow()
        assertEquals(null, requests.last().headers[HttpHeaders.Authorization])
        assertEquals(3, requests.size)
    }

    @Test
    fun `401 뒤 토큰 없이 다시 보낼 때 그사이 지운 로그는 보내지 않는다`() = runBlocking {
        val repository = repository()
        tokenRepository.saveTokens("token-A", "refresh-A")
        repository.setUserId("A")
        repository.enqueue(draft(user = "A")).getOrThrow()
        status = {
            repository.deleteUserEvents("A")
            HttpStatusCode.Unauthorized
        }
        repository.flush().getOrThrow()
        assertEquals(1, requests.size)
        assertTrue(store.data.first().events.isEmpty())
    }

    @Test
    fun `403이면 그 토큰은 더 쓰지 않고 토큰 없이 다시 보낸다`() = runBlocking {
        val repository = repository()
        tokenRepository.saveTokens("token-A", "refresh-A")
        repository.setUserId("A")
        repository.enqueue(draft(user = "A")).getOrThrow()
        repository.enqueue(draft()).getOrThrow()
        status = { if (it.headers[HttpHeaders.Authorization] != null) HttpStatusCode.Forbidden else HttpStatusCode.OK }
        repository.flush().getOrThrow()
        assertEquals(listOf("Bearer token-A", null, null), requests.map { it.headers[HttpHeaders.Authorization] })
        assertEquals(listOf<String?>("A"), requests[1].logs().map { it.userIdAtOccurrence })
        assertTrue(store.data.first().events.isEmpty())
    }

    @Test
    fun `한 요청은 500건을 넘지 않고 ANDROID 플랫폼 필드를 반드시 포함한다`() = runBlocking {
        val repository = repository()
        val event = repository.enqueue(draft()).getOrThrow()
        store.updateData { it.copy(events = List(501) { index -> event.copy(eventId = "event-$index", sequenceNumber = index + 1) }) }
        repository.flush().getOrThrow()
        assertEquals(listOf(500, 1), requests.map { it.logs().size })
        requests.forEach {
            val payload = clientLogJson.decodeFromString<ClientLogRequest>((it.body as TextContent).text)
            assertEquals("ANDROID", payload.platform)
        }
    }

    @Test
    fun `UTF8 직렬화 후 4MiB를 넘지 않게 배치를 구성한다`() = runBlocking {
        val repository = repository()
        val event = repository.enqueue(draft()).getOrThrow().copy(
            eventProperties = kotlinx.serialization.json.JsonObject(mapOf("value" to kotlinx.serialization.json.JsonPrimitive("가".repeat(200_000)))),
        )
        store.updateData { it.copy(events = List(8) { index -> event.copy(eventId = "large-$index", sequenceNumber = index + 1) }) }
        repository.flush().getOrThrow()
        assertTrue(requests.size > 1)
        assertTrue(requests.all { (it.body as TextContent).text.toByteArray().size <= 4 * 1024 * 1024 })
        assertEquals(8, requests.sumOf { it.logs().size })
    }

    @Test
    fun `응답 미수신이나 D0 이외 결과는 큐에서 제거하지 않는다`() = runBlocking {
        val repository = repository()
        val event = repository.enqueue(draft()).getOrThrow()
        status = { throw java.io.IOException("response lost") }
        assertTrue(repository.flush().isFailure)
        assertEquals(listOf(event), store.data.first().events)
    }
}

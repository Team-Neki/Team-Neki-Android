package com.neki.android.core.data.analytics

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.neki.android.core.data.analytics.ClientLogStoreTest.Companion.draft
import com.neki.android.core.data.local.model.ClientLogStore
import com.neki.android.core.data.local.serializer.ClientLogStoreSerializer
import com.neki.android.core.data.auth.TokenRefreshCoordinator
import com.neki.android.core.data.remote.api.TokenRefreshService
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
import kotlinx.coroutines.async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ClientLogRepositoryTest {
    @get:Rule val directory = TemporaryFolder()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val auth = ClientLogAuthState()
    private val requests = mutableListOf<HttpRequestData>()
    private lateinit var store: DataStore<ClientLogStore>
    private lateinit var client: HttpClient
    private var status: suspend (HttpRequestData) -> HttpStatusCode = { HttpStatusCode.OK }
    private var badRequestCode = "D-01"
    private var tokensReadable = true
    private val tokenRepository = object : TokenRepository {
        override suspend fun saveTokens(accessToken: String, refreshToken: String) = auth.updateTokens(accessToken, refreshToken, false)
        override suspend fun replaceTokensIfCurrent(expectedAccessToken: String, accessToken: String, refreshToken: String): Boolean {
            if (auth.snapshot()?.accessToken != expectedAccessToken) return false
            auth.updateTokens(accessToken, refreshToken, true)
            return true
        }
        override suspend fun clearTokensIfCurrent(expectedAccessToken: String): Boolean {
            if (auth.snapshot()?.accessToken != expectedAccessToken) return false
            auth.clear()
            return true
        }
        override suspend fun getTokens() = auth.snapshot()
            ?.takeIf { tokensReadable }
            ?.let { com.neki.android.core.model.Auth(it.accessToken, it.refreshToken) }
        override fun hasTokens() = flowOf(auth.snapshot() != null)
        override fun getAccessToken() = flowOf(auth.snapshot()?.accessToken.orEmpty())
        override fun getRefreshToken() = flowOf(auth.snapshot()?.refreshToken.orEmpty())
        override suspend fun clearTokensWithAuthCache() = auth.clear()
    }

    @Before
    fun setup() {
        store = DataStoreFactory.create(serializer = ClientLogStoreSerializer, scope = scope) {
            directory.newFile("queue.json").also { it.delete() }
        }
        client = HttpClient(MockEngine { request ->
            requests += request
            val code = status(request)
            val body = if (request.url.encodedPath == "/api/auth/refresh") {
                """{"resultCode":"D-0","message":"OK","data":{"accessToken":"new-A","refreshToken":"refresh-A","isNewUser":false}}"""
            } else {
                val resultCode = when (code) {
                    HttpStatusCode.OK -> "D-0"
                    HttpStatusCode.BadRequest -> badRequestCode
                    else -> "D-15"
                }
                """{"resultCode":"$resultCode","message":"test","data":null}"""
            }
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

    private fun repository() = ClientLogRepositoryImpl(
        store,
        ClientLogService(client),
        TokenRefreshCoordinator(tokenRepository, TokenRefreshService(client)),
        auth,
    )
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
    fun `토큰 갱신이 진행되지 않으면 같은 요청을 반복하지 않고 토큰 없이 보낸다`() = runBlocking {
        val repository = repository()
        tokenRepository.saveTokens("token-A", "refresh-A")
        repository.setUserId("A")
        repository.enqueue(draft(user = "A")).getOrThrow()
        tokensReadable = false
        status = { if (it.headers[HttpHeaders.Authorization] != null) HttpStatusCode.Unauthorized else HttpStatusCode.OK }
        repository.flush().getOrThrow()
        assertEquals(listOf("Bearer token-A", null), requests.map { it.headers[HttpHeaders.Authorization] })
        assertTrue(store.data.first().events.isEmpty())
    }

    @Test
    fun `토큰 갱신 중 호출자가 취소돼도 회전된 토큰을 저장한다`() = runBlocking {
        tokenRepository.saveTokens("token-A", "refresh-A")
        auth.setUserId("A")
        val coordinator = TokenRefreshCoordinator(tokenRepository, TokenRefreshService(client))
        val entered = kotlinx.coroutines.CompletableDeferred<Unit>()
        val release = kotlinx.coroutines.CompletableDeferred<Unit>()
        status = {
            entered.complete(Unit)
            release.await()
            HttpStatusCode.OK
        }
        val refresh = launch { coordinator.refresh("token-A", "refresh-A") }
        entered.await()
        refresh.cancel()
        release.complete(Unit)
        refresh.join()
        assertEquals("new-A", auth.snapshot()?.accessToken)
    }

    @Test
    fun `401이면 같은 계정 토큰만 갱신하고 원본 이벤트를 재전송한다`() = runBlocking {
        val repository = repository()
        tokenRepository.saveTokens("token-A", "refresh-A")
        repository.setUserId("A")
        val event = repository.enqueue(draft(user = "A")).getOrThrow()
        status = { if (it.headers[HttpHeaders.Authorization] == "Bearer token-A") HttpStatusCode.Unauthorized else HttpStatusCode.OK }
        repository.flush().getOrThrow()
        assertEquals(3, requests.size)
        assertEquals("/api/auth/refresh", requests[1].url.encodedPath)
        assertEquals("Bearer new-A", requests[2].headers[HttpHeaders.Authorization])
        assertEquals(listOf(event), requests[2].logs())
        assertTrue(store.data.first().events.isEmpty())
    }

    @Test
    fun `갱신 중 계정 전환 시 새 계정 토큰을 덮어쓰거나 이전 로그에 쓰지 않는다`() = runBlocking {
        val repository = repository()
        tokenRepository.saveTokens("token-A", "refresh-A")
        repository.setUserId("A")
        repository.enqueue(draft(user = "A")).getOrThrow()
        status = {
            if (it.url.encodedPath == "/api/auth/refresh") {
                auth.updateTokens("token-B", "refresh-B", false)
                auth.setUserId("B")
                HttpStatusCode.OK
            } else if (it.headers[HttpHeaders.Authorization] != null) {
                HttpStatusCode.Unauthorized
            } else {
                HttpStatusCode.OK
            }
        }
        repository.flush().getOrThrow()
        assertEquals(3, requests.size)
        assertEquals("token-B", auth.snapshot()?.accessToken)
        assertEquals(null, requests[2].headers[HttpHeaders.Authorization])
        assertTrue(store.data.first().events.isEmpty())
        assertFalse(requests.any { it.headers[HttpHeaders.Authorization] == "Bearer token-B" })
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
    fun `일반 API와 로그가 같은 만료 토큰으로 갱신하면 한 번만 회전시킨다`() = runBlocking {
        tokenRepository.saveTokens("token-A", "refresh-A")
        auth.setUserId("A")
        val coordinator = TokenRefreshCoordinator(tokenRepository, TokenRefreshService(client))
        val entered = kotlinx.coroutines.CompletableDeferred<Unit>()
        val release = kotlinx.coroutines.CompletableDeferred<Unit>()
        status = {
            entered.complete(Unit)
            release.await()
            HttpStatusCode.OK
        }
        val apiRefresh = async {
            coordinator.refresh("token-A", "refresh-A")
        }
        entered.await()
        val logRefresh = async {
            coordinator.refresh("token-A", "refresh-A")
        }
        release.complete(Unit)
        assertEquals(apiRefresh.await(), logRefresh.await())
        assertEquals(1, requests.size)
        assertEquals("new-A", auth.snapshot()?.accessToken)
        assertEquals("A", auth.snapshot()?.userId)
    }

    @Test
    fun `A 요청이 실패한 뒤 B로 전환됐으면 B refresh token을 사용하지 않는다`() = runBlocking {
        tokenRepository.saveTokens("token-B", "refresh-B")
        auth.setUserId("B")
        val coordinator = TokenRefreshCoordinator(tokenRepository, TokenRefreshService(client))
        assertEquals(null, coordinator.refresh("token-A", "refresh-A"))
        assertTrue(requests.isEmpty())
        assertEquals("refresh-B", auth.snapshot()?.refreshToken)
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

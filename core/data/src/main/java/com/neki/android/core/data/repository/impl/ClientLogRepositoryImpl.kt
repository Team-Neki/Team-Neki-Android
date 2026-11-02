package com.neki.android.core.data.repository.impl

import androidx.datastore.core.DataStore
import com.neki.android.core.data.analytics.clientLogJson
import com.neki.android.core.data.auth.TokenRefreshCoordinator
import com.neki.android.core.data.analytics.ClientLogAuthState
import com.neki.android.core.data.local.di.ClientLogDataStore
import com.neki.android.core.data.local.model.ClientLogStore
import com.neki.android.core.data.remote.api.ClientLogService
import com.neki.android.core.data.remote.model.request.ClientLogRequest
import com.neki.android.core.data.remote.model.response.BasicNullableResponse
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.ClientLogRepository
import com.neki.android.core.model.analytics.ClientLogDraft
import com.neki.android.core.model.analytics.ClientLogEvent
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min
import kotlin.random.Random

private const val MAX_BATCH_COUNT = 500
private const val MAX_REQUEST_BYTES = 4 * 1024 * 1024
private const val MAX_RETRY_MILLIS = 300_000L
private const val INVALID_PARAMETER_CODE = "D-01"

@Singleton
internal class ClientLogRepositoryImpl @Inject constructor(
    @ClientLogDataStore private val store: DataStore<ClientLogStore>,
    private val service: ClientLogService,
    private val tokenRefreshCoordinator: TokenRefreshCoordinator,
    private val authState: ClientLogAuthState,
) : ClientLogRepository {
    private val sendMutex = Mutex()
    private var retryCount = 0
    private var blockedAccessToken: String? = null

    @Volatile
    override var retryAtMillis: Long = 0
        private set

    override fun setUserId(userId: String?) {
        authState.setUserId(userId)
    }

    override suspend fun enqueue(draft: ClientLogDraft): Result<ClientLogEvent> = runSuspendCatching {
        var appended: ClientLogEvent? = null
        store.updateData { previous ->
            val (updated, event) = previous.append(draft)
            appended = event
            updated
        }
        checkNotNull(appended)
    }

    override suspend fun pendingCount(): Int = store.data.first().events.size

    override suspend fun flush(): Result<Unit> = sendMutex.withLock {
        if (System.currentTimeMillis() < retryAtMillis) return@withLock Result.success(Unit)
        runSuspendCatching {
            store.updateData { it.prune(System.currentTimeMillis()) }
            while (true) {
                val snapshot = authState.snapshot()
                val credentials = snapshot?.takeIf { it.userId != null && it.accessToken != blockedAccessToken }
                // Tokens are saved before the user ID is restored; hold account events until the owner is known.
                val ownerUnknown = snapshot != null && snapshot.userId == null && snapshot.accessToken != blockedAccessToken
                val events = store.data.first().events.filterNot { ownerUnknown && it.userIdAtOccurrence != null }
                if (events.isEmpty()) break
                val owner = events.first().userIdAtOccurrence
                val batch = selectBatch(events.filter { it.userIdAtOccurrence == owner })
                // Events of another or signed-out account go without a token; the server keeps userIdAtOccurrence.
                val auth = credentials?.takeIf { owner != null && it.userId == owner }
                sendBatch(batch, auth)
            }
            retryCount = 0
            retryAtMillis = 0
        }.onFailure {
            val cap = min(MAX_RETRY_MILLIS, 1_000L shl min(retryCount++, 9))
            retryAtMillis = System.currentTimeMillis() + Random.nextLong(cap, min(MAX_RETRY_MILLIS, cap * 2) + 1)
            Timber.w("Client log delivery deferred (%s)", it.javaClass.simpleName)
        }
    }

    private fun selectBatch(events: List<ClientLogEvent>): List<ClientLogEvent> {
        // Include the exact UTF-8 JSON envelope and separators in the byte budget.
        var bytes = clientLogJson.encodeToString(ClientLogRequest(platform = "ANDROID", logs = emptyList())).toByteArray().size
        return events.take(MAX_BATCH_COUNT).takeWhile {
            bytes += clientLogJson.encodeToString(it).toByteArray().size + 1
            bytes <= MAX_REQUEST_BYTES
        }
    }

    private suspend fun sendBatch(
        events: List<ClientLogEvent>,
        credentials: ClientLogAuthState.Credentials?,
        canRefresh: Boolean = true,
    ) {
        check(events.isNotEmpty())
        try {
            val response = service.send(ClientLogRequest(platform = "ANDROID", logs = events), credentials?.accessToken)
            check(response.resultCode == "D-0") { "Unexpected client log response" }
            remove(events)
        } catch (e: ResponseException) {
            when (e.response.status.value) {
                // The server also answers unexpected failures with 400, so only D-01 means a malformed event.
                400 -> if (e.resultCode() == INVALID_PARAMETER_CODE) splitOrDiscard(events, credentials) else throw e
                413 -> splitOrDiscard(events, credentials)
                401 -> {
                    if (credentials != null && canRefresh) {
                        refreshAndRetry(events, credentials)
                    } else {
                        blockAuthentication(credentials)
                    }
                }
                403 -> blockAuthentication(credentials)
                else -> throw e
            }
        }
    }

    private suspend fun splitOrDiscard(
        events: List<ClientLogEvent>,
        credentials: ClientLogAuthState.Credentials?,
    ) {
        if (events.size == 1) {
            Timber.w("Discarding one invalid or oversized client log: %s", events.single().eventId)
            remove(events)
        } else {
            val middle = events.size / 2
            sendBatch(events.take(middle), credentials)
            sendBatch(events.drop(middle), credentials)
        }
    }

    private suspend fun refreshAndRetry(
        events: List<ClientLogEvent>,
        credentials: ClientLogAuthState.Credentials,
    ) {
        // A new login may already have replaced the credentials; never refresh/overwrite that account.
        if (authState.snapshot() != credentials) return
        val tokens = try {
            tokenRefreshCoordinator.refresh(credentials.accessToken, credentials.refreshToken)
        } catch (e: ResponseException) {
            if (e.response.status.value in 400..499) {
                blockAuthentication(credentials)
                return
            }
            throw e
        } ?: run {
            // No refresh happened for unchanged credentials; stop retrying them without backoff.
            if (authState.snapshot() == credentials) blockAuthentication(credentials)
            return
        }
        sendBatch(events, credentials.copy(accessToken = tokens.accessToken, refreshToken = tokens.refreshToken), canRefresh = false)
    }

    private fun blockAuthentication(credentials: ClientLogAuthState.Credentials?) {
        if (credentials == null) error("Anonymous client log request was rejected")
        blockedAccessToken = credentials.accessToken
        Timber.w("Client log authentication requires a valid session")
    }

    private suspend fun ResponseException.resultCode(): String? = runSuspendCatching {
        clientLogJson.decodeFromString<BasicNullableResponse<Unit>>(response.bodyAsText()).resultCode
    }.getOrNull()

    private suspend fun remove(events: List<ClientLogEvent>) {
        val ids = events.map { it.eventId }.toSet()
        store.updateData { previous -> previous.copy(events = previous.events.filterNot { it.eventId in ids }) }
    }
}

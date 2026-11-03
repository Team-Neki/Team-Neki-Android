package com.neki.android.core.data.repository.impl

import androidx.datastore.core.DataStore
import com.neki.android.core.data.analytics.clientLogJson
import com.neki.android.core.data.local.di.ClientLogDataStore
import com.neki.android.core.data.local.model.ClientLogStore
import com.neki.android.core.data.remote.api.ClientLogService
import com.neki.android.core.data.remote.model.request.ClientLogRequest
import com.neki.android.core.data.remote.model.response.BasicNullableResponse
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.ClientLogRepository
import com.neki.android.core.dataapi.repository.TokenRepository
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
    private val tokenRepository: TokenRepository,
) : ClientLogRepository {
    private val sendMutex = Mutex()
    private var retryCount = 0
    private var blockedAccessToken: String? = null

    @Volatile
    private var userId: String? = null

    @Volatile
    override var retryAtMillis: Long = 0
        private set

    override fun setUserId(userId: String?) {
        this.userId = userId
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

    override suspend fun deleteUserEvents(userId: String) {
        store.updateData { it.copy(events = it.events.filterNot { event -> event.userIdAtOccurrence == userId }) }
    }

    override suspend fun flush(ignoreBackoff: Boolean): Result<Unit> = sendMutex.withLock {
        if (!ignoreBackoff && System.currentTimeMillis() < retryAtMillis) return@withLock Result.success(Unit)
        runSuspendCatching {
            store.updateData { it.prune(System.currentTimeMillis()) }
            while (true) {
                // A token the server rejected once is not attached again until it changes.
                val accessToken = tokenRepository.getAccessToken().first().takeIf { it.isNotBlank() && it != blockedAccessToken }
                val currentUserId = userId
                // Tokens are saved before the user ID is restored; hold account events until the owner is known.
                val ownerUnknown = accessToken != null && currentUserId == null
                val events = store.data.first().events.filterNot { ownerUnknown && it.userIdAtOccurrence != null }
                if (events.isEmpty()) break
                val owner = events.first().userIdAtOccurrence
                val batch = selectBatch(events.filter { it.userIdAtOccurrence == owner })
                // Events of another or signed-out account go without a token; the server keeps userIdAtOccurrence.
                sendBatch(batch, accessToken.takeIf { owner != null && owner == currentUserId })
            }
            retryCount = 0
            retryAtMillis = 0
        }.onFailure {
            val cap = min(MAX_RETRY_MILLIS, 1_000L shl min(retryCount++, 9))
            // Up to 10% jitter, as on iOS, so devices do not retry at the same moment.
            retryAtMillis = System.currentTimeMillis() + cap + Random.nextLong(cap / 10 + 1)
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

    private suspend fun sendBatch(events: List<ClientLogEvent>, accessToken: String?) {
        check(events.isNotEmpty())
        try {
            val response = service.send(ClientLogRequest(platform = "ANDROID", logs = events), accessToken)
            check(response.resultCode == "D-0") { "Unexpected client log response" }
            remove(events)
        } catch (e: ResponseException) {
            when (e.response.status.value) {
                // The server also answers unexpected failures with 400, so only D-01 means a malformed event.
                400 -> if (e.resultCode() == INVALID_PARAMETER_CODE) splitOrDiscard(events, accessToken) else throw e
                413 -> splitOrDiscard(events, accessToken)
                // Authorization is optional: drop the rejected token and resend the same logs right away.
                401, 403 -> {
                    if (accessToken == null) error("Anonymous client log request was rejected")
                    blockedAccessToken = accessToken
                    Timber.w("Client log token was rejected; sending without it")
                    // Logs deleted in the meantime (account withdrawal) must not be resent.
                    val queued = store.data.first().events.mapTo(mutableSetOf()) { it.eventId }
                    val remaining = events.filter { it.eventId in queued }
                    if (remaining.isNotEmpty()) sendBatch(remaining, null)
                }
                else -> throw e
            }
        }
    }

    private suspend fun splitOrDiscard(events: List<ClientLogEvent>, accessToken: String?) {
        if (events.size == 1) {
            Timber.w("Discarding one invalid or oversized client log: %s", events.single().eventId)
            remove(events)
        } else {
            val middle = events.size / 2
            sendBatch(events.take(middle), accessToken)
            sendBatch(events.drop(middle), accessToken)
        }
    }

    private suspend fun ResponseException.resultCode(): String? = runSuspendCatching {
        clientLogJson.decodeFromString<BasicNullableResponse<Unit>>(response.bodyAsText()).resultCode
    }.getOrNull()

    private suspend fun remove(events: List<ClientLogEvent>) {
        val ids = events.map { it.eventId }.toSet()
        store.updateData { previous -> previous.copy(events = previous.events.filterNot { it.eventId in ids }) }
    }
}

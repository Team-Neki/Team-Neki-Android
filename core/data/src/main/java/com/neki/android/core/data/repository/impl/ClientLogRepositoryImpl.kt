package com.neki.android.core.data.repository.impl

import androidx.datastore.core.DataStore
import com.neki.android.core.data.analytics.clientLogJson
import com.neki.android.core.data.analytics.ClientLogDiagnostics
import com.neki.android.core.data.local.di.ClientLogDataStore
import com.neki.android.core.data.local.model.ClientLogStore
import com.neki.android.core.data.local.model.ClientLogPruneResult
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
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber
import java.time.Instant
import java.util.Base64
import java.util.concurrent.atomic.AtomicReference
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
    private val diagnostics: ClientLogDiagnostics,
) : ClientLogRepository {
    private val sendMutex = Mutex()
    private val withdrawals = AtomicReference(Withdrawals())
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
        var pruned: ClientLogPruneResult? = null
        store.updateData { previous ->
            val (updated, event) = previous.append(draft)
            appended = event
            updated.prune(draft.occurredAtMillis).also { pruned = it }.store
        }
        pruned?.let { logPruning(it) }
        checkNotNull(appended)
    }

    override suspend fun pendingCount(): Int = store.data.first().events.size

    override suspend fun storedTokenOwner(): String? = runSuspendCatching {
        tokenRepository.getAccessToken().first().tokenSubject()
            ?: tokenRepository.getRefreshToken().first().tokenSubject()
    }.getOrNull()

    override fun beginWithdrawal() {
        withdrawals.updateAndGet { it.copy(pendingCount = it.pendingCount + 1) }
    }

    override fun endWithdrawal() {
        withdrawals.updateAndGet { it.copy(pendingCount = it.pendingCount - 1) }
    }

    override suspend fun deleteUserEvents(userId: String, withdrawalEventId: String): Result<Unit> = runSuspendCatching {
        // Keep older events ineligible even if the disk cleanup fails.
        withdrawals.updateAndGet { it.copy(retainedEvents = it.retainedEvents + (userId to withdrawalEventId)) }
        var removedCount = 0
        store.updateData { previous ->
            val retained = previous.events.filterNot { it.userIdAtOccurrence == userId && it.eventId != withdrawalEventId }
            removedCount = previous.events.size - retained.size
            previous.copy(events = retained)
        }
        diagnostics.removed("withdrawal", removedCount)
    }

    override suspend fun flush(ignoreBackoff: Boolean): Result<Unit> = sendMutex.withLock {
        if (!ignoreBackoff && System.currentTimeMillis() < retryAtMillis) return@withLock Result.success(Unit)
        if (withdrawals.get().pendingCount > 0) return@withLock Result.success(Unit)
        val result = runSuspendCatching {
            val pendingCleanup = tokenRepository.pendingClientLogWithdrawals().first()
            if (pendingCleanup.isNotEmpty()) {
                for (withdrawal in pendingCleanup) {
                    deleteUserEvents(withdrawal.userId, withdrawal.eventId).getOrThrow()
                }
                tokenRepository.clearClientLogWithdrawals(pendingCleanup)
            }
            var pruned: ClientLogPruneResult? = null
            store.updateData { it.prune(System.currentTimeMillis()).also { result -> pruned = result }.store }
            pruned?.let { logPruning(it) }
            while (true) {
                val withdrawal = withdrawals.get()
                if (withdrawal.pendingCount > 0) return@runSuspendCatching
                // A token the server rejected once is not attached again until it changes.
                val accessToken = tokenRepository.getAccessToken().first().takeIf { it.isNotBlank() && it != blockedAccessToken }
                val currentUserId = userId
                // Tokens are saved before the user ID is restored; hold account events until the owner is known.
                val ownerUnknown = accessToken != null && currentUserId == null
                val events = store.data.first().events.dropWhile {
                    !withdrawal.allows(it) || (ownerUnknown && it.userIdAtOccurrence != null)
                }
                if (events.isEmpty()) break
                val owner = events.first().userIdAtOccurrence
                val batch = selectBatch(events.takeWhile { it.userIdAtOccurrence == owner && withdrawal.allows(it) })
                sendBatch(batch, accessToken)
            }
            retryCount = 0
            retryAtMillis = 0
        }.onFailure {
            val cap = min(MAX_RETRY_MILLIS, 1_000L shl min(retryCount++, 9))
            // Up to 10% jitter, as on iOS, so devices do not retry at the same moment.
            retryAtMillis = System.currentTimeMillis() + cap + Random.nextLong(cap / 10 + 1)
            Timber.w("Client log delivery deferred (%s)", it.javaClass.simpleName)
        }
        logStatus()
        result
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
        val withdrawal = withdrawals.get()
        if (withdrawal.pendingCount > 0) return
        // Withdrawal can remove queued logs while an earlier request is in flight.
        val queuedIds = store.data.first().events.mapTo(mutableSetOf()) { it.eventId }
        val pending = events.filter { it.eventId in queuedIds && withdrawal.allows(it) }
        if (pending.isEmpty()) return

        val owner = pending.first().userIdAtOccurrence
        // The account and stored token can change independently. Match the token's subject as well.
        val requestToken = accessToken?.takeIf {
            owner != null && owner == userId && it != blockedAccessToken && it.tokenSubject() == owner
        }
        if (withdrawals.get() !== withdrawal) return
        try {
            val response = service.send(ClientLogRequest(platform = "ANDROID", logs = pending), requestToken)
            check(response.resultCode == "D-0") { "Unexpected client log response" }
            remove(pending)
        } catch (e: ResponseException) {
            when (e.response.status.value) {
                // The server also answers unexpected failures with 400, so only D-01 means a malformed event.
                400 -> if (e.resultCode() == INVALID_PARAMETER_CODE) splitOrDiscard(pending, requestToken, "invalid") else throw e
                413 -> splitOrDiscard(pending, requestToken, "oversized")
                // Authorization is optional: drop the rejected token and resend the same logs right away.
                401, 403 -> {
                    if (requestToken == null) error("Anonymous client log request was rejected")
                    blockedAccessToken = requestToken
                    Timber.w("Client log token was rejected; sending without it")
                    sendBatch(pending, null)
                }
                else -> throw e
            }
        }
    }

    private fun String.tokenSubject(): String? {
        val segments = split('.', limit = 4)
        if (segments.size != 3) return null
        return runCatching {
            val payload = Base64.getUrlDecoder().decode(segments[1]).decodeToString()
            clientLogJson.parseToJsonElement(payload).jsonObject["sub"]?.jsonPrimitive?.contentOrNull
        }.getOrNull()
    }

    private suspend fun splitOrDiscard(events: List<ClientLogEvent>, accessToken: String?, reason: String) {
        if (events.size == 1) {
            diagnostics.removed(reason, remove(events))
        } else {
            val middle = events.size / 2
            sendBatch(events.take(middle), accessToken)
            sendBatch(events.drop(middle), accessToken)
        }
    }

    private suspend fun ResponseException.resultCode(): String? = runSuspendCatching {
        clientLogJson.decodeFromString<BasicNullableResponse<Unit>>(response.bodyAsText()).resultCode
    }.getOrNull()

    private suspend fun remove(events: List<ClientLogEvent>): Int {
        val ids = events.map { it.eventId }.toSet()
        var removedCount = 0
        store.updateData { previous ->
            val retained = previous.events.filterNot { it.eventId in ids }
            removedCount = previous.events.size - retained.size
            previous.copy(events = retained)
        }
        return removedCount
    }

    private fun logPruning(result: ClientLogPruneResult) {
        diagnostics.removed("expired", result.expired)
        diagnostics.removed("over_count", result.overCount)
        diagnostics.removed("over_size", result.overSize)
    }

    private suspend fun logStatus() {
        runSuspendCatching {
            val events = store.data.first().events
            val oldest = events.minOfOrNull { Instant.parse(it.occurredAt).toEpochMilli() }
            val ageSeconds = oldest?.let { ((System.currentTimeMillis() - it) / 1_000).coerceAtLeast(0) } ?: 0
            diagnostics.status(events.size, ageSeconds, retryCount)
        }.onFailure { Timber.w("Client log queue status unavailable (%s)", it.javaClass.simpleName) }
    }

    private data class Withdrawals(
        val pendingCount: Int = 0,
        val retainedEvents: Map<String, String> = emptyMap(),
    ) {
        fun allows(event: ClientLogEvent): Boolean {
            val retainedId = event.userIdAtOccurrence?.let(retainedEvents::get) ?: return true
            return event.eventId == retainedId
        }
    }
}

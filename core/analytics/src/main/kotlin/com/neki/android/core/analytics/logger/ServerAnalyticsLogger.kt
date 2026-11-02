package com.neki.android.core.analytics.logger

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.neki.android.core.analytics.event.AnalyticsEvent
import com.neki.android.core.analytics.server.toJsonObject
import com.neki.android.core.dataapi.repository.ClientLogRepository
import com.neki.android.core.model.analytics.ClientLogDraft
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.JsonObject
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.cancellation.CancellationException

/**
 * Queues occurrence snapshots for server delivery; network delivery runs independently of disk writes.
 * Repository and app info are created lazily on the IO scope to keep them off app startup.
 */
internal class ServerAnalyticsLogger(
    private val amplitudeClient: AmplitudeAnalyticsClient,
    private val sessions: AmplitudeSessionRegistry,
    lazyRepository: Lazy<ClientLogRepository>,
    lazyAppVersion: Lazy<String>,
    lazyContext: Lazy<JsonObject>,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
    private val now: () -> Long = System::currentTimeMillis,
) : AnalyticsLogger, DefaultLifecycleObserver {
    private val commands = Channel<Command>(Channel.UNLIMITED)
    private val wakeSender = Channel<Unit>(Channel.CONFLATED)
    private val pendingCommands = AtomicInteger()
    private val repository by lazyRepository
    private val appVersion by lazyAppVersion
    private val context by lazyContext
    private var userId: String? = null

    // Until login restores the user, use the ID Amplitude persisted from the previous launch, as Amplitude does.
    private var userKnown = false
    private val restoredUserId = scope.async { amplitudeClient.awaitUserId() }

    init {
        scope.launch {
            for (command in commands) {
                try {
                    process(command)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Timber.w("Client log command failed (%s)", e.javaClass.simpleName)
                    if (command is Command.Barrier) command.done.complete(Unit)
                }
            }
        }
        scope.launch {
            while (true) {
                val retryDelay = (repository.retryAtMillis - now()).coerceAtLeast(0)
                val waitMillis = if (retryDelay > 0) retryDelay else FLUSH_INTERVAL_MILLIS
                withTimeoutOrNull(waitMillis) { wakeSender.receive() }
                repository.flush()
            }
        }
    }

    fun initialize() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun log(event: AnalyticsEvent) {
        log(event, UUID.randomUUID().toString(), now())
    }

    @Synchronized
    fun log(event: AnalyticsEvent, eventId: String, occurredAt: Long) {
        if (pendingCommands.incrementAndGet() > MAX_PENDING_COMMANDS) {
            pendingCommands.decrementAndGet()
            sessions.cancel(eventId)
            Timber.w("Client log command queue is full")
            return
        }
        val properties = runCatching { event.params.toJsonObject() }.getOrElse {
            pendingCommands.decrementAndGet()
            sessions.cancel(eventId)
            return
        }
        commands.trySend(Command.Log(event.name, properties, eventId, occurredAt, userId, userKnown))
    }

    @Synchronized
    override fun setUserId(userId: String) {
        this.userId = userId
        userKnown = true
        commands.trySend(Command.User(userId))
    }

    @Synchronized
    override fun clearUserId() {
        userId = null
        userKnown = true
        commands.trySend(Command.User(null))
    }

    override suspend fun flush() {
        // Logout/withdraw should only wait for a bounded best-effort attempt.
        withTimeoutOrNull(BOUNDARY_FLUSH_MILLIS) {
            val done = CompletableDeferred<Unit>()
            commands.send(Command.Barrier(done))
            done.await()
            repository.flush()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        wakeSender.trySend(Unit)
    }

    private suspend fun process(command: Command) {
        when (command) {
            is Command.Log -> {
                try {
                    record(command)
                    if (repository.pendingCount() >= FLUSH_COUNT) wakeSender.trySend(Unit)
                } finally {
                    pendingCommands.decrementAndGet()
                }
            }
            is Command.User -> {
                repository.setUserId(command.userId)
                if (command.userId != null) wakeSender.trySend(Unit)
            }
            is Command.Barrier -> command.done.complete(Unit)
        }
    }

    private suspend fun record(command: Command.Log) {
        // Fall back to Amplitude's current session when it did not report this event in time.
        val sessionId = sessions.await(command.id)
            ?: amplitudeClient.currentSessionId().takeIf { it > 0 }
            ?: command.timestamp
        val draft = ClientLogDraft(
            eventId = command.id,
            eventType = command.name,
            eventProperties = command.properties,
            occurredAtMillis = command.timestamp,
            sessionId = sessionId,
            deviceId = amplitudeClient.awaitDeviceId(),
            userIdAtOccurrence = if (command.userKnown) command.userId else restoredUserId.await(),
            appVersion = appVersion,
            context = context,
        )
        repository.enqueue(draft)
            .onFailure { Timber.w("Client log could not be persisted (%s)", it.javaClass.simpleName) }
    }

    private sealed interface Command {
        data class Log(
            val name: String,
            val properties: JsonObject,
            val id: String,
            val timestamp: Long,
            val userId: String?,
            val userKnown: Boolean,
        ) : Command
        data class User(val userId: String?) : Command
        data class Barrier(val done: CompletableDeferred<Unit>) : Command
    }

    private companion object {
        const val FLUSH_COUNT = 100
        const val FLUSH_INTERVAL_MILLIS = 30_000L
        const val BOUNDARY_FLUSH_MILLIS = 1_500L
        const val MAX_PENDING_COMMANDS = 5_000
    }
}

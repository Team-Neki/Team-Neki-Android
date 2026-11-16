package com.neki.android.core.data.sync

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.neki.android.core.common.coroutine.di.ApplicationScope
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.PendingUpdate
import com.neki.android.core.dataapi.repository.OptimisticUpdateScheduler
import com.neki.android.core.dataapi.repository.UpdateKind
import com.neki.android.core.dataapi.repository.UpdateRejectedException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class WorkManagerOptimisticUpdateScheduler @Inject constructor(
    private val workManager: WorkManager,
    private val ledger: UpdateLedger,
    @ApplicationScope private val scope: CoroutineScope,
) : OptimisticUpdateScheduler {
    private val versions = ConcurrentHashMap<String, AtomicLong>()
    private val enqueueMutex = Mutex()
    private val latest = mutableMapOf<String, UUID>()

    private suspend fun enqueue(update: PendingUpdate): Result<UUID> = scope.async(start = CoroutineStart.UNDISPATCHED) {
        runSuspendCatching {
            enqueueMutex.withLock {
                val name = "${update.kind}:${update.targetId}"
                update.previousValue?.let { ledger.seed(name, it) }
                val request = OneTimeWorkRequestBuilder<UpdateWorker>()
                    .setInputData(workDataOf(
                        "kind" to update.kind.name,
                        "target" to update.targetId,
                        "value" to update.value,
                    ))
                    .setInitialDelay(update.delayMillis, TimeUnit.MILLISECONDS)
                    .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                    .addTag("pending-save")
                    .build()
                try {
                    withContext(Dispatchers.IO) {
                        workManager.enqueueUniqueWork(name, ExistingWorkPolicy.REPLACE, request).result.get()
                    }
                } catch (e: CancellationException) {
                    Timber.tag("OptimisticUpdate").d("등록 취소 [%s]", name)
                    throw e
                } catch (e: Exception) {
                    Timber.tag("OptimisticUpdate").d("등록 실패 [%s] %s", name, e.javaClass.simpleName)
                    throw UpdateRejectedException(ledger.value(name)).apply { initCause(e) }
                }
                latest[name] = request.id
                Timber.tag("OptimisticUpdate").d("등록 [%s] 작업=%s", name, request.id.toString().take(8))
                request.id
            }
        }
    }.await()

    private fun observe(workId: UUID): Flow<WorkInfo.State?> =
        workManager.getWorkInfoByIdFlow(workId).map { it?.state }

    override suspend fun update(update: PendingUpdate): Result<Unit> {
        val name = "${update.kind}:${update.targetId}"
        val version = versions.computeIfAbsent(name) { AtomicLong() }
        val ticket = version.incrementAndGet()
        Timber.tag("OptimisticUpdate").d(
            "요청 [%s] #%d 값=%s",
            name,
            ticket,
            if (update.kind == UpdateKind.MEMO) "${update.value.length}자" else update.value,
        )
        val id = enqueue(update).getOrElse {
            if (version.get() != ticket) {
                Timber.tag("OptimisticUpdate").d("이전 요청 무시 [%s] #%d", name, ticket)
                throw CancellationException("Update replaced")
            }
            return Result.failure(it)
        }
        val status = observe(id).first {
            it == null || it.isFinished
        }
        return enqueueMutex.withLock {
            if (version.get() != ticket || latest[name] != id || status == null || status == WorkInfo.State.CANCELLED) {
                Timber.tag("OptimisticUpdate").d("이전·취소 요청 무시 [%s] #%d", name, ticket)
                throw CancellationException("Update replaced or cancelled")
            }
            if (status == WorkInfo.State.SUCCEEDED) {
                Timber.tag("OptimisticUpdate").d("반영 완료 [%s] #%d", name, ticket)
                Result.success(Unit)
            } else {
                val committedValue = ledger.value(name)
                Timber.tag("OptimisticUpdate").d(
                    "실패 [%s] #%d 복구값=%s",
                    name,
                    ticket,
                    if (update.kind == UpdateKind.MEMO) committedValue?.let { "${it.length}자" } else committedValue,
                )
                Result.failure(UpdateRejectedException(committedValue))
            }
        }
    }
}

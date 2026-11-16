package com.neki.android.core.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.dataapi.repository.PoseRepository
import com.neki.android.core.dataapi.repository.MapRepository
import com.neki.android.core.dataapi.repository.TermRepository
import com.neki.android.core.dataapi.repository.UpdateKind
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import timber.log.Timber

@HiltWorker
class UpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted parameters: WorkerParameters,
    private val photos: PhotoRepository,
    private val poses: PoseRepository,
    private val booths: MapRepository,
    private val terms: TermRepository,
    private val ledger: UpdateLedger,
) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        val target = inputData.getString("target") ?: return Result.failure()
        val value = inputData.getString("value") ?: return Result.failure()
        val kind = inputData.getString("kind")?.let { raw -> UpdateKind.entries.find { it.name == raw } }
            ?: return Result.failure()
        Timber.tag("OptimisticUpdate").d("저장 시작 [%s:%s] 작업=%s", kind, target, id.toString().take(8))
        return try {
            val result = when (kind) {
                UpdateKind.PHOTO_FAVORITE -> photos.updateFavorite(target.toLong(), value.toBooleanStrict())
                UpdateKind.POSE_BOOKMARK -> poses.updateBookmark(target.toLong(), value.toBooleanStrict())
                UpdateKind.BOOTH_FAVORITE -> booths.updatePhotoBoothFavorite(target, value.toBooleanStrict())
                UpdateKind.MARKETING -> terms.updateTermAgreement(target.toLong(), value.toBooleanStrict())
                UpdateKind.MEMO -> photos.updateMemo(target.toLong(), value)
            }
            result.getOrThrow()
            ledger.commit("$kind:$target", value)
            Timber.tag("OptimisticUpdate").d("저장 완료 작업=%s", id.toString().take(8))
            Result.success()
        } catch (e: CancellationException) {
            Timber.tag("OptimisticUpdate").d("저장 취소 작업=%s", id.toString().take(8))
            throw e
        } catch (e: Exception) {
            Timber.tag("OptimisticUpdate").e(e, "저장 실패 작업=%s", id.toString().take(8))
            Result.failure()
        }
    }
}

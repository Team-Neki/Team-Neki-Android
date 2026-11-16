package com.neki.android.core.dataapi.repository

enum class UpdateKind { PHOTO_FAVORITE, POSE_BOOKMARK, BOOTH_FAVORITE, MARKETING, MEMO }

data class PendingUpdate(
    val kind: UpdateKind,
    val targetId: String,
    val value: String,
    val previousValue: String? = null,
)

/** Once enqueued, an update survives the observing screen. Cancellation never means rejection. */
interface OptimisticUpdateScheduler {
    suspend fun update(update: PendingUpdate): Result<Unit>
}

class UpdateRejectedException(val committedValue: String?) : IllegalStateException("Update rejected")

fun Throwable.committedBoolean(fallback: Boolean): Boolean =
    (this as? UpdateRejectedException)?.committedValue?.toBooleanStrictOrNull() ?: fallback

fun Throwable.committedText(fallback: String): String =
    (this as? UpdateRejectedException)?.committedValue ?: fallback

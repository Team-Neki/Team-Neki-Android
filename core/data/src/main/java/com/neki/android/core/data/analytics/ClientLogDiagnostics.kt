package com.neki.android.core.data.analytics

import android.util.Log
import javax.inject.Inject

/** Non-identifying queue diagnostics remain available in Release, independently of the app's Timber trees. */
internal class ClientLogDiagnostics(
    private val write: (Int, String) -> Unit,
) {
    @Inject
    constructor() : this({ priority, message ->
        Log.println(priority, "ClientLog", message)
        Unit
    })

    fun removed(reason: String, count: Int) {
        if (count > 0) write(Log.WARN, "Removed client logs: reason=$reason, count=$count")
    }

    fun corrupted() {
        // A damaged queue cannot be decoded, so the number of lost events is unknown.
        write(Log.WARN, "Replacing corrupted client log queue: lostCount=unknown")
    }

    fun status(pending: Int, oldestAgeSeconds: Long, retryCount: Int) {
        write(Log.DEBUG, "Client log queue: pending=$pending, oldestAgeSeconds=$oldestAgeSeconds, retryCount=$retryCount")
    }
}

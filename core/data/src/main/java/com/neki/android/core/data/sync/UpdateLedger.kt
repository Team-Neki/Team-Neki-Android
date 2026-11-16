package com.neki.android.core.data.sync

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/** Only committed rollback values live here; pending requests are persisted by WorkManager. */
@Singleton
class UpdateLedger @Inject constructor() {
    private val committed = ConcurrentHashMap<String, String>()

    fun seed(key: String, value: String) {
        committed.putIfAbsent(key, value)
    }

    fun commit(key: String, value: String) {
        committed[key] = value
    }

    fun value(key: String): String? = committed[key]
}

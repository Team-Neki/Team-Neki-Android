package com.neki.android.core.data.local.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.neki.android.core.data.analytics.ClientLogDiagnostics
import com.neki.android.core.data.local.model.ClientLogStore
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

internal object ClientLogStoreSerializer : Serializer<ClientLogStore> {
    override val defaultValue = ClientLogStore()

    fun corruptionHandler(diagnostics: ClientLogDiagnostics) = ReplaceFileCorruptionHandler {
        diagnostics.corrupted()
        defaultValue
    }

    // Queues written before the session moved to Amplitude still carry `lastActiveAt`.
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun readFrom(input: InputStream): ClientLogStore = try {
        json.decodeFromString<ClientLogStore>(input.readBytes().decodeToString())
    } catch (e: SerializationException) {
        throw CorruptionException("Cannot read client log queue", e)
    }

    override suspend fun writeTo(t: ClientLogStore, output: OutputStream) {
        output.write(json.encodeToString(t).toByteArray())
    }
}

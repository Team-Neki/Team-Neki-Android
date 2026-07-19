package com.neki.android.core.data.local.serializer

import androidx.datastore.core.Serializer
import com.neki.android.core.data.local.model.MarketingPopupRecord
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

internal object MarketingPopupRecordSerializer : Serializer<MarketingPopupRecord> {

    override val defaultValue: MarketingPopupRecord = MarketingPopupRecord()

    override suspend fun readFrom(input: InputStream): MarketingPopupRecord =
        try {
            Json.decodeFromString(
                deserializer = MarketingPopupRecord.serializer(),
                string = input.readBytes().decodeToString(),
            )
        } catch (e: SerializationException) {
            defaultValue
        }

    override suspend fun writeTo(t: MarketingPopupRecord, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = MarketingPopupRecord.serializer(),
                value = t,
            ).encodeToByteArray(),
        )
    }
}

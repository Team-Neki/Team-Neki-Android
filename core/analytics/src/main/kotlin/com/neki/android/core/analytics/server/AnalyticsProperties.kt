package com.neki.android.core.analytics.server

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal fun Map<String, Any?>.toJsonObject(): JsonObject = JsonObject(mapValues { (_, value) -> value.toJsonElement() })

private fun Any?.toJsonElement(): JsonElement = when (this) {
    null -> JsonNull
    is String -> JsonPrimitive(this)
    is Boolean -> JsonPrimitive(this)
    is Number -> {
        require(toDouble().isFinite()) { "Non-finite analytics number" }
        JsonPrimitive(this)
    }
    is Map<*, *> -> JsonObject(entries.associate { (key, value) ->
        require(key is String) { "Analytics property keys must be strings" }
        key to value.toJsonElement()
    })
    is Iterable<*> -> JsonArray(map { it.toJsonElement() })
    is Array<*> -> JsonArray(map { it.toJsonElement() })
    else -> error("Unsupported analytics property type")
}

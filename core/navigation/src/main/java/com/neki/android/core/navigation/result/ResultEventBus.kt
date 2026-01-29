package com.neki.android.core.navigation.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Local for receiving results in a [ResultEventBus]
 */
object LocalResultEventBus {
    private val mLocalResultEventBus: ProvidableCompositionLocal<ResultEventBus?> =
        compositionLocalOf { null }

    val current: ResultEventBus
        @Composable
        get() = mLocalResultEventBus.current ?: error("No ResultEventBus has been provided")

    infix fun provides(
        bus: ResultEventBus,
    ): ProvidedValue<ResultEventBus?> {
        return mLocalResultEventBus.provides(bus)
    }
}

/**
 * An EventBus for passing results between multiple sets of screens.
 *
 * It provides a solution for event based results.
 */
// https://github.com/android/nav3-recipes/blob/main/app/src/main/java/com/example/nav3recipes/results/event/README.md
class ResultEventBus {
    val channelMap: MutableMap<String, Channel<Any?>> = mutableMapOf()

    inline fun <reified T> getResultFlow(resultKey: String = T::class.toString()) =
        channelMap[resultKey]?.receiveAsFlow()

    inline fun <reified T> sendResult(
        resultKey: String = T::class.toString(),
        result: T,
        allowDuplicate: Boolean = true,
    ) {
        if (!channelMap.contains(resultKey)) {
            channelMap[resultKey] = if (allowDuplicate) {
                Channel(capacity = BUFFERED, onBufferOverflow = BufferOverflow.SUSPEND)
            } else {
                Channel(capacity = Channel.CONFLATED)
            }
        }
        channelMap[resultKey]?.trySend(result)
    }

    inline fun <reified T> removeResult(resultKey: String = T::class.toString()) {
        channelMap.remove(resultKey)
    }
}

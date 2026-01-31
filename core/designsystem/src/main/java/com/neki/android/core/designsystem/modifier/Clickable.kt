package com.neki.android.core.designsystem.modifier

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material3.ripple
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * 클릭의 리플 효과를 없애주는 [Modifier]
 */
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = this.clickable(
    indication = null,
    interactionSource = null,
    onClick = onClick,
)

/**
 * 클릭의 리플 효과를 없애고 500ms 내에 중복 클릭을 막아주는 [Modifier]
 */
fun Modifier.noRippleClickableSingle(
    enabled: Boolean = true,
    onClick: () -> Unit,
): Modifier = this.then(
    ClickableSingleElement(
        enabled = enabled,
        onClick = onClick,
        indicationNodeFactory = null,
    ),
)

/**
 * 500ms 내의 중복 클릭을 막아주는 [Modifier]
 */
fun Modifier.clickableSingle(
    enabled: Boolean = true,
    onClick: () -> Unit,
): Modifier = this.then(
    ClickableSingleElement(
        enabled = enabled,
        onClick = onClick,
        indicationNodeFactory = ripple(),
    ),
)

private data class ClickableSingleElement(
    private val enabled: Boolean,
    private val onClick: () -> Unit,
    private val indicationNodeFactory: IndicationNodeFactory?,
) : ModifierNodeElement<ClickableSingleNode>() {

    override fun create(): ClickableSingleNode = ClickableSingleNode(
        enabled = enabled,
        onClick = onClick,
        indicationNodeFactory = indicationNodeFactory,
    )

    override fun update(node: ClickableSingleNode) {
        node.update(
            enabled = enabled,
            onClick = onClick,
            indicationNodeFactory = indicationNodeFactory,
        )
    }
}

private class ClickableSingleNode(
    private var enabled: Boolean,
    private var onClick: () -> Unit,
    private var indicationNodeFactory: IndicationNodeFactory?,
) : DelegatingNode(), PointerInputModifierNode {

    private var lastClickTime = 0L
    private var interactionSource: MutableInteractionSource? = null
    private var indicationNode: DelegatableNode? = null

    override val shouldAutoInvalidate: Boolean = false

    private val pointerInputNode = delegate(
        SuspendingPointerInputModifierNode {
            detectTapGestures(
                onPress = { offset -> if (enabled) handlePressInteraction(offset) },
                onTap = { if (enabled) processClick() },
            )
        },
    )

    override fun onAttach() {
        initializeIndicationIfNeeded()
    }

    private fun initializeIndicationIfNeeded() {
        if (indicationNode != null) return
        indicationNodeFactory?.let { factory ->
            if (interactionSource == null) {
                interactionSource = MutableInteractionSource()
            }
            val node = factory.create(interactionSource!!)
            delegate(node)
            indicationNode = node
        }
    }

    private suspend fun PressGestureScope.handlePressInteraction(offset: Offset) {
        initializeIndicationIfNeeded()
        interactionSource?.let { source ->
            coroutineScope {
                val press = PressInteraction.Press(offset)
                launch { source.emit(press) }

                val success = tryAwaitRelease()
                val endInteraction = if (success) {
                    PressInteraction.Release(press)
                } else {
                    PressInteraction.Cancel(press)
                }
                launch { source.emit(endInteraction) }
            }
        }
    }

    private fun processClick() {
        val now = System.currentTimeMillis()
        if (now - lastClickTime >= DEBOUNCE_TIME_MS) {
            lastClickTime = now
            onClick()
        }
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize,
    ) {
        pointerInputNode.onPointerEvent(pointerEvent, pass, bounds)
    }

    override fun onCancelPointerInput() {
        pointerInputNode.onCancelPointerInput()
    }

    fun update(
        enabled: Boolean,
        onClick: () -> Unit,
        indicationNodeFactory: IndicationNodeFactory?,
    ) {
        val indicationChanged = this.indicationNodeFactory != indicationNodeFactory
        this.enabled = enabled
        this.onClick = onClick

        if (indicationChanged) {
            indicationNode?.let { undelegate(it) }
            indicationNode = null
            interactionSource = null
            this.indicationNodeFactory = indicationNodeFactory
            initializeIndicationIfNeeded()
        }
    }

    companion object {
        private const val DEBOUNCE_TIME_MS = 500L
    }
}

/**
 * 중복 클릭 방지를 위한 인터페이스
 * Button, IconButton 등 Composable 컴포넌트에서 사용
 */
interface MultipleEventsCutter {
    fun processEvent(event: () -> Unit)

    companion object
}

fun MultipleEventsCutter.Companion.get(): MultipleEventsCutter = MultipleEventsCutterImpl()

private class MultipleEventsCutterImpl : MultipleEventsCutter {
    private val now: Long
        get() = System.currentTimeMillis()

    private var lastEventTimeMs: Long = 0

    override fun processEvent(event: () -> Unit) {
        if (now - lastEventTimeMs >= DEBOUNCE_TIME_MS) {
            lastEventTimeMs = now
            event.invoke()
        }
    }

    companion object {
        private const val DEBOUNCE_TIME_MS = 500L
    }
}

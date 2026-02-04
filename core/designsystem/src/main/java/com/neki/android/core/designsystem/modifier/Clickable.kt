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
import androidx.compose.ui.node.SemanticsModifierNode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
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
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier = this.then(
    ClickableSingleElement(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick,
    ),
)

/**
 * 500ms 내의 중복 클릭을 막아주는 [Modifier]
 */
fun Modifier.clickableSingle(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    interactionSource: MutableInteractionSource? = null,
    onClick: () -> Unit,
): Modifier = this.then(
    ClickableSingleElement(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick,
        indicationNodeFactory = ripple(),
        interactionSource = interactionSource,
    ),
)

private data class ClickableSingleElement(
    private val enabled: Boolean,
    private val onClickLabel: String?,
    private val role: Role?,
    private val indicationNodeFactory: IndicationNodeFactory? = null,
    private val interactionSource: MutableInteractionSource? = null,
    private val onClick: () -> Unit,
) : ModifierNodeElement<ClickableSingleNode>() {

    override fun create(): ClickableSingleNode = ClickableSingleNode(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick,
        indicationNodeFactory = indicationNodeFactory,
        interactionSource = interactionSource,
    )

    override fun update(node: ClickableSingleNode) = node.update(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick,
        indicationNodeFactory = indicationNodeFactory,
        interactionSource = interactionSource,
    )
}

private class ClickableSingleNode(
    private var enabled: Boolean,
    private var onClickLabel: String?,
    private var role: Role?,
    private var indicationNodeFactory: IndicationNodeFactory?,
    private var interactionSource: MutableInteractionSource?,
    private var onClick: () -> Unit,
) : DelegatingNode(), PointerInputModifierNode, SemanticsModifierNode {

    private val multipleEventsCutter = MultipleEventsCutter.get()
    private var internalInteractionSource: MutableInteractionSource? = null
    private var indicationNode: DelegatableNode? = null
    private var currentPressInteraction: PressInteraction.Press? = null

    private val activeInteractionSource: MutableInteractionSource?
        get() = interactionSource ?: internalInteractionSource

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
            if (interactionSource == null && internalInteractionSource == null) {
                internalInteractionSource = MutableInteractionSource()
            }
            val source = activeInteractionSource ?: return@let
            val node = factory.create(source)
            delegate(node)
            indicationNode = node
        }
    }

    private suspend fun PressGestureScope.handlePressInteraction(offset: Offset) {
        initializeIndicationIfNeeded()
        activeInteractionSource?.let { source ->
            coroutineScope {
                val press = PressInteraction.Press(offset)
                currentPressInteraction = press
                launch { source.emit(press) }

                val success = tryAwaitRelease()
                currentPressInteraction = null
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
        multipleEventsCutter.processEvent { onClick() }
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

    override fun SemanticsPropertyReceiver.applySemantics() {
        this@ClickableSingleNode.role?.let { this.role = it }
        onClick(
            label = onClickLabel,
            action = { processClick(); true },
        )
        if (!enabled) { disabled() }
    }

    fun update(
        enabled: Boolean,
        onClickLabel: String?,
        role: Role?,
        indicationNodeFactory: IndicationNodeFactory?,
        interactionSource: MutableInteractionSource?,
        onClick: () -> Unit,
    ) {
        val interactionSourceChanged = this.interactionSource != interactionSource
        val indicationChanged = this.indicationNodeFactory != indicationNodeFactory

        this.enabled = enabled
        this.onClickLabel = onClickLabel
        this.role = role
        this.onClick = onClick

        if (interactionSourceChanged) {
            this.interactionSource = interactionSource
        }

        if (indicationChanged || interactionSourceChanged) {
            indicationNode?.let { undelegate(it) }
            indicationNode = null
            if (interactionSource == null) {
                internalInteractionSource = null
            }
            this.indicationNodeFactory = indicationNodeFactory
            initializeIndicationIfNeeded()
        }
    }

    override fun onDetach() {
        currentPressInteraction?.let { press ->
            activeInteractionSource?.tryEmit(PressInteraction.Cancel(press))
        }
        currentPressInteraction = null
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

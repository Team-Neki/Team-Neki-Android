package com.neki.android.feature.map.impl.util

import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.neki.android.feature.map.impl.DragLevel

/** 일반 지도와 검색 결과 패널이 같은 드래그 임계값과 애니메이션을 사용한다. */
@Composable
internal fun rememberAnchoredPanelState(
    initialValue: DragLevel,
    anchors: DraggableAnchors<DragLevel>,
    confirmValueChange: (DragLevel) -> Boolean = { true },
): AnchoredDraggableState<DragLevel> {
    val density = LocalDensity.current
    val currentConfirmValueChange by rememberUpdatedState(confirmValueChange)
    val state = remember(density) {
        AnchoredDraggableState(
            initialValue = initialValue,
            anchors = anchors,
            positionalThreshold = { distance -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = tween(),
            decayAnimationSpec = splineBasedDecay(density),
            confirmValueChange = { currentConfirmValueChange(it) },
        )
    }
    SideEffect { state.updateAnchors(anchors, state.targetValue) }
    return state
}

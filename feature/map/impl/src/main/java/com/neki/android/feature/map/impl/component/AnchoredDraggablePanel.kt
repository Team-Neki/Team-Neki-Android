package com.neki.android.feature.map.impl.component

import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.neki.android.feature.map.impl.DragValue
import kotlin.math.roundToInt

@Composable
fun AnchoredDraggablePanel() {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenHeightPx = with(density) {
        configuration.screenHeightDp.dp.toPx()
    }

    val anchors = DraggableAnchors {
        DragValue.Start at screenHeightPx * 0.8f   // 20%
        DragValue.Center at screenHeightPx * 0.5f  // 50%
        DragValue.End at screenHeightPx * 0.2f     // 80%
    }

    val state = remember {
        AnchoredDraggableState(
            initialValue = DragValue.Start,
            anchors = anchors,
            positionalThreshold = { distance -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = tween(),
            decayAnimationSpec = splineBasedDecay(density),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset {
                IntOffset(0, state.requireOffset().roundToInt())
            }
            .anchoredDraggable(
                state = state,
                orientation = Orientation.Vertical,
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                )
                .padding(20.dp),
        ) {
            Text(
                text = "시트의 내용",
                color = Color.Black,
            )
        }
    }
}

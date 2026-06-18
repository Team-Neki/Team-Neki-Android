package com.neki.android.feature.pose.impl.detail.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import net.engawapg.lib.zoomable.ScrollGesturePropagation
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
internal fun PoseDetailImageItem(
    imageUrl: String?,
) {
    val zoomState = rememberZoomState()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .zoomable(
                    zoomState = zoomState,
                    scrollGesturePropagation = ScrollGesturePropagation.ContentEdge,
                ),
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            onSuccess = { state -> zoomState.setContentSize(state.painter.intrinsicSize) },
        )
    }
}

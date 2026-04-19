package com.neki.android.feature.archive.impl.photo_detail.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import coil3.compose.AsyncImage
import net.engawapg.lib.zoomable.ScrollGesturePropagation
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
internal fun PhotoDetailImageItem(
    imageUrl: String?,
    isScrollInProgress: Boolean,
    isTapEnabled: Boolean,
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit,
) {
    val zoomState = rememberZoomState()
    var contentWidth by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { contentWidth = it.width }
                .zoomable(
                    zoomState = zoomState,
                    scrollGesturePropagation = ScrollGesturePropagation.ContentEdge,
                    onTap = { position: Offset ->
                        if (
                            isScrollInProgress && contentWidth > 0 &&
                            zoomState.scale <= 1f &&
                            isTapEnabled
                        ) {
                            if (position.x < contentWidth / 2) {
                                onClickLeft()
                            } else {
                                onClickRight()
                            }
                        }
                    },
                ),
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            onSuccess = { state -> zoomState.setContentSize(state.painter.intrinsicSize) },
        )
    }
}

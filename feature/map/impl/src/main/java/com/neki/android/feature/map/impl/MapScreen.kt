package com.neki.android.feature.map.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.NaverMap
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.map.impl.component.AnchoredDraggablePanel

enum class DragValue { Start, Center, End }

@Composable
fun MapRoute(
    modifier: Modifier = Modifier,
) {
    MapScreen()
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
        )

        AnchoredDraggablePanel()
    }
}

@Preview
@Composable
private fun MapScreenPreview() {
    NekiTheme {
        MapScreen()
    }
}

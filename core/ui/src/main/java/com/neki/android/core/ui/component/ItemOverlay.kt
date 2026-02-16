package com.neki.android.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun GridItemOverlay(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
) {
    Box(
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.04f),
                shape = shape,
            )
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to Color.Black.copy(alpha = 0.2f),
                        134f / 242f to Color.Black.copy(alpha = 0f),
                    ),
                ),
                shape = shape,
            ),
    )
}

@Composable
fun SelectedPhotoGridItemOverlay(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
) {
    Box(
        modifier = modifier
            .border(
                width = 2.dp,
                color = NekiTheme.colorScheme.primary400,
                shape = shape,
            )
            .background(
                color = Color.Black.copy(alpha = 0.2f),
                shape = shape,
            ),
    )
}

@ComponentPreview
@Composable
private fun GridItemOverlayPreview() {
    NekiTheme {
        GridItemOverlay(
            modifier = Modifier
                .size(80.dp),
        )
    }
}

@ComponentPreview
@Composable
private fun SelectedPhotoGridItemOverlayPreview() {
    NekiTheme {
        SelectedPhotoGridItemOverlay(
            modifier = Modifier
                .size(80.dp),
        )
    }
}

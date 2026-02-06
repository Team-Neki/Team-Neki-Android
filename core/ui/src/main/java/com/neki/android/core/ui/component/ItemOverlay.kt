package com.neki.android.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.modifier.photoGridBackground
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun PhotoGridItemOverlay(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
) {
    Box(
        modifier = modifier.photoGridBackground(shape = shape),
    )
}

@Composable
fun SelectedPhotoGridItemOverlay(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
) {
    Box(
        modifier = modifier.background(
            color = Color.Black.copy(alpha = 0.2f),
            shape = shape,
        ),
    )
}

@ComponentPreview
@Composable
private fun PhotoGridItemOverlayPreview() {
    NekiTheme {
        PhotoGridItemOverlay(
            modifier = Modifier
                .size(80.dp),
        )
    }
}

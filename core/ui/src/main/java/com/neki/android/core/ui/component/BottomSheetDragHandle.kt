package com.neki.android.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun BottomSheetDragHandle(
    modifier: Modifier = Modifier,
    width: Dp = 45.dp,
    height: Dp = 4.dp,
    color: Color = NekiTheme.colorScheme.gray300,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .size(width = width, height = height)
                .background(
                    color = color,
                    shape = RoundedCornerShape(13.dp),
                )
                .align(Alignment.Center),
        )
    }
}

@ComponentPreview
@Composable
private fun BottomSheetDragHandlePreview() {
    NekiTheme {
        BottomSheetDragHandle()
    }
}

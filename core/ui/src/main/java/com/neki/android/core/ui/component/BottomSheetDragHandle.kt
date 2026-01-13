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
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme

private const val DRAG_HANDLE_RADIUS = 13

@Composable
fun BottomSheetDragHandle(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .size(width = 45.dp, height = 4.dp)
                .background(
                    color = NekiTheme.colorScheme.gray100,
                    shape = RoundedCornerShape(DRAG_HANDLE_RADIUS.dp),
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

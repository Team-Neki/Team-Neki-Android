package com.neki.android.feature.map.impl.photobooth.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Brand
import com.neki.android.core.ui.compose.HorizontalSpacer
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState

@Composable
internal fun ReorderableCollectionItemScope.PhotoBoothOrderChangeItem(
    brand: Brand,
    isDragging: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isDragging) NekiTheme.colorScheme.gray50 else Color.White)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = brand.imageUrl,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = 0.5.dp,
                    color = NekiTheme.colorScheme.gray75,
                    shape = RoundedCornerShape(8.dp),
                ),
            placeholder = painterResource(R.drawable.icon_photo_booth_empty),
            error = painterResource(R.drawable.icon_photo_booth_empty),
            contentDescription = null,
        )
        HorizontalSpacer(12.dp)
        Text(
            text = brand.name,
            style = NekiTheme.typography.title18SemiBold,
            color = NekiTheme.colorScheme.gray900,
            modifier = Modifier.weight(1f),
        )
        val dragHandleInteractionSource = remember { MutableInteractionSource() }
        val isDragHandleDragging by dragHandleInteractionSource.collectIsDraggedAsState()
        Icon(
            modifier = Modifier
                .draggableHandle(interactionSource = dragHandleInteractionSource)
                .size(24.dp),
            imageVector = ImageVector.vectorResource(R.drawable.icon_drag),
            tint = if (isDragHandleDragging) NekiTheme.colorScheme.gray50 else Color.Unspecified,
            contentDescription = null,
        )
    }
}

@ComponentPreview
@Composable
private fun PhotoBoothOrderChangeItemPreview() {
    NekiTheme {
        val lazyListState = rememberLazyListState()
        val reorderableState = rememberReorderableLazyListState(lazyListState) { _, _ -> }
        LazyColumn(state = lazyListState) {
            item(key = 1L) {
                ReorderableItem(reorderableState, key = 1L) { isDragging ->
                    PhotoBoothOrderChangeItem(
                        brand = Brand(id = 1L, name = "인생네컷", imageUrl = ""),
                        isDragging = isDragging,
                    )
                }
            }
        }
    }
}

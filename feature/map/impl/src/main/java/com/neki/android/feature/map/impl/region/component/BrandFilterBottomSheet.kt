package com.neki.android.feature.map.impl.region.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.bottomsheet.BottomSheetDragHandle
import com.neki.android.core.designsystem.button.CTAButtonPrimary
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Brand
import com.neki.android.feature.map.impl.search.model.SearchBrandFilter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BrandFilterBottomSheet(
    filters: ImmutableList<SearchBrandFilter>,
    selectedBrandIds: ImmutableSet<Long>,
    onDismiss: () -> Unit = {},
    onBrandClick: (Long) -> Unit = {},
    onApply: () -> Unit = {},
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = NekiTheme.colorScheme.white,
        dragHandle = { BottomSheetDragHandle(color = NekiTheme.colorScheme.gray100) },
    ) {
        BrandFilterBottomSheetContent(
            filters = filters,
            selectedBrandIds = selectedBrandIds,
            onBrandClick = onBrandClick,
            onApply = onApply,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun BrandFilterBottomSheetContent(
    filters: ImmutableList<SearchBrandFilter>,
    selectedBrandIds: ImmutableSet<Long>,
    onBrandClick: (Long) -> Unit = {},
    onApply: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 34.dp),
    ) {
        Text(
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
            text = "브랜드 필터",
            style = NekiTheme.typography.title20SemiBold,
            color = NekiTheme.colorScheme.gray900,
        )
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            filters.forEach { filter ->
                BrandFilterOption(
                    brand = filter.brand,
                    selected = filter.brand.id in selectedBrandIds,
                    onClick = { onBrandClick(filter.brand.id) },
                )
            }
        }
        CTAButtonPrimary(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            text = "적용",
            onClick = onApply,
        )
    }
}

@Composable
private fun BrandFilterOption(
    brand: Brand,
    selected: Boolean,
    onClick: () -> Unit = {},
) {
    val color = if (selected) NekiTheme.colorScheme.primary400 else NekiTheme.colorScheme.gray400
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .clip(shape)
            .toggleable(
                value = selected,
                role = Role.Checkbox,
                onValueChange = { onClick() },
            )
            .border(
                width = 1.dp,
                color = if (selected) color else NekiTheme.colorScheme.gray75,
                shape = shape,
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = brand.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .border(0.5.dp, NekiTheme.colorScheme.gray100, CircleShape),
        )
        Text(
            text = brand.name,
            style = if (selected) NekiTheme.typography.body14SemiBold else NekiTheme.typography.body14Medium,
            color = color,
        )
    }
}

@ComponentPreview
@Composable
private fun BrandFilterBottomSheetPreview() {
    NekiTheme {
        Column(modifier = Modifier.width(375.dp)) {
            BottomSheetDragHandle(color = NekiTheme.colorScheme.gray100)
            BrandFilterBottomSheetContent(
                filters = persistentListOf(
                    SearchBrandFilter(
                        brand = Brand(id = 1, name = "포토이즘"),
                        count = 4,
                    ),
                    SearchBrandFilter(
                        brand = Brand(id = 2, name = "인생네컷"),
                        count = 2,
                    ),
                ),
                selectedBrandIds = persistentSetOf(1L),
            )
        }
    }
}

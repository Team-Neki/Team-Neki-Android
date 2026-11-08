package com.neki.android.feature.pose.impl.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.modifier.clickableSingle
import com.neki.android.core.designsystem.modifier.dropdownShadow
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.pose.impl.main.PoseSortType

@Composable
internal fun PoseSortDropdown(
    selectedSortType: PoseSortType,
    onDismissRequest: () -> Unit,
    onClickItem: (PoseSortType) -> Unit,
    offset: IntOffset = IntOffset.Zero,
    alignment: Alignment = Alignment.TopEnd,
) {
    Popup(
        offset = offset,
        alignment = alignment,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        PoseSortDropdownContent(
            selectedSortType = selectedSortType,
            onClickItem = onClickItem,
        )
    }
}

@Composable
private fun PoseSortDropdownContent(
    selectedSortType: PoseSortType,
    modifier: Modifier = Modifier,
    onClickItem: (PoseSortType) -> Unit = {},
) {
    Column(
        modifier = modifier
            .width(106.dp)
            .dropdownShadow(shape = RoundedCornerShape(8.dp))
            .background(
                color = NekiTheme.colorScheme.white,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        PoseSortType.entries.forEach { sortType ->
            val isSelected = sortType == selectedSortType
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableSingle { onClickItem(sortType) }
                    .padding(start = 12.dp, end = 16.dp, top = 5.dp, bottom = 5.dp),
                text = sortType.displayText,
                style = if (isSelected) NekiTheme.typography.body14SemiBold
                else NekiTheme.typography.body14Regular,
                color = if (isSelected) NekiTheme.colorScheme.gray900
                else NekiTheme.colorScheme.gray700,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun PoseSortDropdownContentPreview() {
    NekiTheme {
        PoseSortDropdownContent(selectedSortType = PoseSortType.POPULAR)
    }
}

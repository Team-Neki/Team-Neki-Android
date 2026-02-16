package com.neki.android.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * 단순 액션용 드롭다운
 * */
@Composable
fun <T> DropdownPopup(
    items: ImmutableList<T>,
    onSelect: (T) -> Unit,
    onDismissRequest: () -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
    offset: IntOffset = IntOffset.Zero,
    alignment: Alignment = Alignment.TopStart,
) {
    Popup(
        offset = offset,
        alignment = alignment,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        Column(
            modifier = modifier
                .dropdownShadow(shape = RoundedCornerShape(8.dp))
                .background(
                    color = NekiTheme.colorScheme.white,
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(vertical = 6.dp),
        ) {
            items.forEach { item ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickableSingle { onSelect(item) }
                        .padding(horizontal = 16.dp, vertical = 5.dp),
                    text = itemLabel(item),
                    style = NekiTheme.typography.body14Medium,
                )
            }
        }
    }
}

/**
 * 선택값을 유지해야하는 경우에 쓰이는 드롭다운
 * */
@Composable
fun <T> DropdownPopup(
    items: ImmutableList<T>,
    selectedItem: T?,
    onSelect: (T) -> Unit,
    onDismissRequest: () -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
    offset: IntOffset = IntOffset.Zero,
    alignment: Alignment = Alignment.TopStart,
) {
    Popup(
        offset = offset,
        alignment = alignment,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        Column(
            modifier = modifier
                .dropdownShadow(shape = RoundedCornerShape(8.dp))
                .background(
                    color = NekiTheme.colorScheme.white,
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(vertical = 6.dp),
        ) {
            items.forEach { item ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (selectedItem == item) NekiTheme.colorScheme.gray50
                            else NekiTheme.colorScheme.white,
                        )
                        .clickableSingle { onSelect(item) }
                        .padding(horizontal = 16.dp, vertical = 5.dp),
                    text = itemLabel(item),
                    style = NekiTheme.typography.body14Medium,
                )
            }
        }
    }
}

private enum class PreviewDropdownOption(val label: String) {
    NEWEST("최신순"),
    OLDEST("오래된순"),
    ;

    override fun toString(): String = label
}

@ComponentPreview
@Composable
private fun DropdownPopupPreview() {
    NekiTheme {
        DropdownPopup(
            items = PreviewDropdownOption.entries.toImmutableList(),
            selectedItem = PreviewDropdownOption.NEWEST,
            onSelect = {},
            onDismissRequest = {},
            itemLabel = { it.label },
        )
    }
}

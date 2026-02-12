package com.neki.android.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.modifier.clickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun <T> SelectDialog(
    options: ImmutableList<T>,
    onDismissRequest: () -> Unit,
    onSelect: (T) -> Unit,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .widthIn(max = 400.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(NekiTheme.colorScheme.white)
                .padding(vertical = 12.dp),
        ) {
            options.forEach { option ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickableSingle(onClick = { onSelect(option) })
                        .padding(vertical = 14.dp),
                    text = option.toString(),
                    style = NekiTheme.typography.body16SemiBold,
                    color = NekiTheme.colorScheme.gray800,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

private enum class PreviewSelectOption(val label: String) {
    OPTION_1("선택지1"),
    OPTION_2("선택지2"),
    ;

    override fun toString(): String = label
}

@ComponentPreview
@Composable
private fun SelectDialogPreview() {
    NekiTheme {
        SelectDialog(
            options = PreviewSelectOption.entries.toImmutableList(),
            onDismissRequest = {},
            onSelect = {},
        )
    }
}

package com.neki.android.feature.archive.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.bottomsheet.BottomSheetDragHandle
import com.neki.android.core.designsystem.button.CTAButtonGray
import com.neki.android.core.designsystem.button.CTAButtonPrimary
import com.neki.android.core.designsystem.modifier.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun <T> DeleteOptionBottomSheet(
    title: String,
    options: ImmutableList<T>,
    selectedOption: T,
    onDismissRequest: () -> Unit,
    onClickCancel: () -> Unit,
    onClickDelete: () -> Unit,
    onOptionSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = NekiTheme.colorScheme.white,
        dragHandle = { BottomSheetDragHandle(color = NekiTheme.colorScheme.gray100) },
    ) {
        DeleteOptionBottomSheetContent(
            title = title,
            options = options,
            selectedOption = selectedOption,
            onClickCancel = onClickCancel,
            onClickDelete = onClickDelete,
            onOptionSelect = onOptionSelect,
        )
    }
}

@Composable
internal fun <T> DeleteOptionBottomSheetContent(
    title: String,
    options: List<T>,
    selectedOption: T,
    onClickCancel: () -> Unit,
    onClickDelete: () -> Unit,
    onOptionSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 34.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = title,
            style = NekiTheme.typography.title20SemiBold,
            color = NekiTheme.colorScheme.gray900,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            options.forEach { option ->
                DeleteOptionRow(
                    label = option.toString(),
                    isSelected = selectedOption == option,
                    onClick = { onOptionSelect(option) },
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CTAButtonGray(
                modifier = Modifier.weight(93f),
                text = "취소",
                onClick = onClickCancel,
            )
            CTAButtonPrimary(
                modifier = Modifier.weight(230f),
                text = "삭제하기",
                onClick = onClickDelete,
            )
        }
    }
}

@Composable
private fun DeleteOptionRow(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (isSelected) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_check),
                contentDescription = null,
                tint = NekiTheme.colorScheme.primary500,
            )
        }

        Text(
            text = label,
            style = if (isSelected) NekiTheme.typography.body16SemiBold else NekiTheme.typography.body16Medium,
            color = if (isSelected) NekiTheme.colorScheme.gray900 else NekiTheme.colorScheme.gray400,
        )
    }
}

private enum class PreviewDeleteOption(val label: String) {
    OPTION_1("옵션 1"),
    OPTION_2("옵션 2"),
    ;

    override fun toString(): String = label
}

@ComponentPreview
@Composable
private fun DeleteOptionBottomSheetContentPreview() {
    NekiTheme {
        DeleteOptionBottomSheetContent(
            title = "삭제하시겠어요?",
            options = PreviewDeleteOption.entries,
            selectedOption = PreviewDeleteOption.OPTION_1,
            onClickCancel = {},
            onClickDelete = {},
            onOptionSelect = {},
        )
    }
}

@ComponentPreview
@Composable
private fun DeleteOptionBottomSheetContentOption2Preview() {
    NekiTheme {
        DeleteOptionBottomSheetContent(
            title = "삭제하시겠어요?",
            options = PreviewDeleteOption.entries,
            selectedOption = PreviewDeleteOption.OPTION_2,
            onClickCancel = {},
            onClickDelete = {},
            onOptionSelect = {},
        )
    }
}

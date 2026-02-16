package com.neki.android.core.ui.component

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
import com.neki.android.core.ui.compose.VerticalSpacer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DoubleButtonOptionBottomSheet(
    title: String,
    options: ImmutableList<T>,
    primaryButtonText: String,
    secondaryButtonText: String,
    selectedOption: T?,
    onDismissRequest: () -> Unit,
    onClickSecondaryButton: () -> Unit,
    onClickPrimaryButton: () -> Unit,
    onOptionSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    buttonEnabled: Boolean = true,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = NekiTheme.colorScheme.white,
        dragHandle = { BottomSheetDragHandle(color = NekiTheme.colorScheme.gray100) },
    ) {
        DoubleButtonOptionBottomSheetContent(
            title = title,
            options = options,
            selectedOption = selectedOption,
            onClickCancel = onClickSecondaryButton,
            onClickDoubleButton = onClickPrimaryButton,
            onOptionSelect = onOptionSelect,
            buttonEnabled = buttonEnabled,
            primaryButtonText = primaryButtonText,
            secondaryButtonText = secondaryButtonText,
        )
    }
}

@Composable
internal fun <T> DoubleButtonOptionBottomSheetContent(
    title: String,
    options: ImmutableList<T>,
    primaryButtonText: String,
    secondaryButtonText: String,
    selectedOption: T?,
    onClickCancel: () -> Unit,
    onClickDoubleButton: () -> Unit,
    onOptionSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    buttonEnabled: Boolean = true,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 34.dp),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = title,
            style = NekiTheme.typography.title20SemiBold,
            color = NekiTheme.colorScheme.gray900,
        )
        VerticalSpacer(12.dp)
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            options.forEach { option ->
                OptionRow(
                    label = option.toString(),
                    isSelected = selectedOption == option,
                    onClick = { onOptionSelect(option) },
                )
            }
        }
        VerticalSpacer(16.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CTAButtonGray(
                modifier = Modifier.weight(93f),
                text = secondaryButtonText,
                onClick = onClickCancel,
                enabled = buttonEnabled,
            )
            CTAButtonPrimary(
                modifier = Modifier.weight(230f),
                text = primaryButtonText,
                onClick = onClickDoubleButton,
                enabled = buttonEnabled,
            )
        }
    }
}

@Composable
private fun OptionRow(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
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

private enum class PreviewOption(val label: String) {
    OPTION_1("옵션 1"),
    OPTION_2("옵션 2"),
    ;

    override fun toString(): String = label
}

@ComponentPreview
@Composable
private fun DoubleButtonOptionBottomSheetContentPreview() {
    NekiTheme {
        DoubleButtonOptionBottomSheetContent(
            title = "삭제하시겠어요?",
            options = PreviewOption.entries.toImmutableList(),
            primaryButtonText = "확인",
            secondaryButtonText = "취소",
            selectedOption = PreviewOption.OPTION_1,
            onClickCancel = {},
            onClickDoubleButton = {},
            onOptionSelect = {},
        )
    }
}

@ComponentPreview
@Composable
private fun DoubleButtonOptionBottomSheetContentOption2Preview() {
    NekiTheme {
        DoubleButtonOptionBottomSheetContent(
            title = "삭제하시겠어요?",
            options = PreviewOption.entries.toImmutableList(),
            primaryButtonText = "확인",
            secondaryButtonText = "취소",
            selectedOption = PreviewOption.OPTION_2,
            onClickCancel = {},
            onClickDoubleButton = {},
            onOptionSelect = {},
        )
    }
}

@ComponentPreview
@Composable
private fun DoubleButtonOptionBottomSheetContentDisabledPreview() {
    NekiTheme {
        DoubleButtonOptionBottomSheetContent(
            title = "삭제하시겠어요?",
            options = PreviewOption.entries.toImmutableList(),
            primaryButtonText = "확인",
            secondaryButtonText = "취소",
            selectedOption = null,
            onClickCancel = {},
            onClickDoubleButton = {},
            onOptionSelect = {},
            buttonEnabled = false,
        )
    }
}

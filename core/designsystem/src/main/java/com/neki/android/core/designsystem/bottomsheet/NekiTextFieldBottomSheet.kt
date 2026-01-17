package com.neki.android.core.designsystem.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.button.CTAButtonGray
import com.neki.android.core.designsystem.button.CTAButtonPrimary
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NekiTextFieldBottomSheet(
    title: String,
    subtitle: String,
    textFieldState: TextFieldState,
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    placeholder: String = "",
    maxLength: Int? = null,
    confirmButtonText: String = "추가하기",
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = NekiTheme.colorScheme.white,
        dragHandle = { BottomSheetDragHandle(color = NekiTheme.colorScheme.gray100) },
    ) {
        NekiTextFieldBottomSheetContent(
            title = title,
            subtitle = subtitle,
            textFieldState = textFieldState,
            onCancelClick = onCancelClick,
            onConfirmClick = onConfirmClick,
            placeholder = placeholder,
            maxLength = maxLength,
            confirmButtonText = confirmButtonText,
            isError = isError,
            errorMessage = errorMessage,
            keyboardOptions = keyboardOptions,
        )
    }
}

@Composable
private fun NekiTextFieldBottomSheetContent(
    title: String,
    subtitle: String,
    textFieldState: TextFieldState,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    maxLength: Int? = null,
    confirmButtonText: String = "추가하기",
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val isOverLimit by remember(textFieldState.text.length) { derivedStateOf { maxLength?.let { textFieldState.text.length > it } ?: false } }
    val hasError by remember(isError) { derivedStateOf { isError || isOverLimit } }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 34.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Title & Subtitle
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                style = NekiTheme.typography.title20SemiBold,
                color = NekiTheme.colorScheme.gray900,
            )
            Text(
                text = subtitle,
                style = NekiTheme.typography.body14Regular,
                color = NekiTheme.colorScheme.gray700,
            )
        }

        // TextField
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            NekiBottomSheetTextField(
                textFieldState = textFieldState,
                placeholder = placeholder,
                maxLength = maxLength,
                isError = hasError,
                keyboardOptions = keyboardOptions,
            )

            // Error message
            if (hasError && !errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage,
                    style = NekiTheme.typography.caption12Regular,
                    color = NekiTheme.colorScheme.primary600,
                )
            }
        }

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CTAButtonGray(
                modifier = Modifier.width(93.dp),
                text = "취소",
                onClick = onCancelClick,
            )
            CTAButtonPrimary(
                modifier = Modifier.weight(1f),
                text = confirmButtonText,
                onClick = onConfirmClick,
                enabled = textFieldState.text.isNotEmpty() && !hasError,
            )
        }
    }
}

@Composable
private fun NekiBottomSheetTextField(
    textFieldState: TextFieldState,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    maxLength: Int? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor = when {
        isError -> NekiTheme.colorScheme.primary600
        isFocused -> NekiTheme.colorScheme.gray700
        else -> NekiTheme.colorScheme.gray75
    }

    BasicTextField(
        state = textFieldState,
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = NekiTheme.colorScheme.white,
                shape = RoundedCornerShape(8.dp),
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(horizontal = 16.dp, vertical = 13.dp),
        textStyle = NekiTheme.typography.body16Medium.copy(
            color = NekiTheme.colorScheme.gray900,
        ),
        interactionSource = interactionSource,
        cursorBrush = SolidColor(NekiTheme.colorScheme.gray800),
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = keyboardOptions,
        decorator = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (textFieldState.text.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = NekiTheme.typography.body16Regular,
                            color = NekiTheme.colorScheme.gray300,
                        )
                    }
                    innerTextField()
                }
                maxLength?.let {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${textFieldState.text.length}/$maxLength",
                        style = NekiTheme.typography.caption12Regular,
                        color = NekiTheme.colorScheme.gray300,
                    )
                }
            }
        },
    )
}

@ComponentPreview
@Composable
private fun NekiTextFieldBottomSheetContentDefaultPreview() {
    NekiTheme {
        NekiTextFieldBottomSheetContent(
            title = "텍스트",
            subtitle = "보조 텍스트가 들어가는 자리입니다",
            textFieldState = rememberTextFieldState(),
            onCancelClick = {},
            onConfirmClick = {},
            placeholder = "플레이스 홀더에 들어갈 문구",
        )
    }
}

@ComponentPreview
@Composable
private fun NekiTextFieldBottomSheetContentCompletedPreview() {
    NekiTheme {
        NekiTextFieldBottomSheetContent(
            title = "텍스트",
            subtitle = "보조 텍스트가 들어가는 자리입니다",
            textFieldState = rememberTextFieldState(initialText = "입력 완료 입력 완료 입력 완료 입력 완료 입력 완료 입력 완료 "),
            onCancelClick = {},
            onConfirmClick = {},
        )
    }
}

@ComponentPreview
@Composable
private fun NekiTextFieldBottomSheetContentErrorPreview() {
    NekiTheme {
        NekiTextFieldBottomSheetContent(
            title = "텍스트",
            subtitle = "보조 텍스트가 들어가는 자리입니다",
            textFieldState = rememberTextFieldState(initialText = "오류인 상태 텍스트입니다"),
            onCancelClick = {},
            onConfirmClick = {},
            maxLength = 16,
            isError = true,
            errorMessage = "에러 메세지 텍스트",
        )
    }
}

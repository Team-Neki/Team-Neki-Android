package com.neki.android.core.designsystem

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun NekiTextField(
    textFieldState: TextFieldState,
    modifier: Modifier = Modifier,
    titleLabel: String? = null,
    placeholder: String = "",
    maxLength: Int? = null,
    isError: Boolean = false,
    textStyle: TextStyle = NekiTheme.typography.body16Medium.copy(
        color = NekiTheme.colorScheme.gray900,
    ),
    cursorBrush: Brush = SolidColor(NekiTheme.colorScheme.gray800),
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor = when {
        isError -> NekiTheme.colorScheme.primary600
        isFocused -> NekiTheme.colorScheme.gray700
        else -> NekiTheme.colorScheme.gray75
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        titleLabel?.let {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                text = it,
                style = NekiTheme.typography.body14Medium,
                color = NekiTheme.colorScheme.gray700,
            )
        }

        BasicTextField(
            state = textFieldState,
            modifier = Modifier
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
            textStyle = textStyle,
            inputTransformation = inputTransformation,
            outputTransformation = outputTransformation,
            interactionSource = interactionSource,
            cursorBrush = cursorBrush,
            lineLimits = lineLimits,
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
}

@Composable
fun NekiTextFieldWithError(
    textFieldState: TextFieldState,
    modifier: Modifier = Modifier,
    titleLabel: String? = null,
    placeholder: String = "",
    maxLength: Int? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    textStyle: TextStyle = NekiTheme.typography.body16Medium.copy(
        color = NekiTheme.colorScheme.gray900,
    ),
    cursorBrush: Brush = SolidColor(NekiTheme.colorScheme.gray800),
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        NekiTextField(
            textFieldState = textFieldState,
            titleLabel = titleLabel,
            placeholder = placeholder,
            maxLength = maxLength,
            isError = isError,
            textStyle = textStyle,
            cursorBrush = cursorBrush,
            lineLimits = lineLimits,
            inputTransformation = inputTransformation,
            outputTransformation = outputTransformation,
            keyboardOptions = keyboardOptions,
        )

        Text(
            modifier = Modifier.heightIn(min = 16.dp),
            text = if (isError) errorMessage.orEmpty() else "",
            style = NekiTheme.typography.caption12Regular,
            color = NekiTheme.colorScheme.primary600,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NekiTextFieldPreview() {
    NekiTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            NekiTextField(
                textFieldState = remember { TextFieldState() },
                placeholder = "플레이스홀더",
            )

            NekiTextField(
                textFieldState = remember { TextFieldState("입력된 텍스트") },
                maxLength = 20,
            )

            NekiTextField(
                textFieldState = remember { TextFieldState("에러 상태") },
                isError = true,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NekiTextFieldWithTitleLabelPreview() {
    NekiTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            NekiTextField(
                textFieldState = remember { TextFieldState() },
                titleLabel = "닉네임",
                placeholder = "닉네임을 입력해주세요",
            )

            NekiTextField(
                textFieldState = remember { TextFieldState("입력된 텍스트") },
                titleLabel = "닉네임",
                maxLength = 10,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NekiTextFieldWithErrorPreview() {
    NekiTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            NekiTextFieldWithError(
                textFieldState = remember { TextFieldState() },
                placeholder = "플레이스홀더",
            )

            NekiTextFieldWithError(
                textFieldState = remember { TextFieldState("입력된 텍스트") },
                placeholder = "플레이스홀더",
            )

            NekiTextFieldWithError(
                textFieldState = remember { TextFieldState("에러 상태") },
                isError = true,
                errorMessage = "올바른 값을 입력해주세요",
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NekiTextFieldWithErrorAndTitleLabelPreview() {
    NekiTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            NekiTextFieldWithError(
                textFieldState = remember { TextFieldState() },
                titleLabel = "닉네임",
                placeholder = "닉네임을 입력해주세요",
                maxLength = 10,
            )

            NekiTextFieldWithError(
                textFieldState = remember { TextFieldState("입력된 텍스트") },
                placeholder = "플레이스홀더",
                maxLength = 10,
            )

            NekiTextFieldWithError(
                textFieldState = remember { TextFieldState("에러 상태") },
                titleLabel = "닉네임",
                isError = true,
                errorMessage = "이미 사용 중인 닉네임입니다",
                maxLength = 10,
            )
        }
    }
}

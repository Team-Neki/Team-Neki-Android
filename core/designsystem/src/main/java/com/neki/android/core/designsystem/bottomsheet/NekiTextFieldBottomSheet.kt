package com.neki.android.core.designsystem.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.NekiTextFieldWithError
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
    onClickCancel: () -> Unit,
    onClickConfirm: () -> Unit,
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
            onClickCancel = onClickCancel,
            onClickConfirm = onClickConfirm,
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
    onClickCancel: () -> Unit,
    onClickConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    maxLength: Int? = null,
    confirmButtonText: String = "추가하기",
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 34.dp),
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
        Spacer(modifier = Modifier.height(16.dp))

        // TextField
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            NekiTextFieldWithError(
                textFieldState = textFieldState,
                placeholder = placeholder,
                maxLength = maxLength,
                isError = isError,
                keyboardOptions = keyboardOptions,
                errorMessage = errorMessage,
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Buttons
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
                text = confirmButtonText,
                onClick = onClickConfirm,
                enabled = textFieldState.text.isNotEmpty() && !isError,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun NekiTextFieldBottomSheetContentDefaultPreview() {
    NekiTheme {
        NekiTextFieldBottomSheetContent(
            title = "텍스트",
            subtitle = "보조 텍스트가 들어가는 자리입니다",
            textFieldState = rememberTextFieldState(),
            onClickCancel = {},
            onClickConfirm = {},
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
            onClickCancel = {},
            onClickConfirm = {},
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
            onClickCancel = {},
            onClickConfirm = {},
            maxLength = 16,
            isError = true,
            errorMessage = "에러 메세지 텍스트",
        )
    }
}

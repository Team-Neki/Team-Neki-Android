package com.neki.android.feature.archive.impl.photo_detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.button.NekiTextButton
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.archive.impl.photo_detail.MemoMode

@Composable
internal fun MemoTextField(
    memo: String,
    memoMode: MemoMode,
    onClickMemoMore: () -> Unit,
    onClickMemoFold: () -> Unit,
    onClickMemoText: () -> Unit,
    onClickMemoCancel: () -> Unit,
    onClickMemoDone: (String) -> Unit,
    onMemoTextChanged: (String) -> Unit,
) {
    val memoState = remember { TextFieldState(initialText = memo) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var previousMemoMode by remember { mutableStateOf(memoMode) }

    LaunchedEffect(memo, memoMode) {
        if (memoMode != MemoMode.Editing) {
            memoState.edit { replace(0, length, memo) }
        }
    }

    LaunchedEffect(memoMode) {
        if (memoMode == MemoMode.Editing) {
            memoState.edit { replace(0, length, memo) }
            focusRequester.requestFocus()
        } else if (previousMemoMode == MemoMode.Editing) {
            keyboardController?.hide()
        }
        previousMemoMode = memoMode
    }

    LaunchedEffect(memoState, memoMode) {
        if (memoMode == MemoMode.Editing) {
            snapshotFlow { memoState.text.toString() }
                .collect { text -> onMemoTextChanged(text) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                when (memoMode) {
                    MemoMode.Editing -> NekiTheme.colorScheme.gray25
                    else -> NekiTheme.colorScheme.white
                },
            ),
    ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = NekiTheme.colorScheme.gray75,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    when (memoMode) {
                        MemoMode.Preview, MemoMode.Expanded -> Modifier.noRippleClickableSingle { onClickMemoText() }
                        else -> Modifier
                    },
                )
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 20.dp,
                    bottom = if (memoMode == MemoMode.Editing) 0.dp else 20.dp,
                ),
        ) {
            when (memoMode) {
                MemoMode.Closed -> Unit
                MemoMode.Preview -> MemoPreviewContent(
                    memo = memo,
                    onClickMemoMore = onClickMemoMore,
                )

                MemoMode.Expanded -> MemoExpandedContent(
                    memoState = memoState,
                    focusRequester = focusRequester,
                    onClickMemoText = onClickMemoText,
                    onClickMemoFold = onClickMemoFold,
                )

                MemoMode.Editing -> MemoEditingContent(
                    memoState = memoState,
                    focusRequester = focusRequester,
                    onClickMemoCancel = onClickMemoCancel,
                    onClickMemoDone = onClickMemoDone,
                )
            }
        }
    }
}

@Composable
private fun MemoPreviewContent(
    memo: String,
    onClickMemoMore: () -> Unit,
) {
    var hasOverflow by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = memo.ifEmpty { "자유롭게 메모를 입력해주세요. (최대 100자)" },
            modifier = Modifier.weight(1f),
            style = NekiTheme.typography.body16Regular,
            color = if (memo.isEmpty()) NekiTheme.colorScheme.gray300
            else NekiTheme.colorScheme.gray700,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { result -> hasOverflow = result.hasVisualOverflow },
        )
        if (hasOverflow) {
            Text(
                text = "더보기",
                modifier = Modifier.noRippleClickableSingle { onClickMemoMore() },
                style = NekiTheme.typography.body16Medium,
                color = NekiTheme.colorScheme.gray300,
            )
        }
    }
}

@Composable
private fun ColumnScope.MemoExpandedContent(
    memoState: TextFieldState,
    focusRequester: FocusRequester,
    onClickMemoText: () -> Unit,
    onClickMemoFold: () -> Unit,
) {
    BasicTextField(
        state = memoState,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (it.isFocused) onClickMemoText()
            },
        textStyle = NekiTheme.typography.body16Medium,
        readOnly = true,
        inputTransformation = InputTransformation.maxLength(100),
        cursorBrush = SolidColor(Color.Transparent),
        decorator = { innerTextField -> innerTextField() },
    )
    Text(
        text = "메모 접기",
        modifier = Modifier
            .align(Alignment.End)
            .noRippleClickableSingle { onClickMemoFold() },
        style = NekiTheme.typography.body16Medium,
        color = NekiTheme.colorScheme.gray200,
    )
}

@Composable
private fun MemoEditingContent(
    memoState: TextFieldState,
    focusRequester: FocusRequester,
    onClickMemoCancel: () -> Unit,
    onClickMemoDone: (String) -> Unit,
) {
    val charCount = memoState.text.length

    BasicTextField(
        state = memoState,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        textStyle = NekiTheme.typography.body16Medium,
        inputTransformation = InputTransformation.maxLength(100),
        cursorBrush = SolidColor(NekiTheme.colorScheme.gray900),
        decorator = { innerTextField -> innerTextField() },
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$charCount/100",
                style = NekiTheme.typography.caption12Regular,
                color = NekiTheme.colorScheme.gray300,
            )
            Text(
                text = "전체 지우기",
                modifier = Modifier.noRippleClickableSingle {
                    memoState.edit { replace(0, length, "") }
                },
                style = NekiTheme.typography.caption12Medium,
                color = NekiTheme.colorScheme.gray200,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NekiTextButton(
                onClick = onClickMemoCancel,
                contentPadding = PaddingValues(10.dp),
            ) {
                Text(
                    text = "취소",
                    style = NekiTheme.typography.body16SemiBold,
                    color = NekiTheme.colorScheme.gray800,
                )
            }
            NekiTextButton(
                onClick = { onClickMemoDone(memoState.text.toString()) },
                contentPadding = PaddingValues(10.dp),
            ) {
                Text(
                    text = "완료",
                    style = NekiTheme.typography.body16SemiBold,
                    color = NekiTheme.colorScheme.primary500,
                )
            }
        }
    }
}

@ComponentPreview
@Composable
private fun MemoTextFieldPreviewPreview() {
    NekiTheme {
        Box {
            MemoTextField(
                memo = "짧은 메모",
                memoMode = MemoMode.Preview,
                onClickMemoMore = {},
                onClickMemoFold = {},
                onClickMemoText = {},
                onClickMemoCancel = {},
                onClickMemoDone = {},
                onMemoTextChanged = {},
            )
        }
    }
}

@ComponentPreview
@Composable
private fun MemoTextFieldPreviewOverflowPreview() {
    NekiTheme {
        Box {
            MemoTextField(
                memo = "재밌었던 날인데 메모가 길어서 한 줄에 다 안 들어갈 수도 있어요 이렇게 길면 더보기가 나와야 해요",
                memoMode = MemoMode.Preview,
                onClickMemoMore = {},
                onClickMemoFold = {},
                onClickMemoText = {},
                onClickMemoCancel = {},
                onClickMemoDone = {},
                onMemoTextChanged = {},
            )
        }
    }
}

@ComponentPreview
@Composable
private fun MemoTextFieldExpandedPreview() {
    NekiTheme {
        Box {
            MemoTextField(
                memo = "재밌었던 날인데 메모가 길어서 한 줄에 다 안 들어갈 수도 있어요",
                memoMode = MemoMode.Expanded,
                onClickMemoMore = {},
                onClickMemoFold = {},
                onClickMemoText = {},
                onClickMemoCancel = {},
                onClickMemoDone = {},
                onMemoTextChanged = {},
            )
        }
    }
}

@ComponentPreview
@Composable
private fun MemoTextFieldEditingPreview() {
    NekiTheme {
        Box {
            MemoTextField(
                memo = "재밌었던 날",
                memoMode = MemoMode.Editing,
                onClickMemoMore = {},
                onClickMemoFold = {},
                onClickMemoText = {},
                onClickMemoCancel = {},
                onClickMemoDone = {},
                onMemoTextChanged = {},
            )
        }
    }
}

@ComponentPreview
@Composable
private fun MemoTextFieldEmptyPreview() {
    NekiTheme {
        Box {
            MemoTextField(
                memo = "",
                memoMode = MemoMode.Preview,
                onClickMemoMore = {},
                onClickMemoFold = {},
                onClickMemoText = {},
                onClickMemoCancel = {},
                onClickMemoDone = {},
                onMemoTextChanged = {},
            )
        }
    }
}

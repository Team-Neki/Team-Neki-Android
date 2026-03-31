package com.neki.android.feature.archive.impl.photo_detail.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.actionbar.NekiBothSidesActionBar
import com.neki.android.core.designsystem.button.NekiIconButton
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.archive.impl.photo_detail.MemoMode

@Composable
internal fun PhotoDetailActionBar(
    isFavorite: Boolean,
    memo: String,
    memoMode: MemoMode,
    modifier: Modifier = Modifier,
    onClickDownload: () -> Unit = {},
    onClickFavorite: () -> Unit = {},
    onClickMemo: () -> Unit = {},
    onClickMemoMore: () -> Unit = {},
    onClickMemoText: () -> Unit = {},
    onClickMemoFold: () -> Unit = {},
    onClickMemoCancel: () -> Unit = {},
    onClickMemoDone: (String) -> Unit = {},
    onClickDelete: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NekiTheme.colorScheme.white),
    ) {
        AnimatedVisibility(
            visible = memoMode != MemoMode.Closed,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            MemoTextField(
                memo = memo,
                memoMode = memoMode,
                onClickMemoMore = onClickMemoMore,
                onClickMemoFold = onClickMemoFold,
                onClickMemoText = onClickMemoText,
                onClickMemoCancel = onClickMemoCancel,
                onClickMemoDone = onClickMemoDone,
            )
        }

        // 아이콘 바 (Editing 모드에서는 숨김)
        if (memoMode != MemoMode.Editing) {
            NekiBothSidesActionBar(
                startContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        NekiIconButton(
                            modifier = Modifier.padding(8.dp),
                            onClick = onClickDownload,
                        ) {
                            Icon(
                                modifier = Modifier.size(28.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.icon_download),
                                contentDescription = null,
                                tint = NekiTheme.colorScheme.gray700,
                            )
                        }
                        NekiIconButton(
                            modifier = Modifier.padding(8.dp),
                            onClick = onClickFavorite,
                        ) {
                            Icon(
                                modifier = Modifier.size(28.dp),
                                imageVector = ImageVector.vectorResource(
                                    if (isFavorite) R.drawable.icon_favorite_filled
                                    else R.drawable.icon_favorite_stroked,
                                ),
                                contentDescription = null,
                                tint = if (isFavorite) NekiTheme.colorScheme.primary400
                                else NekiTheme.colorScheme.gray700,
                            )
                        }
                        NekiIconButton(
                            modifier = Modifier.padding(8.dp),
                            onClick = onClickMemo,
                        ) {
                            // TODO: ic_note 아이콘 리소스 추가 후 교체
                            Icon(
                                modifier = Modifier.size(28.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.icon_bookmark_stroked),
                                contentDescription = null,
                                tint = NekiTheme.colorScheme.gray700,
                            )
                        }
                    }
                },
                endContent = {
                    NekiIconButton(
                        modifier = Modifier.padding(8.dp),
                        onClick = onClickDelete,
                    ) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.icon_trash),
                            contentDescription = null,
                            tint = NekiTheme.colorScheme.gray700,
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun MemoTextField(
    memo: String,
    memoMode: MemoMode,
    onClickMemoMore: () -> Unit,
    onClickMemoFold: () -> Unit,
    onClickMemoText: () -> Unit,
    onClickMemoCancel: () -> Unit,
    onClickMemoDone: (String) -> Unit,
) {
    val isEditing = memoMode == MemoMode.Editing
    val isExpanded = memoMode == MemoMode.Expanded
    val isPreview = memoMode == MemoMode.Preview
    val memoState = remember { TextFieldState(initialText = memo) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(memo, memoMode) {
        if (memoMode != MemoMode.Editing) {
            memoState.edit { replace(0, length, memo) }
        }
    }

    LaunchedEffect(memoMode) {
        if (memoMode == MemoMode.Editing) {
            memoState.edit { replace(0, length, memo) }
            focusRequester.requestFocus()
        }
    }

    val textFieldStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp,
        letterSpacing = (-0.32).sp,
        color = NekiTheme.colorScheme.gray700,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None,
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isEditing) NekiTheme.colorScheme.gray25 else NekiTheme.colorScheme.white),
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
                    if (isPreview || isExpanded) Modifier.noRippleClickableSingle { onClickMemoText() } else Modifier,
                )
                .padding(
                    start = if (isEditing) 20.dp else 12.dp,
                    end = if (isEditing) 16.dp else 12.dp,
                    top = if (isPreview) 8.dp else 16.dp,
                    bottom = if (isEditing) 8.dp else 8.dp,
                ),
        ) {
            if (isPreview) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = memo.ifEmpty { "메모 없음" },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        style = textFieldStyle.copy(
                            color = if (memo.isEmpty()) NekiTheme.colorScheme.gray300
                            else NekiTheme.colorScheme.gray700,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "더보기",
                        modifier = Modifier
                            .noRippleClickableSingle { onClickMemoMore() }
                            .padding(8.dp),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 24.sp,
                            letterSpacing = (-0.32).sp,
                            color = NekiTheme.colorScheme.gray200,
                        ),
                    )
                }
            } else {
                BasicTextField(
                    state = memoState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (isExpanded) Modifier.padding(horizontal = 8.dp) else Modifier)
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (it.isFocused && isExpanded) onClickMemoText()
                        },
                    textStyle = textFieldStyle,
                    readOnly = isExpanded,
                    inputTransformation = InputTransformation.maxLength(100),
                    cursorBrush = SolidColor(
                        if (isEditing) NekiTheme.colorScheme.gray900 else Color.Transparent,
                    ),
                    decorator = { innerTextField ->
                        Box {
                            if (memoState.text.isEmpty()) {
                                Text(
                                    text = if (isEditing) "자유롭게 메모를 입력해주세요. (최대 100자)" else "메모 없음",
                                    style = textFieldStyle.copy(color = NekiTheme.colorScheme.gray300),
                                )
                            }
                            innerTextField()
                        }
                    },
                )
            }

            // Expanded: 메모 접기
            if (isExpanded) {
                Text(
                    text = "메모 접기",
                    modifier = Modifier
                        .align(Alignment.End)
                        .noRippleClickableSingle { onClickMemoFold() }
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp,
                        letterSpacing = (-0.32).sp,
                        color = NekiTheme.colorScheme.gray200,
                    ),
                )
            }

            // Editing: 카운터 + 전체지우기 + 취소/완료
            if (isEditing) {
                val charCount = memoState.text.length
                val hasText = charCount > 0

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$charCount/100",
                            style = TextStyle(
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                letterSpacing = (-0.24).sp,
                                color = NekiTheme.colorScheme.gray300,
                            ),
                        )
                        Text(
                            text = "전체 지우기",
                            modifier = Modifier
                                .noRippleClickableSingle { memoState.edit { replace(0, length, "") } }
                                .padding(8.dp),
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 16.sp,
                                letterSpacing = (-0.24).sp,
                                color = if (hasText) NekiTheme.colorScheme.gray600
                                else NekiTheme.colorScheme.gray200,
                            ),
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "취소",
                            modifier = Modifier
                                .noRippleClickableSingle { onClickMemoCancel() }
                                .padding(vertical = 10.dp),
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 24.sp,
                                letterSpacing = (-0.32).sp,
                                color = NekiTheme.colorScheme.gray800,
                            ),
                        )
                        Text(
                            text = "완료",
                            modifier = Modifier
                                .noRippleClickableSingle { onClickMemoDone(memoState.text.toString()) }
                                .padding(vertical = 10.dp),
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 24.sp,
                                letterSpacing = (-0.32).sp,
                                color = NekiTheme.colorScheme.primary500,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@ComponentPreview
@Composable
private fun PhotoDetailActionBarClosedPreview() {
    NekiTheme {
        PhotoDetailActionBar(
            isFavorite = false,
            memo = "",
            memoMode = MemoMode.Closed,
        )
    }
}

@ComponentPreview
@Composable
private fun PhotoDetailActionBarPreviewPreview() {
    NekiTheme {
        PhotoDetailActionBar(
            isFavorite = false,
            memo = "재밌었던 날인데 메모가 길어서 한 줄에 다 안 들어갈 수도 있어요",
            memoMode = MemoMode.Preview,
        )
    }
}

@ComponentPreview
@Composable
private fun PhotoDetailActionBarExpandedPreview() {
    NekiTheme {
        PhotoDetailActionBar(
            isFavorite = true,
            memo = "재밌었던 날인데 메모가 길어서 한 줄에 다 안 들어갈 수도 있어요",
            memoMode = MemoMode.Expanded,
        )
    }
}

@ComponentPreview
@Composable
private fun PhotoDetailActionBarEditingPreview() {
    NekiTheme {
        PhotoDetailActionBar(
            isFavorite = true,
            memo = "재밌었던 날",
            memoMode = MemoMode.Editing,
        )
    }
}

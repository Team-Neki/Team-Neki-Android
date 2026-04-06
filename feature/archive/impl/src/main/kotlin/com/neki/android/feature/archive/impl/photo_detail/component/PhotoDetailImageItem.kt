package com.neki.android.feature.archive.impl.photo_detail.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.feature.archive.impl.photo_detail.MemoMode

@Composable
internal fun PhotoDetailImageItem(
    imageUrl: String?,
    memo: String,
    memoMode: MemoMode,
    actionBarHeight: Dp = 0.dp,
    isScrollInProgress: Boolean,
    isTapEnabled: Boolean,
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit,
    onClickMemoMore: () -> Unit,
    onClickMemoText: () -> Unit,
    onClickMemoFold: () -> Unit,
    onClickMemoCancel: () -> Unit,
    onClickMemoDone: (String) -> Unit,
    onMemoTextChanged: (String) -> Unit,
) {
    val isMemoActive = memoMode == MemoMode.Expanded || memoMode == MemoMode.Editing
    val bottomPadding = if (memoMode == MemoMode.Editing) actionBarHeight else 0.dp

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomPadding),
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
        )

        if (isTapEnabled) {
            Row(modifier = Modifier.matchParentSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .noRippleClickableSingle {
                            if (!isScrollInProgress) onClickLeft()
                        },
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .noRippleClickableSingle {
                            if (!isScrollInProgress) onClickRight()
                        },
                )
            }
        }

        // dim 오버레이
        AnimatedVisibility(
            visible = isMemoActive,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x80202227))
                    .noRippleClickableSingle { onClickMemoFold() },
            )
        }

        // 메모 텍스트 영역
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .then(
                    if (memoMode == MemoMode.Editing) Modifier.imePadding()
                    else Modifier.windowInsetsPadding(
                        WindowInsets.ime.exclude(WindowInsets(bottom = actionBarHeight)),
                    ),
                ),
            visible = memoMode != MemoMode.Closed,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            MemoTextField(
                memo = memo,
                memoMode = memoMode,
                onClickMemoMore = onClickMemoMore,
                onClickMemoText = onClickMemoText,
                onClickMemoFold = onClickMemoFold,
                onClickMemoCancel = onClickMemoCancel,
                onClickMemoDone = onClickMemoDone,
                onMemoTextChanged = onMemoTextChanged,
            )
        }
    }
}

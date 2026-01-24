package com.neki.android.feature.pose.impl.main.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.ui.component.DoubleButtonOptionBottomSheet
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RandomPosePeopleCountBottomSheet(
    selectedCount: PeopleCount? = null,
    onDismissRequest: () -> Unit = {},
    onOptionSelected: (PeopleCount) -> Unit = {},
    onClickSelectButton: () -> Unit = {},
) {
    DoubleButtonOptionBottomSheet(
        title = "랜덤 포즈 추천을 위해\n촬영 중인 인원수를 선택해주세요",
        options = PeopleCount.entries.toImmutableList(),
        selectedOption = selectedCount,
        primaryButtonText = "선택하기",
        secondaryButtonText = "취소",
        onDismissRequest = onDismissRequest,
        onClickCancel = onDismissRequest,
        onClickActionButton = onClickSelectButton,
        onOptionSelect = onOptionSelected,
        buttonEnabled = selectedCount != null,
    )
}

@ComponentPreview
@Composable
private fun RandomPosePeopleCountBottomSheetPreview() {
    NekiTheme {
        RandomPosePeopleCountBottomSheet()
    }
}

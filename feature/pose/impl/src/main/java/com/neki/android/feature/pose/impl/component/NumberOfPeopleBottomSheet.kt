package com.neki.android.feature.pose.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.extension.clickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.designsystem.bottomsheet.BottomSheetDragHandle
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.feature.pose.impl.NumberOfPeople

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NumberOfPeopleBottomSheet(
    modifier: Modifier = Modifier,
    selectedItem: NumberOfPeople = NumberOfPeople.UNSELECTED,
    onDismissRequest: () -> Unit = {},
    onClickItem: (NumberOfPeople) -> Unit = {},
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        containerColor = NekiTheme.colorScheme.white,
        dragHandle = { BottomSheetDragHandle() },
    ) {
        NumberOfPeopleBottomSheetContent(
            selectedItem = selectedItem,
            onClickItem = onClickItem,
        )
    }
}

@Composable
private fun NumberOfPeopleBottomSheetContent(
    modifier: Modifier = Modifier,
    selectedItem: NumberOfPeople = NumberOfPeople.UNSELECTED,
    onClickItem: (NumberOfPeople) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Text(
            text = "인원 수",
            style = NekiTheme.typography.title20SemiBold,
            color = NekiTheme.colorScheme.gray900,
        )
        VerticalSpacer(16.dp)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            NumberOfPeople.entries.drop(1).forEach { item ->
                Row(
                    modifier = Modifier.clickableSingle { onClickItem(item) },
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (item == selectedItem) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.icon_check_primary),
                            contentDescription = null,
                            tint = Color.Unspecified,
                        )
                    }
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        text = item.displayText,
                        style = if (item == selectedItem) NekiTheme.typography.body16SemiBold else NekiTheme.typography.body16Medium,
                        color = if (item == selectedItem) NekiTheme.colorScheme.gray900 else NekiTheme.colorScheme.gray500,
                    )
                }
            }
        }
    }
}

@ComponentPreview
@Composable
private fun NumberOfPeopleBottomSheetContentPreview() {
    NekiTheme {
        NumberOfPeopleBottomSheetContent(
            selectedItem = NumberOfPeople.TWO,
        )
    }
}

@ComponentPreview
@Composable
private fun NumberOfPeopleBottomSheetPreview() {
    NekiTheme {
        NumberOfPeopleBottomSheet(
            selectedItem = NumberOfPeople.TWO,
        )
    }
}

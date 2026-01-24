package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.bottomsheet.BottomSheetDragHandle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.feature.map.impl.const.DirectionApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DirectionBottomSheet(
    onDismissRequest: () -> Unit = {},
    onClickDirectionItem: (DirectionApp) -> Unit = {},
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = NekiTheme.colorScheme.white,
        dragHandle = { BottomSheetDragHandle() },
    ) {
        DirectionBottomSheetContent(
            onClickItem = onClickDirectionItem,
        )
    }
}

@Composable
private fun DirectionBottomSheetContent(
    onClickItem: (DirectionApp) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Text(
            text = "길찾기 앱 선택",
            color = NekiTheme.colorScheme.gray900,
            style = NekiTheme.typography.title20SemiBold,
        )
        VerticalSpacer(10.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 18.5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            DirectionItem(
                app = DirectionApp.GOOGLE_MAP,
                onClickItem = { onClickItem(DirectionApp.GOOGLE_MAP) },
            )
            DirectionItem(
                app = DirectionApp.NAVER_MAP,
                onClickItem = { onClickItem(DirectionApp.NAVER_MAP) },
            )
            DirectionItem(
                app = DirectionApp.KAKAO_MAP,
                onClickItem = { onClickItem(DirectionApp.KAKAO_MAP) },
            )
        }
        VerticalSpacer(34.dp)
    }
}

@ComponentPreview
@Composable
private fun DirectionBottomSheetContentPreview() {
    NekiTheme {
        DirectionBottomSheetContent()
    }
}

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
import com.neki.android.feature.map.impl.const.DirectionAppConst

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DirectionBottomSheet(
    onDismissRequest: () -> Unit = {},
    onClickDirectionItem: (DirectionAppConst) -> Unit = {},
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = NekiTheme.colorScheme.white,
        dragHandle = { BottomSheetDragHandle() },
    ) {
        DirectionBottomSheetContent(
            onItemClick = onClickDirectionItem,
        )
    }
}

@Composable
private fun DirectionBottomSheetContent(
    onItemClick: (DirectionAppConst) -> Unit = {},
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
                .padding(vertical = 8.dp, horizontal = 22.5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            DirectionItem(
                app = DirectionAppConst.GOOGLE_MAP,
                onClickItem = { onItemClick(DirectionAppConst.GOOGLE_MAP) },
            )
            DirectionItem(
                app = DirectionAppConst.NAVER_MAP,
                onClickItem = { onItemClick(DirectionAppConst.NAVER_MAP) },
            )
            DirectionItem(
                app = DirectionAppConst.KAKAO_MAP,
                onClickItem = { onItemClick(DirectionAppConst.KAKAO_MAP) },
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

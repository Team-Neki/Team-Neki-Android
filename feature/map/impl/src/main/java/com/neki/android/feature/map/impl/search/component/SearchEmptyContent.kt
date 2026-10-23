package com.neki.android.feature.map.impl.search.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun SearchEmptyContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Image(
            modifier = Modifier.size(68.dp),
            painter = painterResource(R.drawable.icon_map_search_empty),
            contentDescription = null,
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "어디에서 네컷을 찍을까요?\n브랜드나 지점을 검색해보세요.",
                style = NekiTheme.typography.body16Medium,
                color = NekiTheme.colorScheme.gray500,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "지역 상세 검색은 현재 서울만 지원하고 있어요.",
                style = NekiTheme.typography.caption11Medium,
                color = NekiTheme.colorScheme.gray300,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun SearchEmptyContentPreview() {
    NekiTheme {
        SearchEmptyContent()
    }
}

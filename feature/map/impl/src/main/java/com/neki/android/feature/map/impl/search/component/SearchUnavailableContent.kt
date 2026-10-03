package com.neki.android.feature.map.impl.search.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
internal fun SearchNetworkErrorContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Box(
            modifier = Modifier.size(68.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.icon_network_disconnect),
                contentDescription = null,
                modifier = Modifier.size(68.dp),
            )
        }
        Text(
            text = "네트워크가 불안정해요.\n잠시 후 다시 시도해주세요.",
            style = NekiTheme.typography.body16Medium,
            color = NekiTheme.colorScheme.gray500,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
internal fun SearchNoResultsContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Box(
            modifier = Modifier.size(68.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.icon_gpic_sheet),
                contentDescription = null,
                modifier = Modifier.size(68.dp),
            )
        }
        Text(
            text = "조건에 맞는 포토부스가 없어요.\n다른 지역이나 브랜드로 검색해보세요.",
            style = NekiTheme.typography.body16Medium,
            color = NekiTheme.colorScheme.gray500,
            textAlign = TextAlign.Center,
        )
    }
}

@ComponentPreview
@Composable
private fun SearchNetworkErrorContentPreview() {
    NekiTheme {
        SearchNetworkErrorContent()
    }
}

@ComponentPreview
@Composable
private fun SearchNoResultsContentPreview() {
    NekiTheme {
        SearchNoResultsContent()
    }
}

package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.modifier.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.feature.map.impl.const.DirectionApp

@Composable
internal fun DirectionItem(
    app: DirectionApp,
    modifier: Modifier = Modifier,
    onClickItem: (DirectionApp) -> Unit = {},
) {
    Column(
        modifier = modifier
            .noRippleClickable(
                onClick = { onClickItem(app) },
            )
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .border(
                    width = 1.dp,
                    color = NekiTheme.colorScheme.gray50,
                    shape = RoundedCornerShape(12.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                model = app.appLogoRes,
                contentDescription = null,
            )
        }
        VerticalSpacer(8.dp)
        Text(
            text = app.appName,
            color = NekiTheme.colorScheme.gray900,
            style = NekiTheme.typography.body14Medium,
            textAlign = TextAlign.Center,
        )
    }
}

@ComponentPreview
@Composable
private fun GoogleMapDirectionItemPreview() {
    NekiTheme {
        DirectionItem(app = DirectionApp.GOOGLE_MAP)
    }
}

@ComponentPreview
@Composable
private fun NaverMapDirectionItemPreview() {
    NekiTheme {
        DirectionItem(app = DirectionApp.NAVER_MAP)
    }
}

@ComponentPreview
@Composable
private fun KakaoMapDirectionItemPreview() {
    NekiTheme {
        DirectionItem(app = DirectionApp.KAKAO_MAP)
    }
}

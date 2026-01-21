package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.neki.android.core.designsystem.extension.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.feature.map.impl.const.DirectionAppConst

@Composable
fun DirectionItem(
    app: DirectionAppConst,
    modifier: Modifier = Modifier,
    onClickItem: (DirectionAppConst) -> Unit = {},
) {
    Column(
        modifier = modifier
            .noRippleClickable(
                onClick = { onClickItem(app) },
            ),
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
        DirectionItem(app = DirectionAppConst.GOOGLE_MAP)
    }
}

@ComponentPreview
@Composable
private fun NaverMapDirectionItemPreview() {
    NekiTheme {
        DirectionItem(app = DirectionAppConst.NAVER_MAP)
    }
}

@ComponentPreview
@Composable
private fun KakaoMapDirectionItemPreview() {
    NekiTheme {
        DirectionItem(app = DirectionAppConst.KAKAO_MAP)
    }
}

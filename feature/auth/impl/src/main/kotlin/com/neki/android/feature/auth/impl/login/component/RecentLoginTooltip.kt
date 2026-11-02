package com.neki.android.feature.auth.impl.login.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun RecentLoginTooltip(
    modifier: Modifier = Modifier,
) {
    val backgroundColor = NekiTheme.colorScheme.gray800

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
    ) {
        Row(
            modifier = Modifier
                .background(color = backgroundColor, shape = RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier.size(12.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_clock),
                contentDescription = null,
            )
            Text(
                text = "최근 로그인",
                style = NekiTheme.typography.caption11Medium.copy(
                    fontSize = 10.5.sp,
                    lineHeight = 15.sp,
                ),
                color = NekiTheme.colorScheme.white,
            )
        }
        Canvas(
            modifier = Modifier
                .padding(end = 13.5.dp)
                .size(width = 7.5.dp, height = 6.dp),
        ) {
            val cornerRadius = 1.dp.toPx()
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width / 2 - cornerRadius, size.height - cornerRadius)
                quadraticTo(size.width / 2, size.height, size.width / 2 + cornerRadius, size.height - cornerRadius)
                lineTo(size.width, 0f)
                close()
            }
            drawPath(path, backgroundColor)
        }
    }
}

@ComponentPreview
@Composable
private fun RecentLoginTooltipPreview() {
    NekiTheme {
        RecentLoginTooltip()
    }
}

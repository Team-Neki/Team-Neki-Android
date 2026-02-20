package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.buttonShadow
import com.neki.android.core.designsystem.modifier.clickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun MapRefreshChip(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .buttonShadow()
            .clip(CircleShape)
            .clickableSingle(onClick = onClick)
            .background(
                shape = CircleShape,
                color = NekiTheme.colorScheme.white,
            )
            .border(
                width = 1.dp,
                shape = CircleShape,
                color = NekiTheme.colorScheme.primary400,
            )
            .padding(horizontal = 13.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = ImageVector.vectorResource(R.drawable.icon_rotate),
            contentDescription = null,
            tint = NekiTheme.colorScheme.primary400,
        )
        Text(
            text = "이 지역 재검색",
            style = NekiTheme.typography.body14SemiBold,
            color = NekiTheme.colorScheme.gray800,
        )
    }
}

@ComponentPreview
@Composable
private fun MapRefreshChipPreview() {
    NekiTheme {
        MapRefreshChip()
    }
}

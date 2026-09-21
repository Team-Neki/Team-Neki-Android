package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
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
import com.neki.android.core.designsystem.modifier.buttonShadow
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun MapSearchBar(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .buttonShadow(shape = CircleShape)
            .background(
                color = NekiTheme.colorScheme.white,
                shape = CircleShape,
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(R.drawable.icon_neki),
            contentDescription = null,
            tint = Color.Unspecified,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = "포토 부스 검색하기",
            color = NekiTheme.colorScheme.gray800,
            style = NekiTheme.typography.body16Medium,
        )
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(R.drawable.icon_search),
            contentDescription = null,
            tint = Color.Unspecified,
        )
    }
}

@ComponentPreview
@Composable
private fun MapSearchBarPreview() {
    NekiTheme {
        MapSearchBar()
    }
}

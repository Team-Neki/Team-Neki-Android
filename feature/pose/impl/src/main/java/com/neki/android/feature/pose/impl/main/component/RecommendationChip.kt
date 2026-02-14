package com.neki.android.feature.pose.impl.main.component

import androidx.compose.foundation.background
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
internal fun RecommendationChip(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .buttonShadow()
            .clickableSingle(onClick = onClick)
            .background(shape = CircleShape, color = NekiTheme.colorScheme.gray800)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(R.drawable.icon_repeat),
            contentDescription = null,
            tint = NekiTheme.colorScheme.primary400,
        )
        Text(
            text = "랜덤 포즈 추천",
            style = NekiTheme.typography.title18SemiBold,
            color = NekiTheme.colorScheme.white,
        )
    }
}

@ComponentPreview
@Composable
private fun RecommendationChipPreview() {
    NekiTheme {
        RecommendationChip(modifier = Modifier.padding(8.dp))
    }
}

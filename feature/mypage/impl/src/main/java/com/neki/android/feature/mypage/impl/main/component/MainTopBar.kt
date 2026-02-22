package com.neki.android.feature.mypage.impl.main.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.button.NekiIconButton
import com.neki.android.core.designsystem.topbar.NekiLeftTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun MainTopBar(
    modifier: Modifier = Modifier,
    onClickIcon: () -> Unit = {},
) {
    NekiLeftTitleTopBar(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 8.dp),
        title = {
            Text(
                text = "마이페이지",
                style = NekiTheme.typography.title20SemiBold,
                color = NekiTheme.colorScheme.gray900,
            )
        },
        actions = {
            NekiIconButton(
                onClick = onClickIcon,
                contentPadding = PaddingValues(8.dp),
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_bell),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
            }
        },
    )
}

@ComponentPreview
@Composable
private fun MainTopBarPreview() {
    NekiTheme {
        MainTopBar()
    }
}

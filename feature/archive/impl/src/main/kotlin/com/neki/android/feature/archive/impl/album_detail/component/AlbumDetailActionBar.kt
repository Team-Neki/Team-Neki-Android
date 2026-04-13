package com.neki.android.feature.archive.impl.album_detail.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.actionbar.NekiActionBar
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.archive.impl.R
import com.neki.android.core.designsystem.R as DesignR

@Composable
internal fun AlbumDetailActionBar(
    visible: Boolean,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onClickDownload: () -> Unit = {},
    onClickCopy: () -> Unit = {},
    onClickMove: () -> Unit = {},
    onClickDelete: () -> Unit = {},
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = expandVertically(expandFrom = Alignment.Top),
        exit = shrinkVertically(shrinkTowards = Alignment.Top),
    ) {
        NekiActionBar(
            modifier = Modifier.fillMaxWidth(),
            padding = PaddingValues(top = 8.dp, bottom = 10.dp, start = 20.dp, end = 20.dp),
        ) {
            ActionBarItem(
                iconRes = DesignR.drawable.icon_download,
                label = "다운로드",
                isEnabled = isEnabled,
                onClick = onClickDownload,
                modifier = Modifier.width(48.dp),
            )
            ActionBarItem(
                iconRes = R.drawable.icon_copy_photo,
                label = "사진 복제",
                isEnabled = isEnabled,
                onClick = onClickCopy,
                modifier = Modifier.width(48.dp),
            )
            ActionBarItem(
                iconRes = R.drawable.icon_move_photo,
                label = "사진 이동",
                isEnabled = isEnabled,
                onClick = onClickMove,
                modifier = Modifier.width(48.dp),
            )
            ActionBarItem(
                iconRes = DesignR.drawable.icon_trash,
                label = "삭제",
                isEnabled = isEnabled,
                onClick = onClickDelete,
                modifier = Modifier.width(48.dp),
            )
        }
    }
}

@Composable
private fun ActionBarItem(
    iconRes: Int,
    label: String,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (isEnabled) NekiTheme.colorScheme.gray700
    else NekiTheme.colorScheme.gray200

    Column(
        modifier = modifier.noRippleClickableSingle(enabled = isEnabled, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            modifier = Modifier.size(28.dp),
            imageVector = ImageVector.vectorResource(iconRes),
            contentDescription = null,
            tint = contentColor,
        )
        Text(
            modifier = Modifier.wrapContentWidth(unbounded = true),
            text = label,
            style = NekiTheme.typography.caption12Medium,
            color = contentColor,
        )
    }
}

@ComponentPreview
@Composable
private fun AlbumDetailActionBarEnabledPreview() {
    NekiTheme {
        AlbumDetailActionBar(visible = true, isEnabled = true)
    }
}

@ComponentPreview
@Composable
private fun AlbumDetailActionBarDisabledPreview() {
    NekiTheme {
        AlbumDetailActionBar(visible = true, isEnabled = false)
    }
}

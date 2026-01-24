package com.neki.android.core.designsystem.button

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.extension.MultipleEventsCutter
import com.neki.android.core.designsystem.extension.get
import com.neki.android.core.designsystem.ui.theme.NekiTheme

/**
 * 중복 클릭 방지 기능이 포함된 아이콘 버튼 컴포넌트
 *
 * @param onClick 클릭 이벤트 핸들러
 * @param enabled 버튼 활성화 여부
 * @param multipleEventsCutterEnabled 중복 클릭 방지 활성화 여부
 *
 * Note: IconButton의 default size는 48.dp
 */
@Composable
fun NekiIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    multipleEventsCutterEnabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }
    IconButton(
        modifier = modifier,
        onClick = {
            if (multipleEventsCutterEnabled) {
                multipleEventsCutter.processEvent { onClick() }
            } else {
                onClick()
            }
        },
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
    ) {
        content()
    }
}

@ComponentPreview
@Composable
private fun NekiIconButtonPreview() {
    NekiTheme {
        NekiIconButton(onClick = {}) {
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_close),
                contentDescription = null,
            )
        }
    }
}

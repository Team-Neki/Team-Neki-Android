package com.neki.android.feature.map.impl.search.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.button.NekiIconButton
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun SearchField(
    query: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    focusRequester: FocusRequester = remember { FocusRequester() },
    onQueryChange: (String) -> Unit = {},
    onSubmit: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .border(1.dp, NekiTheme.colorScheme.gray100, CircleShape)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NekiIconButton(
            modifier = Modifier.size(48.dp),
            enabled = enabled,
            onClick = onBack,
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_left),
                contentDescription = "뒤로가기",
                tint = NekiTheme.colorScheme.gray800,
            )
        }
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .semantics { contentDescription = "브랜드, 지점명, 지역 검색" },
            enabled = enabled,
            singleLine = true,
            textStyle = NekiTheme.typography.body16Medium.copy(color = NekiTheme.colorScheme.gray800),
            cursorBrush = SolidColor(NekiTheme.colorScheme.gray800),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
            decorationBox = { innerTextField ->
                Box {
                    if (query.isEmpty()) {
                        Text(
                            text = "브랜드, 지점명, 지역을 검색해보세요",
                            style = NekiTheme.typography.body16Medium,
                            color = NekiTheme.colorScheme.gray300,
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                        )
                    }
                    innerTextField()
                }
            },
        )
        NekiIconButton(
            modifier = Modifier.size(48.dp),
            enabled = enabled && query.isNotBlank(),
            onClick = onSubmit,
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_search),
                contentDescription = "검색",
                tint = NekiTheme.colorScheme.gray900,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun SearchFieldPreview() {
    NekiTheme {
        SearchField(
            query = "",
        )
    }
}

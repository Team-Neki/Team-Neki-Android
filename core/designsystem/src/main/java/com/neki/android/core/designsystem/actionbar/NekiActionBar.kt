package com.neki.android.core.designsystem.actionbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.button.NekiIconButton
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun NekiStartActionBar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = NekiTheme.colorScheme.gray75,
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart,
        ) {
            content()
        }
    }
}

@Composable
fun NekiEndActionBar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = NekiTheme.colorScheme.gray75,
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            content()
        }
    }
}

@Composable
fun NekiBothSidesActionBar(
    modifier: Modifier = Modifier,
    startContent: @Composable () -> Unit,
    endContent: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = NekiTheme.colorScheme.gray75,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            startContent()
            endContent()
        }
    }
}

@ComponentPreview
@Composable
private fun NekiStartActionBarPreview() {
    NekiTheme {
        NekiStartActionBar(
            modifier = Modifier.fillMaxWidth(),
        ) {
            NekiIconButton(
                modifier = Modifier.padding(8.dp),
                onClick = {},
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_left),
                    contentDescription = null,
                    tint = NekiTheme.colorScheme.gray900,
                )
            }
        }
    }
}

@ComponentPreview
@Composable
private fun NekiEndActionBarPreview() {
    NekiTheme {
        NekiEndActionBar(
            modifier = Modifier.fillMaxWidth(),
        ) {
            NekiIconButton(
                modifier = Modifier.padding(8.dp),
                onClick = {},
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.icon_bookmark_stroked),
                    contentDescription = null,
                    tint = NekiTheme.colorScheme.gray500,
                )
            }
        }
    }
}

@ComponentPreview
@Composable
private fun NekiBothSidesActionBarPreview() {
    NekiTheme {
        NekiBothSidesActionBar(
            modifier = Modifier.fillMaxWidth(),
            startContent = {
                NekiIconButton(
                    modifier = Modifier.padding(8.dp),
                    onClick = {},
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_left),
                        contentDescription = null,
                        tint = NekiTheme.colorScheme.gray900,
                    )
                }
            },
            endContent = {
                NekiIconButton(
                    modifier = Modifier.padding(8.dp),
                    onClick = {},
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.icon_bookmark_stroked),
                        contentDescription = null,
                        tint = NekiTheme.colorScheme.gray500,
                    )
                }
            },
        )
    }
}

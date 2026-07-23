package com.neki.android.feature.notification.impl.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun NotificationEmptyContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            modifier = Modifier.size(72.dp),
            painter = painterResource(R.drawable.image_bell_fill),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = "아직 받은 알림이 없어요.",
            style = NekiTheme.typography.body16Medium,
            color = NekiTheme.colorScheme.gray400,
        )
    }
}

@ComponentPreview
@Composable
private fun NotificationEmptyContentPreview() {
    NekiTheme {
        NotificationEmptyContent()
    }
}

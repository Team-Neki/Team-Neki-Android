package com.neki.android.core.designsystem

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Default",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    uiMode = UI_MODE_NIGHT_NO,
    device = "spec:width=375dp,height=812dp,dpi=480",
)
annotation class DevicePreview

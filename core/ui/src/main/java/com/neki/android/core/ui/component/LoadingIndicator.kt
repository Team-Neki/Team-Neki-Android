package com.neki.android.core.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.R

@Composable
fun LoadingDialog(
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    properties: DialogProperties = DialogProperties(),
    onDismissRequest: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        LoadingIndicator(
            modifier = modifier.size(size),
        )
    }
}

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.loading_animation),
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier.size(size),
    )
}

@ComponentPreview
@Composable
private fun LoadingDialogPreview() {
    NekiTheme {
        LoadingDialog(
            onDismissRequest = {},
        )
    }
}

@ComponentPreview
@Composable
private fun LoadingIndicatorPreview() {
    NekiTheme {
        LoadingIndicator()
    }
}

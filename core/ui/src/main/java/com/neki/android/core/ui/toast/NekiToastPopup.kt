package com.neki.android.core.ui.toast

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.compose.LocalSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.toast.ToastActionPopup
import com.neki.android.core.designsystem.toast.ToastPopup
import com.neki.android.core.designsystem.ui.theme.NekiTheme

class NekiToastPopup(
    private val context: Context
) : Toast(context) {
    @Composable
    private fun MakeText(
        toastPopup: @Composable () -> Unit,
        duration: Int = LENGTH_SHORT,
    ) {
        val density = LocalDensity.current
        val composeView = ComposeView(context)

        composeView.setContent {
            NekiTheme {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    toastPopup()
                }
            }
        }

        composeView.apply {
            setViewTreeLifecycleOwner(LocalLifecycleOwner.current)
            setViewTreeSavedStateRegistryOwner(LocalSavedStateRegistryOwner.current)
            setViewTreeViewModelStoreOwner(LocalViewModelStoreOwner.current)
        }

        this.duration = duration
        view = composeView
        setGravity(
            Gravity.FILL_HORIZONTAL or Gravity.BOTTOM,
            0,
            with(density) { 28.dp.toPx().toInt() }
        )

        show()
    }

    @Composable
    fun ShowToastPopup(
        text: String,
        @DrawableRes iconRes: Int= R.drawable.icon_checkbox_on_24,
        duration: Int = LENGTH_SHORT
    ) {
        MakeText(
            toastPopup = {
                ToastPopup(
                    iconRes = iconRes,
                    text = text,
                )
            },
            duration = duration
        )
    }

    @Composable
    fun ShowToastActionPopup(
        @DrawableRes iconRes: Int,
        text: String,
        buttonText: String,
        onClickActionButton: () -> Unit,
        duration: Int = LENGTH_SHORT
    ) {
        MakeText(
            toastPopup = {
                ToastActionPopup(
                    iconRes = iconRes,
                    text = text,
                    buttonText = buttonText,
                    onClickActionButton = onClickActionButton
                )
            },
            duration = duration
        )
    }
}

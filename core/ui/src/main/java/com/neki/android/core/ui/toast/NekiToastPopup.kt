package com.neki.android.core.ui.toast

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.toast.ToastActionPopup
import com.neki.android.core.designsystem.toast.ToastPopup
import com.neki.android.core.designsystem.ui.theme.NekiTheme

class NekiToastPopup(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val savedStateRegistryOwner: SavedStateRegistryOwner,
    private val viewModelStoreOwner: ViewModelStoreOwner,
) : Toast(context) {
    private fun makeText(
        duration: Int = LENGTH_SHORT,
        toastPopup: @Composable () -> Unit,
    ) {
        val composeView = ComposeView(context).apply {
            setContent {
                NekiTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                    ) {
                        toastPopup()
                    }
                }
            }

            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
            setViewTreeViewModelStoreOwner(viewModelStoreOwner)
        }

        this.duration = duration
        view = composeView
        setGravity(
            Gravity.FILL_HORIZONTAL or Gravity.BOTTOM,
            0,
            (28 * Resources.getSystem().displayMetrics.density).toInt(),
        )

        show()
    }

    fun showToastPopup(
        text: String,
        @DrawableRes iconRes: Int = R.drawable.icon_checkbox_on_24,
        duration: Int = LENGTH_SHORT,
    ) {
        makeText(
            duration = duration,
        ) {
            ToastPopup(
                iconRes = iconRes,
                text = text,
            )
        }
    }

    fun showToastActionPopup(
        @DrawableRes iconRes: Int,
        text: String,
        buttonText: String,
        onClickActionButton: () -> Unit,
        duration: Int = LENGTH_SHORT,
    ) {
        makeText(
            duration = duration,
        ) {
            ToastActionPopup(
                iconRes = iconRes,
                text = text,
                buttonText = buttonText,
                onClickActionButton = onClickActionButton,
            )
        }
    }
}

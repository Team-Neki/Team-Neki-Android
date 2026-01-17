package com.neki.android.core.ui.toast

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.toast.NekiActionToast
import com.neki.android.core.designsystem.toast.NekiToast
import com.neki.android.core.designsystem.ui.theme.NekiTheme

class NekiToast(
    private val context: Context,
) : Toast(context) {
    private fun makeText(
        duration: Int = LENGTH_SHORT,
        toast: @Composable () -> Unit,
    ) {
        val activity = context as ComponentActivity

        val composeView = ComposeView(context).apply {
            setContent {
                NekiTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                    ) {
                        toast()
                    }
                }
            }

            setViewTreeLifecycleOwner(activity)
            setViewTreeSavedStateRegistryOwner(activity)
            setViewTreeViewModelStoreOwner(activity)
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

    fun showToast(
        text: String,
        @DrawableRes iconRes: Int = R.drawable.icon_checkbox_on_24,
        duration: Int = LENGTH_SHORT,
    ) {
        makeText(
            duration = duration,
        ) {
            NekiToast(
                iconRes = iconRes,
                text = text,
            )
        }
    }

    fun showActionToast(
        @DrawableRes iconRes: Int,
        text: String,
        buttonText: String,
        onClickActionButton: () -> Unit,
        duration: Int = LENGTH_SHORT,
    ) {
        makeText(
            duration = duration,
        ) {
            NekiActionToast(
                iconRes = iconRes,
                text = text,
                buttonText = buttonText,
                onClickActionButton = onClickActionButton,
            )
        }
    }
}

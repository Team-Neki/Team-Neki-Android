package com.neki.android.core.ui.toast

import android.content.Context
import android.content.res.Resources
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.toast.NekiActionToast
import com.neki.android.core.designsystem.toast.NekiToast
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NekiToast(
    private val context: Context,
) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private fun makeToast(
        duration: Int = Toast.LENGTH_SHORT,
        toast: @Composable (dismiss: () -> Unit) -> Unit,
    ) {
        val activity = context as ComponentActivity
        var dismissJob: Job? = null
        lateinit var composeView: ComposeView

        fun dismiss() {
            dismissJob?.cancel()
            if (composeView.isAttachedToWindow) {
                windowManager.removeView(composeView)
            }
        }

        composeView = ComposeView(context).apply {
            setContent {
                NekiTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                    ) {
                        toast { dismiss() }
                    }
                }
            }

            setViewTreeLifecycleOwner(activity)
            setViewTreeSavedStateRegistryOwner(activity)
            setViewTreeViewModelStoreOwner(activity)
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT,
        ).apply {
            windowAnimations = android.R.style.Animation_Toast
            gravity = Gravity.BOTTOM or Gravity.FILL_HORIZONTAL
            y = (28 * Resources.getSystem().displayMetrics.density).toInt()
        }

        windowManager.addView(composeView, params)

        dismissJob = activity.lifecycleScope.launch(Dispatchers.Main) {
            delay(if (duration == Toast.LENGTH_SHORT) 2500L else 3500L)
            dismiss()
        }
    }

    fun showToast(
        text: String,
        @DrawableRes iconRes: Int = R.drawable.icon_checkbox_on,
        duration: Int = Toast.LENGTH_SHORT,
    ) {
        makeToast(duration = duration) {
            NekiToast(
                iconRes = iconRes,
                text = text,
            )
        }
    }

    fun showActionToast(
        text: String,
        buttonText: String,
        @DrawableRes iconRes: Int = R.drawable.icon_checkbox_on,
        duration: Int = Toast.LENGTH_SHORT,
        onClickActionButton: () -> Unit,
    ) {
        makeToast(duration = duration) { dismiss ->
            NekiActionToast(
                iconRes = iconRes,
                text = text,
                buttonText = buttonText,
                onClickActionButton = {
                    dismiss()
                    onClickActionButton()
                },
            )
        }
    }
}

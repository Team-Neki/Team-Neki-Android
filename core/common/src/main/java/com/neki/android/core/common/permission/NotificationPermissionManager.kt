package com.neki.android.core.common.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat

object NotificationPermissionManager {
    const val NOTIFICATION_PERMISSION = Manifest.permission.POST_NOTIFICATIONS

    fun isGrantedNotificationPermission(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun shouldShowNotificationRationale(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.shouldShowRequestPermissionRationale(NOTIFICATION_PERMISSION)
        } else false
    }
}

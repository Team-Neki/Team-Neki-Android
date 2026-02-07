package com.neki.android.core.common.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

object NotificationPermissionManager {
    const val NOTIFICATION_PERMISSION = Manifest.permission.POST_NOTIFICATIONS

    fun isGrantedNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, NOTIFICATION_PERMISSION) == PermissionChecker.PERMISSION_GRANTED
        } else NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun shouldShowNotificationRationale(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.shouldShowRequestPermissionRationale(NOTIFICATION_PERMISSION)
        } else false
    }
}

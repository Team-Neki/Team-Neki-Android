package com.neki.android.core.common.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

object CameraPermissionManager {
    const val CAMERA_PERMISSION = Manifest.permission.CAMERA

    fun isGrantedCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, CAMERA_PERMISSION) == PermissionChecker.PERMISSION_GRANTED
    }

    fun shouldShowCameraRationale(activity: Activity): Boolean {
        return activity.shouldShowRequestPermissionRationale(CAMERA_PERMISSION)
    }
}

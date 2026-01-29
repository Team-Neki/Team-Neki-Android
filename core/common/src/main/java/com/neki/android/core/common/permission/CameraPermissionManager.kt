package com.neki.android.core.common.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

object CameraPermissionManager {
    const val CAMERA_PERMISSION = Manifest.permission.CAMERA

    fun isGrantedCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA,
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    fun shouldShowCameraRationale(activity: Activity): Boolean {
        return activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
    }
}
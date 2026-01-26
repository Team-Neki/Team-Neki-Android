package com.neki.android.core.common.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

object LocationPermissionManager {
    val LOCATION_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    fun isGrantedLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PermissionChecker.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PermissionChecker.PERMISSION_GRANTED
    }

    fun shouldShowLocationRationale(activity: Activity): Boolean {
        return activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

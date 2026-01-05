package com.neki.android.feature.photo_upload.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.Navigator
import kotlinx.serialization.Serializable

sealed interface PhotoUploadNavKey : NavKey {

    @Serializable
    data object QRScan : PhotoUploadNavKey
}

fun Navigator.navigateToQRScan() {
    navigate(PhotoUploadNavKey.QRScan)
}

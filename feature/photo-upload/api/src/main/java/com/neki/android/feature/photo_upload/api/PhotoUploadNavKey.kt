package com.neki.android.feature.photo_upload.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.main.MainNavigator
import kotlinx.serialization.Serializable

sealed interface PhotoUploadNavKey : NavKey {

    @Serializable
    data object QRScan : PhotoUploadNavKey
}

fun MainNavigator.navigateToQRScan() {
    navigate(PhotoUploadNavKey.QRScan)
}

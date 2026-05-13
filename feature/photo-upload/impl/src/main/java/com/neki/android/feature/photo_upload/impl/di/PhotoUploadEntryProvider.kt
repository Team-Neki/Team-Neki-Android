package com.neki.android.feature.photo_upload.impl.di

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.main.EntryProviderInstaller
import com.neki.android.core.navigation.main.MainNavigator
import com.neki.android.core.navigation.result.LocalResultEventBus
import com.neki.android.feature.photo_upload.api.PhotoUploadNavKey
import com.neki.android.feature.photo_upload.api.QRScanResult
import com.neki.android.feature.photo_upload.impl.qrscan.QRScanRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object PhotoUploadEntryProvider {

    @IntoSet
    @Provides
    fun providePhotoUploadEntryBuilder(navigator: MainNavigator): EntryProviderInstaller = {
        photoUploadEntry(navigator)
    }
}

private fun EntryProviderScope<NavKey>.photoUploadEntry(navigator: MainNavigator) {
    entry<PhotoUploadNavKey.QRScan> {
        val resultBus = LocalResultEventBus.current

        QRScanRoute(
            navigateBack = navigator::goBack,
            setQRResult = { resultBus.sendResult<QRScanResult>(result = it) },
        )
    }
}

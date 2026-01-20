package com.neki.android.feature.photo_upload.impl.di

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.EntryProviderInstaller
import com.neki.android.core.navigation.Navigator
import com.neki.android.feature.archive.api.navigateToAlbumDetail
import com.neki.android.feature.photo_upload.api.PhotoUploadNavKey
import com.neki.android.feature.photo_upload.impl.album.UploadAlbumRoute
import com.neki.android.feature.photo_upload.impl.album.UploadAlbumViewModel
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
    fun providePhotoUploadEntryBuilder(navigator: Navigator): EntryProviderInstaller = {
        photoUploadEntry(navigator)
    }
}

private fun EntryProviderScope<NavKey>.photoUploadEntry(navigator: Navigator) {
    entry<PhotoUploadNavKey.QRScan> {
        QRScanRoute(
            navigateBack = navigator::goBack,
            navigateToHome = {},
        )
    }
    entry<PhotoUploadNavKey.UploadAlbum> { key ->
        UploadAlbumRoute(
            viewModel = hiltViewModel<UploadAlbumViewModel, UploadAlbumViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(key.uriStrings)
                },
            ),
            navigateBack = navigator::goBack,
            navigateToAlbumDetail = { id ->
                navigator.remove(key)
                navigator.navigateToAlbumDetail(id = id)
            },
        )
    }
}

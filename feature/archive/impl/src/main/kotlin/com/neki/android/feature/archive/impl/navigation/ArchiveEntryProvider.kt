package com.neki.android.feature.archive.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.EntryProviderInstaller
import com.neki.android.core.navigation.Navigator
import com.neki.android.core.navigation.result.LocalResultEventBus
import com.neki.android.core.navigation.result.ResultEffect
import com.neki.android.feature.archive.api.ArchiveNavKey
import com.neki.android.feature.archive.api.navigateToAlbumDetail
import com.neki.android.feature.archive.api.navigateToAllAlbum
import com.neki.android.feature.archive.api.navigateToAllPhoto
import com.neki.android.feature.archive.api.navigateToPhotoDetail
import com.neki.android.feature.archive.impl.album.AllAlbumRoute
import com.neki.android.feature.archive.impl.album_detail.AlbumDetailRoute
import com.neki.android.feature.archive.impl.album_detail.AlbumDetailViewModel
import com.neki.android.feature.archive.impl.main.ArchiveMainIntent
import com.neki.android.feature.archive.impl.main.ArchiveMainRoute
import com.neki.android.feature.archive.impl.main.ArchiveMainViewModel
import com.neki.android.feature.archive.impl.photo.AllPhotoRoute
import com.neki.android.feature.archive.impl.photo_detail.PhotoDetailRoute
import com.neki.android.feature.archive.impl.photo_detail.PhotoDetailViewModel
import com.neki.android.feature.photo_upload.api.QRScanResult
import com.neki.android.feature.photo_upload.api.navigateToQRScan
import com.neki.android.feature.photo_upload.api.navigateToUploadAlbum
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object ArchiveEntryProviderModule {

    @IntoSet
    @Provides
    fun provideArchiveEntryBuilder(navigator: Navigator): EntryProviderInstaller = {
        archiveEntry(navigator)
    }
}

private fun EntryProviderScope<NavKey>.archiveEntry(navigator: Navigator) {
    entry<ArchiveNavKey.Archive> {
        val resultBus = LocalResultEventBus.current
        val viewModel = hiltViewModel<ArchiveMainViewModel>()
        ResultEffect<QRScanResult>(resultBus) { result ->
            when (result) {
                is QRScanResult.QRCodeScanned -> {
                    viewModel.store.onIntent(ArchiveMainIntent.QRCodeScanned(result.imageUrl))
                }

                QRScanResult.OpenGallery -> {
                    viewModel.store.onIntent(ArchiveMainIntent.ClickGalleryUploadRow)
                }
            }
        }
        ResultEffect<Boolean>(resultBus) { hasUpdated ->
            if (hasUpdated) viewModel.store.onIntent(ArchiveMainIntent.RefreshArchiveMainScreen)
        }

        ArchiveMainRoute(
            viewModel = viewModel,
            navigateToQRScan = navigator::navigateToQRScan,
            navigateToUploadAlbumWithGallery = navigator::navigateToUploadAlbum,
            navigateToUploadAlbumWithQRScan = navigator::navigateToUploadAlbum,
            navigateToAllAlbum = navigator::navigateToAllAlbum,
            navigateToFavoriteAlbum = { id ->
                navigator.navigateToAlbumDetail(
                    id = id,
                    isFavorite = true,
                )
            },
            navigateToAlbumDetail = { id, title ->
                navigator.navigateToAlbumDetail(
                    id = id,
                    title = title,
                    isFavorite = false,
                )
            },
            navigateToAllPhoto = navigator::navigateToAllPhoto,
            navigateToPhotoDetail = navigator::navigateToPhotoDetail,
        )
    }

    entry<ArchiveNavKey.AllPhoto> {
        AllPhotoRoute(
            navigateBack = navigator::goBack,
            navigateToPhotoDetail = navigator::navigateToPhotoDetail,
        )
    }

    entry<ArchiveNavKey.AllAlbum> {
        AllAlbumRoute(
            navigateBack = navigator::goBack,
            navigateToFavoriteAlbum = { id ->
                navigator.navigateToAlbumDetail(
                    id = id,
                    isFavorite = true,
                )
            },
            navigateToAlbumDetail = { id, title ->
                navigator.navigateToAlbumDetail(
                    id = id,
                    title = title,
                    isFavorite = false,
                )
            },
        )
    }
    entry<ArchiveNavKey.AlbumDetail> { key ->
        AlbumDetailRoute(
            viewModel = hiltViewModel<AlbumDetailViewModel, AlbumDetailViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(key.albumId, key.title, key.isFavorite)
                },
            ),
            navigateBack = navigator::goBack,
            navigateToPhotoDetail = navigator::navigateToPhotoDetail,
        )
    }

    entry<ArchiveNavKey.PhotoDetail> { key ->
        PhotoDetailRoute(
            viewModel = hiltViewModel<PhotoDetailViewModel, PhotoDetailViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(key.photo)
                },
            ),
            navigateBack = navigator::goBack,
        )
    }
}

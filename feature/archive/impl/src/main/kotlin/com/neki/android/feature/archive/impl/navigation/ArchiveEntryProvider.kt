package com.neki.android.feature.archive.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.main.EntryProviderInstaller
import com.neki.android.core.navigation.main.MainNavigator
import com.neki.android.core.navigation.result.LocalResultEventBus
import com.neki.android.core.navigation.result.ResultEffect
import com.neki.android.feature.archive.api.AlbumDetailResult
import com.neki.android.feature.archive.api.AllAlbumResult
import com.neki.android.feature.archive.api.AllPhotoResult
import com.neki.android.feature.archive.api.ArchiveNavKey
import com.neki.android.feature.archive.api.PhotoCopiedResult
import com.neki.android.feature.archive.api.PhotoDetailResult
import com.neki.android.feature.archive.api.PhotoMovedResult
import com.neki.android.feature.archive.api.PhotoUploadedResult
import com.neki.android.feature.archive.api.navigateToAlbumDetail
import com.neki.android.feature.archive.api.navigateToAllAlbum
import com.neki.android.feature.archive.api.navigateToAllPhoto
import com.neki.android.feature.archive.api.navigateToPhotoDetail
import com.neki.android.feature.archive.impl.album.AllAlbumIntent
import com.neki.android.feature.archive.impl.album.AllAlbumRoute
import com.neki.android.feature.archive.impl.album.AllAlbumViewModel
import com.neki.android.feature.archive.impl.album_detail.AlbumDetailIntent
import com.neki.android.feature.archive.impl.album_detail.AlbumDetailRoute
import com.neki.android.feature.archive.impl.album_detail.AlbumDetailViewModel
import com.neki.android.feature.archive.impl.main.ArchiveMainIntent
import com.neki.android.feature.archive.impl.main.ArchiveMainRoute
import com.neki.android.feature.archive.impl.main.ArchiveMainViewModel
import com.neki.android.feature.archive.impl.photo.AllPhotoIntent
import com.neki.android.feature.archive.impl.photo.AllPhotoRoute
import com.neki.android.feature.archive.impl.photo.AllPhotoViewModel
import com.neki.android.feature.archive.impl.photo_detail.PhotoDetailIntent
import com.neki.android.feature.archive.impl.photo_detail.PhotoDetailRoute
import com.neki.android.feature.archive.impl.photo_detail.PhotoDetailViewModel
import com.neki.android.feature.photo_upload.api.navigateToQRScan
import com.neki.android.feature.select_album.api.SelectAlbumAction
import com.neki.android.feature.select_album.api.navigateToSelectAlbum
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
    fun provideArchiveEntryBuilder(navigator: MainNavigator): EntryProviderInstaller = {
        archiveEntry(navigator)
    }
}

private fun EntryProviderScope<NavKey>.archiveEntry(navigator: MainNavigator) {
    entry<ArchiveNavKey.Archive> {
        val resultBus = LocalResultEventBus.current
        val viewModel = hiltViewModel<ArchiveMainViewModel>()

        ResultEffect<PhotoDetailResult>(resultBus) {
            viewModel.store.onIntent(ArchiveMainIntent.RefreshArchiveMain)
        }
        ResultEffect<AlbumDetailResult>(resultBus) {
            viewModel.store.onIntent(ArchiveMainIntent.RefreshArchiveMain)
        }
        ResultEffect<AllPhotoResult>(resultBus) {
            viewModel.store.onIntent(ArchiveMainIntent.RefreshArchiveMain)
        }
        ResultEffect<AllAlbumResult>(resultBus) {
            viewModel.store.onIntent(ArchiveMainIntent.RefreshArchiveMain)
        }
        ResultEffect<PhotoUploadedResult>(resultBus) {
            viewModel.store.onIntent(ArchiveMainIntent.RefreshArchiveMain)
        }

        ArchiveMainRoute(
            viewModel = viewModel,
            navigateToQRScan = navigator::navigateToQRScan,
            navigateToAllAlbum = navigator::navigateToAllAlbum,
            navigateToFavoriteAlbum = { id ->
                navigator.navigateToAlbumDetail(id = id, isFavorite = true)
            },
            navigateToAlbumDetail = { id, title ->
                navigator.navigateToAlbumDetail(id = id, title = title, isFavorite = false)
            },
            navigateToAllPhoto = navigator::navigateToAllPhoto,
            navigateToPhotoDetail = navigator::navigateToPhotoDetail,
        )
    }

    entry<ArchiveNavKey.AllPhoto> {
        val resultBus = LocalResultEventBus.current
        val viewModel = hiltViewModel<AllPhotoViewModel>()

        ResultEffect<PhotoDetailResult>(resultBus) {
            viewModel.store.onIntent(AllPhotoIntent.RefreshPhotos)
            resultBus.sendResult(result = AllPhotoResult, allowDuplicate = false)
        }
        ResultEffect<PhotoCopiedResult>(resultBus) {
            viewModel.store.onIntent(AllPhotoIntent.ClickTopBarCancelIcon)
            resultBus.sendResult(result = AllPhotoResult, allowDuplicate = false)
        }

        AllPhotoRoute(
            viewModel = viewModel,
            navigateBack = navigator::goBack,
            navigateToPhotoDetail = navigator::navigateToPhotoDetail,
            navigateToSelectAlbum = { photoIds ->
                navigator.navigateToSelectAlbum(
                    action = SelectAlbumAction.CopyPhotos(photoIds = photoIds, fromPhotoDetail = false),
                    title = "앨범에 추가",
                    multiSelect = true,
                )
            },
        )
    }

    entry<ArchiveNavKey.AllAlbum> {
        val resultBus = LocalResultEventBus.current
        val viewModel = hiltViewModel<AllAlbumViewModel>()

        ResultEffect<AlbumDetailResult>(resultBus) {
            viewModel.store.onIntent(AllAlbumIntent.RefreshAlbums)
            resultBus.sendResult(result = AllAlbumResult, allowDuplicate = false)
        }

        AllAlbumRoute(
            viewModel = viewModel,
            navigateBack = navigator::goBack,
            navigateToFavoriteAlbum = { id ->
                navigator.navigateToAlbumDetail(id = id, isFavorite = true)
            },
            navigateToAlbumDetail = { id, title ->
                navigator.navigateToAlbumDetail(id = id, title = title, isFavorite = false)
            },
        )
    }

    entry<ArchiveNavKey.AlbumDetail> { key ->
        val resultBus = LocalResultEventBus.current
        val viewModel = hiltViewModel<AlbumDetailViewModel, AlbumDetailViewModel.Factory>(
            creationCallback = { factory -> factory.create(key.albumId, key.title, key.isFavorite) },
        )

        ResultEffect<PhotoDetailResult>(resultBus) {
            viewModel.store.onIntent(AlbumDetailIntent.RefreshPhotos)
            resultBus.sendResult(result = AlbumDetailResult, allowDuplicate = false)
        }
        ResultEffect<PhotoMovedResult>(resultBus) {
            viewModel.store.onIntent(AlbumDetailIntent.ClickCancelButton)
            viewModel.store.onIntent(AlbumDetailIntent.RefreshPhotos)
            resultBus.sendResult(result = AlbumDetailResult, allowDuplicate = false)
        }
        ResultEffect<PhotoCopiedResult>(resultBus) {
            viewModel.store.onIntent(AlbumDetailIntent.ClickCancelButton)
            viewModel.store.onIntent(AlbumDetailIntent.RefreshPhotos)
            resultBus.sendResult(result = AlbumDetailResult, allowDuplicate = false)
        }

        AlbumDetailRoute(
            viewModel = viewModel,
            navigateBack = navigator::goBack,
            navigateToPhotoDetail = navigator::navigateToPhotoDetail,
            navigateToSelectAlbum = { action ->
                navigator.navigateToSelectAlbum(
                    action = action,
                    title = "앨범에 추가",
                    multiSelect = true,
                )
            },
        )
    }

    entry<ArchiveNavKey.PhotoDetail> { key ->
        val resultBus = LocalResultEventBus.current
        val viewModel = hiltViewModel<PhotoDetailViewModel, PhotoDetailViewModel.Factory>(
            creationCallback = { factory -> factory.create(key) },
        )

        ResultEffect<PhotoCopiedResult>(resultBus) { result ->
            viewModel.store.onIntent(PhotoDetailIntent.PhotoCopied(result.albumIds.first(), result.albumTitle))
            resultBus.sendResult(result = PhotoDetailResult, allowDuplicate = false)
        }

        PhotoDetailRoute(
            viewModel = viewModel,
            navigateBack = navigator::goBack,
            navigateToSelectAlbum = { photoId ->
                navigator.navigateToSelectAlbum(
                    action = SelectAlbumAction.CopyPhotos(photoIds = listOf(photoId), fromPhotoDetail = true),
                    title = "모든 앨범",
                    multiSelect = false,
                )
            },
            navigateToAlbumDetail = { id, title ->
                navigator.remove(key)
                if (key.folderId != null) {
                    val sourceAlbumKey = navigator.state.currentSubStack
                        .filterIsInstance<ArchiveNavKey.AlbumDetail>()
                        .firstOrNull { !it.isFavorite && it.albumId == key.folderId }
                    if (sourceAlbumKey != null) navigator.remove(sourceAlbumKey)
                }
                navigator.navigateToAlbumDetail(id = id, title = title, isFavorite = false)
            },
        )
    }
}

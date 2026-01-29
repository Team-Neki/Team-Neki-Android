package com.neki.android.feature.archive.impl.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.FolderRepository
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.domain.usecase.UploadMultiplePhotoUseCase
import com.neki.android.core.domain.usecase.UploadSinglePhotoUseCase
import com.neki.android.core.model.UploadType
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val DEFAULT_PHOTOS_SIZE = 20

@HiltViewModel
class ArchiveMainViewModel @Inject constructor(
    private val uploadSinglePhotoUseCase: UploadSinglePhotoUseCase,
    private val uploadMultiplePhotoUseCase: UploadMultiplePhotoUseCase,
    private val photoRepository: PhotoRepository,
    private val folderRepository: FolderRepository,
) : ViewModel() {

    val store: MviIntentStore<ArchiveMainState, ArchiveMainIntent, ArchiveMainSideEffect> =
        mviIntentStore(
            initialState = ArchiveMainState(),
            onIntent = ::onIntent,
            initialFetchData = { store.onIntent(ArchiveMainIntent.EnterArchiveMainScreen) },
        )

    private var fetchJob: Job? = null
    private fun onIntent(
        intent: ArchiveMainIntent,
        state: ArchiveMainState,
        reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit,
        postSideEffect: (ArchiveMainSideEffect) -> Unit,
    ) {
        if (intent != ArchiveMainIntent.EnterArchiveMainScreen) reduce { copy(isFirstEntered = false) }
        when (intent) {
            is ArchiveMainIntent.QRCodeScanned -> reduce {
                copy(
                    scannedImageUrl = intent.imageUrl,
                    isShowChooseWithAlbumDialog = true,
                )
            }

            ArchiveMainIntent.EnterArchiveMainScreen -> fetchInitialData(reduce)
            ArchiveMainIntent.RefreshArchiveMainScreen -> fetchInitialData(reduce)
            ArchiveMainIntent.ClickScreen -> reduce { copy(isFirstEntered = false) }
            ArchiveMainIntent.ClickGoToTopButton -> postSideEffect(ArchiveMainSideEffect.ScrollToTop)

            // TopBar Intent
            ArchiveMainIntent.ClickAddIcon -> reduce { copy(isShowAddDialog = true) }
            ArchiveMainIntent.DismissAddDialog -> reduce { copy(isShowAddDialog = false) }
            ArchiveMainIntent.ClickQRScanRow -> {
                reduce { copy(isShowAddDialog = false) }
                postSideEffect(ArchiveMainSideEffect.NavigateToQRScan)
            }

            ArchiveMainIntent.ClickGalleryUploadRow -> {
                reduce { copy(isShowAddDialog = false) }
                postSideEffect(ArchiveMainSideEffect.OpenGallery)
            }

            is ArchiveMainIntent.SelectGalleryImage -> reduce {
                copy(
                    isShowChooseWithAlbumDialog = true,
                    selectedUris = intent.uris.toImmutableList(),
                )
            }

            ArchiveMainIntent.DismissChooseWithAlbumDialog -> reduce { copy(isShowChooseWithAlbumDialog = false) }
            ArchiveMainIntent.ClickUploadWithAlbumRow -> {
                reduce {
                    copy(
                        isShowChooseWithAlbumDialog = false,
                        scannedImageUrl = null,
                        selectedUris = persistentListOf(),
                    )
                }
                if (state.scannedImageUrl == null)
                    postSideEffect(ArchiveMainSideEffect.NavigateToUploadAlbumWithGallery(state.selectedUris.map { it.toString() }))
                else postSideEffect(ArchiveMainSideEffect.NavigateToUploadAlbumWithQRScan(state.scannedImageUrl))
            }

            ArchiveMainIntent.ClickUploadWithoutAlbumRow -> uploadWithoutAlbum(state, reduce, postSideEffect)

            ArchiveMainIntent.ClickAddNewAlbumRow -> reduce {
                copy(
                    isShowAddDialog = false,
                    isShowAddAlbumBottomSheet = true,
                )
            }

            ArchiveMainIntent.ClickNotificationIcon -> {}

            // Album Intent
            ArchiveMainIntent.ClickAllAlbumText -> postSideEffect(ArchiveMainSideEffect.NavigateToAllAlbum)
            ArchiveMainIntent.ClickFavoriteAlbum -> postSideEffect(ArchiveMainSideEffect.NavigateToFavoriteAlbum(-1L))
            is ArchiveMainIntent.ClickAlbumItem -> postSideEffect(ArchiveMainSideEffect.NavigateToAlbumDetail(intent.albumId))

            // Photo Intent
            ArchiveMainIntent.ClickAllPhotoText -> postSideEffect(ArchiveMainSideEffect.NavigateToAllPhoto)
            is ArchiveMainIntent.ClickPhotoItem -> postSideEffect(ArchiveMainSideEffect.NavigateToPhotoDetail(intent.photo))

            // Add Album BottomSheet Intent
            ArchiveMainIntent.DismissAddAlbumBottomSheet -> reduce { copy(isShowAddAlbumBottomSheet = false) }
            is ArchiveMainIntent.ClickAddAlbumButton -> handleAddAlbum(intent.albumName, reduce, postSideEffect)
        }
    }

    private fun fetchInitialData(reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit) {
        if (fetchJob?.isActive == true) return

        fetchJob = viewModelScope.launch {
            reduce { copy(isLoading = true) }
            try {
                awaitAll(
                    async { fetchFavoriteSummary(reduce) },
                    async { fetchPhotos(reduce) },
                    async { fetchFolders(reduce) },
                )
            } finally {
                reduce { copy(isLoading = false) }
            }
        }
    }

    private suspend fun fetchFavoriteSummary(reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit) {
        photoRepository.getFavoriteSummary()
            .onSuccess { data ->
                reduce { copy(favoriteAlbum = data) }
            }
            .onFailure { error ->
                Timber.e(error)
            }
    }

    private suspend fun fetchPhotos(reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit, size: Int = DEFAULT_PHOTOS_SIZE) {
        photoRepository.getPhotos()
            .onSuccess { data ->
                reduce { copy(recentPhotos = data.toImmutableList()) }
            }
            .onFailure { error ->
                Timber.e(error)
            }
    }

    private suspend fun fetchFolders(reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit) {
        folderRepository.getFolders()
            .onSuccess { data ->
                reduce { copy(albums = data.toImmutableList()) }
            }
            .onFailure { error ->
                Timber.e(error)
            }
    }

    private fun uploadWithoutAlbum(
        state: ArchiveMainState,
        reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit,
        postSideEffect: (ArchiveMainSideEffect) -> Unit,
    ) {
        reduce { copy(isShowChooseWithAlbumDialog = false) }
        val onSuccessSideEffect = {
            reduce { copy(isLoading = false) }
            postSideEffect(ArchiveMainSideEffect.ShowToastMessage("이미지를 추가했어요"))
        }
        if (state.uploadType == UploadType.SINGLE) {
            uploadSingleImage(
                imageUrl = state.scannedImageUrl ?: return,
                reduce = reduce,
                postSideEffect = postSideEffect,
                onSuccess = onSuccessSideEffect,
            )
        } else {
            uploadMultipleImages(
                imageUris = state.selectedUris,
                reduce = reduce,
                postSideEffect = postSideEffect,
                onSuccess = onSuccessSideEffect,
            )
        }
    }

    private fun uploadSingleImage(
        imageUrl: String,
        reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit,
        postSideEffect: (ArchiveMainSideEffect) -> Unit,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }

            uploadSinglePhotoUseCase(
                imageUrl = imageUrl,
            ).onSuccess {
                fetchPhotos(reduce, 1) // 가장 최신 데이터 가져오기
                onSuccess()
            }.onFailure { error ->
                Timber.e(error)
                postSideEffect(ArchiveMainSideEffect.ShowToastMessage("이미지 업로드에 실패했어요"))
                reduce { copy(isLoading = false) }
            }
        }
    }

    private fun uploadMultipleImages(
        imageUris: List<Uri>,
        reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit,
        postSideEffect: (ArchiveMainSideEffect) -> Unit,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }

            uploadMultiplePhotoUseCase(
                imageUris = imageUris,
            ).onSuccess {
                fetchPhotos(reduce)
                onSuccess()
            }.onFailure { error ->
                Timber.e(error)
                postSideEffect(ArchiveMainSideEffect.ShowToastMessage("이미지 업로드에 실패했어요"))
                reduce { copy(isLoading = false) }
            }
        }
    }

    private fun handleAddAlbum(
        albumName: String,
        reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit,
        postSideEffect: (ArchiveMainSideEffect) -> Unit,
    ) {
        // TODO: Add album to repository
        reduce { copy(isShowAddAlbumBottomSheet = false) }
        postSideEffect(ArchiveMainSideEffect.ShowToastMessage("새로운 앨범을 추가했어요"))
    }
}

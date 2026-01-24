package com.neki.android.feature.archive.impl.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.common.util.urlToByteArray
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.domain.usecase.UploadSinglePhotoUseCase
import com.neki.android.core.model.Album
import com.neki.android.core.model.Photo
import com.neki.android.core.model.UploadType
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val DEFAULT_PHOTOS_SIZE = 20

@HiltViewModel
class ArchiveMainViewModel @Inject constructor(
    private val uploadSinglePhotoUseCase: UploadSinglePhotoUseCase,
    private val photoRepository: PhotoRepository,
) : ViewModel() {

    val store: MviIntentStore<ArchiveMainState, ArchiveMainIntent, ArchiveMainSideEffect> =
        mviIntentStore(
            initialState = ArchiveMainState(),
            onIntent = ::onIntent,
            initialFetchData = { store.onIntent(ArchiveMainIntent.EnterArchiveMainScreen) },
        )

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
        val dummyPhotos = listOf(
            Photo(id = 1, imageUrl = "https://picsum.photos/seed/pose1/400/500", isFavorite = true, date = "2025.01.15"),
            Photo(id = 2, imageUrl = "https://picsum.photos/seed/pose2/400/650", isFavorite = false, date = "2025.01.15"),
            Photo(id = 3, imageUrl = "https://picsum.photos/seed/pose3/400/480", isFavorite = true, date = "2025.01.14"),
            Photo(id = 4, imageUrl = "https://picsum.photos/seed/pose4/400/720", isFavorite = false, date = "2025.01.14"),
            Photo(id = 5, imageUrl = "https://picsum.photos/seed/pose5/400/550", isFavorite = false, date = "2025.01.13"),
            Photo(id = 6, imageUrl = "https://picsum.photos/seed/pose6/400/400", isFavorite = true, date = "2025.01.13"),
            Photo(id = 7, imageUrl = "https://picsum.photos/seed/pose7/400/600", isFavorite = false, date = "2025.01.12"),
            Photo(id = 8, imageUrl = "https://picsum.photos/seed/pose8/400/520", isFavorite = false, date = "2025.01.12"),
            Photo(id = 9, imageUrl = "https://picsum.photos/seed/pose9/400/680", isFavorite = true, date = "2025.01.11"),
            Photo(id = 10, imageUrl = "https://picsum.photos/seed/pose10/400/450", isFavorite = false, date = "2025.01.11"),
            Photo(id = 11, imageUrl = "https://picsum.photos/seed/pose11/400/580", isFavorite = false, date = "2025.01.10"),
            Photo(id = 12, imageUrl = "https://picsum.photos/seed/pose12/400/700", isFavorite = true, date = "2025.01.10"),
            Photo(id = 13, imageUrl = "https://picsum.photos/seed/pose13/400/460", isFavorite = false, date = "2025.01.09"),
            Photo(id = 14, imageUrl = "https://picsum.photos/seed/pose14/400/620", isFavorite = false, date = "2025.01.09"),
            Photo(id = 15, imageUrl = "https://picsum.photos/seed/pose15/400/540", isFavorite = true, date = "2025.01.08"),
        ).toImmutableList()

        val travelPhotos = persistentListOf(
            Photo(id = 101, imageUrl = "https://picsum.photos/seed/travel1/400/500", date = "2025.01.10"),
            Photo(id = 102, imageUrl = "https://picsum.photos/seed/travel2/400/600", date = "2025.01.10"),
            Photo(id = 103, imageUrl = "https://picsum.photos/seed/travel3/400/480", date = "2025.01.09"),
            Photo(id = 104, imageUrl = "https://picsum.photos/seed/travel4/400/550", date = "2025.01.09"),
        )

        val familyPhotos = persistentListOf(
            Photo(id = 201, imageUrl = "https://picsum.photos/seed/family1/400/520", date = "2025.01.05"),
            Photo(id = 202, imageUrl = "https://picsum.photos/seed/family2/400/680", date = "2025.01.05"),
            Photo(id = 203, imageUrl = "https://picsum.photos/seed/family3/400/450", date = "2025.01.04"),
        )

        val friendPhotos = persistentListOf(
            Photo(id = 301, imageUrl = "https://picsum.photos/seed/friend1/400/580", date = "2024.12.25"),
            Photo(id = 302, imageUrl = "https://picsum.photos/seed/friend2/400/620", date = "2024.12.25"),
            Photo(id = 303, imageUrl = "https://picsum.photos/seed/friend3/400/500", date = "2024.12.24"),
            Photo(id = 304, imageUrl = "https://picsum.photos/seed/friend4/400/700", date = "2024.12.24"),
            Photo(id = 305, imageUrl = "https://picsum.photos/seed/friend5/400/460", date = "2024.12.23"),
        )

        val dummyAlbums = persistentListOf(
            Album(id = 1, title = "제주도 여행 2025", thumbnailUrl = "https://picsum.photos/seed/travel1/400/500", photoList = travelPhotos),
            Album(id = 2, title = "가족 생일파티", thumbnailUrl = "https://picsum.photos/seed/family1/400/520", photoList = familyPhotos),
            Album(id = 3, title = "대학 동기 모임", thumbnailUrl = "https://picsum.photos/seed/friend1/400/580", photoList = friendPhotos),
        )

        val favoritePhotos = dummyPhotos.filter { it.isFavorite }.toImmutableList()

        val favoriteAlbum = Album(
            id = 0,
            title = "즐겨찾는 사진",
            thumbnailUrl = favoritePhotos.firstOrNull()?.imageUrl,
            photoList = favoritePhotos,
        )

        fetchPhotos(reduce)

        reduce {
            copy(
                favoriteAlbum = favoriteAlbum,
                albums = dummyAlbums,
//                recentPhotos = dummyPhotos,
            )
        }
    }

    private fun uploadWithoutAlbum(
        state: ArchiveMainState,
        reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit,
        postSideEffect: (ArchiveMainSideEffect) -> Unit,
    ) {
        reduce { copy(isShowChooseWithAlbumDialog = false) }
        val onSuccessSideEffect = {
            postSideEffect(ArchiveMainSideEffect.ShowToastMessage("이미지를 추가했어요"))
        }
        if (state.uploadType == UploadType.QR_SCAN) {
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
            val imageBytes = imageUrl.urlToByteArray()

            uploadSinglePhotoUseCase(
                imageBytes = imageBytes,
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
        // TODO: 이미지 여러개 업로드
        postSideEffect(ArchiveMainSideEffect.ShowToastMessage("이미지 업로드에 실패했어요"))
    }

    private fun fetchPhotos(reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit, size: Int = DEFAULT_PHOTOS_SIZE) {
        viewModelScope.launch {
            photoRepository.getPhotos()
                .onSuccess { data ->
                    reduce {
                        copy(
                            recentPhotos = data.toImmutableList(),
                            isLoading = false,
                        )
                    }
                }
                .onFailure { error ->
                    Timber.e(error)
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

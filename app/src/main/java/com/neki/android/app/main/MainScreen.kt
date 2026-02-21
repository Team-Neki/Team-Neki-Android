package com.neki.android.app.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.neki.android.app.main.component.AddPhotoBottomSheet
import com.neki.android.app.main.component.AlbumUploadOption
import com.neki.android.app.ui.BottomNavigationBar
import com.neki.android.app.main.component.SelectWithAlbumDialog
import com.neki.android.core.navigation.result.LocalResultEventBus
import com.neki.android.core.navigation.result.ResultEffect
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.map.api.MapNavKey
import com.neki.android.feature.photo_upload.api.QRScanResult
import timber.log.Timber

@Composable
fun MainRoute(
    currentKey: NavKey,
    currentTopLevelKey: NavKey,
    topLevelKeys: Set<NavKey>,
    entries: SnapshotStateList<NavEntry<NavKey>>,
    onTabSelected: (NavKey) -> Unit,
    onBack: () -> Unit,
    navigateToQRScan: () -> Unit,
    navigateToUploadAlbumWithGallery: (List<String>) -> Unit,
    navigateToUploadAlbumWithQRScan: (String) -> Unit,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val nekiToast = remember { NekiToast(context) }
    val resultBus = LocalResultEventBus.current

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(10),
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.store.onIntent(MainIntent.SelectGalleryImage(uris))
        } else {
            Timber.d("No media selected")
        }
    }

    ResultEffect<QRScanResult>(resultBus) { result ->
        when (result) {
            is QRScanResult.QRCodeScanned -> viewModel.store.onIntent(MainIntent.QRCodeScanned(result.imageUrl))
            QRScanResult.OpenGallery -> viewModel.store.onIntent(MainIntent.ClickGalleryUpload)
        }
    }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            MainSideEffect.NavigateToQRScan -> navigateToQRScan()
            MainSideEffect.OpenGallery -> photoPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
            is MainSideEffect.NavigateToUploadAlbumWithGallery -> navigateToUploadAlbumWithGallery(sideEffect.uriStrings)
            is MainSideEffect.NavigateToUploadAlbumWithQRScan -> navigateToUploadAlbumWithQRScan(sideEffect.imageUrl)
            is MainSideEffect.ShowToast -> nekiToast.showToast(sideEffect.message)
        }
    }

    MainScreen(
        uiState = uiState,
        currentKey = currentKey,
        currentTopLevelKey = currentTopLevelKey,
        topLevelKeys = topLevelKeys,
        entries = entries,
        onTabSelected = onTabSelected,
        onBack = onBack,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
fun MainScreen(
    uiState: MainState,
    currentKey: NavKey,
    currentTopLevelKey: NavKey,
    topLevelKeys: Set<NavKey>,
    entries: SnapshotStateList<NavEntry<NavKey>>,
    onTabSelected: (NavKey) -> Unit,
    onBack: () -> Unit,
    onIntent: (MainIntent) -> Unit,
) {
    val shouldShowBottomBar by remember(currentKey) {
        mutableStateOf(currentKey in topLevelKeys)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        bottomBar = {
            BottomNavigationBar(
                visible = shouldShowBottomBar,
                currentTab = currentTopLevelKey,
                currentKey = currentKey,
                onTabSelected = { onTabSelected(it.navKey) },
                onClickFab = { onIntent(MainIntent.ClickAddPhotoFab) },
            )
            if (shouldShowBottomBar) {
                BottomNavigationBar(
                    currentTab = currentTopLevelKey,
                    currentKey = currentKey,
                    onTabSelected = { onTabSelected(it.navKey) },
                    onClickFab = { onIntent(MainIntent.ClickAddPhotoFab) },
                )
            }
        },
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(
                if (currentKey == MapNavKey.Map) PaddingValues(bottom = innerPadding.calculateBottomPadding()) else innerPadding,
            ),
            entries = entries,
            onBack = onBack,
        )
    }

    if (uiState.isLoading) {
        LoadingDialog()
    }

    if (uiState.isShowAddPhotoBottomSheet) {
        AddPhotoBottomSheet(
            onDismissRequest = { onIntent(MainIntent.DismissAddPhotoBottomSheet) },
            onClickQRScan = { onIntent(MainIntent.ClickQRScan) },
            onClickGallery = { onIntent(MainIntent.ClickGalleryUpload) },
        )
    }

    if (uiState.isShowSelectWithAlbumDialog) {
        SelectWithAlbumDialog(
            onDismissRequest = { onIntent(MainIntent.DismissSelectWithAlbumDialog) },
            onSelect = { option ->
                when (option) {
                    AlbumUploadOption.WITHOUT_ALBUM -> onIntent(MainIntent.ClickUploadWithoutAlbum)
                    AlbumUploadOption.WITH_ALBUM -> onIntent(MainIntent.ClickUploadWithAlbum)
                }
            },
        )
    }
}

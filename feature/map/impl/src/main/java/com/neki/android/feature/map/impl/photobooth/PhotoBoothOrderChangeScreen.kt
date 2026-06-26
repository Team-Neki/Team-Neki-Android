package com.neki.android.feature.map.impl.photobooth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.topbar.BackTitleTextButtonTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Brand
import com.neki.android.core.navigation.result.LocalResultEventBus
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.map.api.MapResult
import com.neki.android.feature.map.impl.photobooth.component.PhotoBoothOrderChangeItem
import kotlinx.collections.immutable.persistentListOf
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
internal fun PhotoBoothOrderChangeRoute(
    viewModel: PhotoBoothOrderChangeViewModel,
    navigateBack: () -> Unit = {},
) {
    val state by viewModel.store.uiState.collectAsStateWithLifecycle()
    val resultBus = LocalResultEventBus.current

    viewModel.store.sideEffects.collectWithLifecycle { effect ->
        when (effect) {
            is PhotoBoothOrderChangeSideEffect.SendBrandsOrderChangeResult -> {
                resultBus.sendResult<MapResult>(
                    result = MapResult.BrandOrderChanged(orderedBrands = effect.orderedBrands),
                    allowDuplicate = false,
                )
            }
            PhotoBoothOrderChangeSideEffect.NavigateBack -> navigateBack()
        }
    }

    PhotoBoothOrderChangeScreen(
        state = state,
        onIntent = viewModel.store::onIntent,
        onClickBack = navigateBack,
    )
}

@Composable
internal fun PhotoBoothOrderChangeScreen(
    state: PhotoBoothOrderChangeState = PhotoBoothOrderChangeState(),
    onIntent: (PhotoBoothOrderChangeIntent) -> Unit = {},
    onClickBack: () -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onIntent(PhotoBoothOrderChangeIntent.ReorderBrand(from.index, to.index))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        BackTitleTextButtonTopBar(
            title = "브랜드 순서 변경",
            buttonLabel = "완료",
            enabled = state.isOrderChanged,
            onBack = onClickBack,
            onClickTextButton = { onIntent(PhotoBoothOrderChangeIntent.ClickComplete) },
        )
        LazyColumn(state = lazyListState) {
            itemsIndexed(
                items = state.brands,
                key = { _, brand -> brand.id },
            ) { _, brand ->
                ReorderableItem(state = reorderableState, key = brand.id) { isDragging ->
                    PhotoBoothOrderChangeItem(
                        brand = brand,
                        isDragging = isDragging,
                    )
                }
            }
        }
    }

    if (state.isLoading) {
        LoadingDialog(
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
        )
    }
}

@ComponentPreview
@Composable
private fun PhotoBoothOrderChangeScreenPreview() {
    NekiTheme {
        PhotoBoothOrderChangeScreen(
            state = PhotoBoothOrderChangeState(
                brands = persistentListOf(
                    Brand(id = 1L, name = "인생네컷"),
                    Brand(id = 2L, name = "하루필름"),
                    Brand(id = 3L, name = "포토이즘"),
                    Brand(id = 4L, name = "셀픽스"),
                ),
                isOrderChanged = false,
            ),
        )
    }
}

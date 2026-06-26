package com.neki.android.feature.map.impl.photobooth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.MapRepository
import com.neki.android.core.model.Brand
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = PhotoBoothOrderChangeViewModel.Factory::class)
internal class PhotoBoothOrderChangeViewModel @AssistedInject constructor(
    @Assisted private val originalBrandsOrder: ImmutableList<Brand>,
    private val mapRepository: MapRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(originalBrandsOrder: ImmutableList<Brand>): PhotoBoothOrderChangeViewModel
    }

    val store: MviIntentStore<PhotoBoothOrderChangeState, PhotoBoothOrderChangeIntent, PhotoBoothOrderChangeSideEffect> =
        mviIntentStore(
            initialState = PhotoBoothOrderChangeState(
                brands = originalBrandsOrder,
            ),
            onIntent = ::onIntent,
        )

    private fun onIntent(
        intent: PhotoBoothOrderChangeIntent,
        state: PhotoBoothOrderChangeState,
        reduce: (PhotoBoothOrderChangeState.() -> PhotoBoothOrderChangeState) -> Unit,
        postSideEffect: (PhotoBoothOrderChangeSideEffect) -> Unit,
    ) {
        when (intent) {
            is PhotoBoothOrderChangeIntent.ReorderBrand -> handleReorder(intent.from, intent.to, state, reduce)
            PhotoBoothOrderChangeIntent.ClickComplete -> handleClickComplete(state, reduce, postSideEffect)
        }
    }

    private fun handleClickComplete(
        state: PhotoBoothOrderChangeState,
        reduce: (PhotoBoothOrderChangeState.() -> PhotoBoothOrderChangeState) -> Unit,
        postSideEffect: (PhotoBoothOrderChangeSideEffect) -> Unit,
    ) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }
            mapRepository.saveBrandOrder(state.brands.map { it.id })
                .onSuccess {
                    reduce { copy(isLoading = false) }
                    postSideEffect(PhotoBoothOrderChangeSideEffect.SendBrandsOrderChangeResult(state.brands))
                    postSideEffect(PhotoBoothOrderChangeSideEffect.NavigateBack)
                }
                .onFailure { e ->
                    Timber.e(e)
                    reduce { copy(isLoading = false) }
                }
        }
    }

    private fun handleReorder(
        from: Int,
        to: Int,
        state: PhotoBoothOrderChangeState,
        reduce: (PhotoBoothOrderChangeState.() -> PhotoBoothOrderChangeState) -> Unit,
    ) {
        val mutable = state.brands.toMutableList()
        mutable.add(to, mutable.removeAt(from))
        val reordered = mutable.toImmutableList()
        reduce {
            copy(
                brands = reordered,
                isOrderChanged = reordered.indices.any { reordered[it].id != originalBrandsOrder[it].id },
            )
        }
    }
}

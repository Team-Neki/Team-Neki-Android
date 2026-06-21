package com.neki.android.feature.map.impl.photobooth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.MapRepository
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PhotoBoothOrderChangeViewModel @Inject constructor(
    private val mapRepository: MapRepository,
) : ViewModel() {

    val store: MviIntentStore<PhotoBoothOrderChangeState, PhotoBoothOrderChangeIntent, PhotoBoothOrderChangeSideEffect> =
        mviIntentStore(
            initialState = PhotoBoothOrderChangeState(),
            onIntent = ::onIntent,
            initialFetchData = { store.onIntent(PhotoBoothOrderChangeIntent.LoadBrands) },
        )

    private fun onIntent(
        intent: PhotoBoothOrderChangeIntent,
        state: PhotoBoothOrderChangeState,
        reduce: (PhotoBoothOrderChangeState.() -> PhotoBoothOrderChangeState) -> Unit,
        postSideEffect: (PhotoBoothOrderChangeSideEffect) -> Unit,
    ) {
        when (intent) {
            PhotoBoothOrderChangeIntent.LoadBrands -> loadBrands(reduce)
            is PhotoBoothOrderChangeIntent.ReorderBrand -> handleReorder(intent.from, intent.to, state, reduce)
            PhotoBoothOrderChangeIntent.ClickComplete -> postSideEffect(PhotoBoothOrderChangeSideEffect.NavigateBack)
        }
    }

    private fun loadBrands(
        reduce: (PhotoBoothOrderChangeState.() -> PhotoBoothOrderChangeState) -> Unit,
    ) {
        viewModelScope.launch {
            mapRepository.getBrands().onSuccess { brands ->
                val immutable = brands.toImmutableList()
                reduce { copy(brands = immutable, initialBrands = immutable) }
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
                isOrderChanged = reordered.map { it.id } != initialBrands.map { it.id },
            )
        }
    }
}

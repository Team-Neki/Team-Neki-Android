package com.neki.android.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.enableSavedStateHandles
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.compose.LocalSavedStateRegistryOwner

/**
 * Hilt ViewModel 공유를 지원하는 NavEntryDecorator.
 *
 * 사용법:
 * 1. NavDisplay에 decorator 등록
 * 2. 부모 entry에 clazzContentKey 설정
 * 3. 자식 entry에 parent() 메타데이터 설정
 *
 * @see HiltSharedViewModelStoreNavEntryDecorator.parent
 */
@Composable
fun <T : Any> rememberHiltSharedViewModelStoreNavEntryDecorator(
    viewModelStoreOwner: ViewModelStoreOwner =
        checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
    removeViewModelStoreOnPop: () -> Boolean = { true },
): HiltSharedViewModelStoreNavEntryDecorator<T> {
    val currentRemoveViewModelStoreOnPop = rememberUpdatedState(removeViewModelStoreOnPop)
    return remember(viewModelStoreOwner, currentRemoveViewModelStoreOnPop) {
        HiltSharedViewModelStoreNavEntryDecorator(
            viewModelStore = viewModelStoreOwner.viewModelStore,
            removeViewModelStoreOnPop = removeViewModelStoreOnPop,
        )
    }
}

/**
 * 부모-자식 NavEntry 간 ViewModel 공유를 위한 Decorator.
 * hiltViewModel()과 함께 사용 가능.
 */
class HiltSharedViewModelStoreNavEntryDecorator<T : Any>(
    viewModelStore: ViewModelStore,
    removeViewModelStoreOnPop: () -> Boolean,
) : NavEntryDecorator<T>(
    onPop = { key ->
        if (removeViewModelStoreOnPop()) {
            viewModelStore.getHiltEntryViewModel().clearViewModelStoreOwnerForKey(key)
        }
    },
    decorate = { entry ->
        // parent() 메타데이터가 있으면 부모의 contentKey 사용
        val contentKey = entry.metadata[PARENT_CONTENT_KEY] ?: entry.contentKey
        val entryViewModelStore =
            viewModelStore.getHiltEntryViewModel().viewModelStoreForKey(contentKey)

        val savedStateRegistryOwner = LocalSavedStateRegistryOwner.current

        val childViewModelStoreOwner = remember(entryViewModelStore, savedStateRegistryOwner) {
            object :
                ViewModelStoreOwner,
                SavedStateRegistryOwner by savedStateRegistryOwner {

                override val viewModelStore: ViewModelStore
                    get() = entryViewModelStore

                init {
                    require(this.lifecycle.currentState == Lifecycle.State.INITIALIZED) {
                        "SavedStateNavEntryDecorator must be added before this decorator."
                    }
                    enableSavedStateHandles()
                }
            }
        }

        CompositionLocalProvider(LocalViewModelStoreOwner provides childViewModelStoreOwner) {
            entry.Content()
        }
    },
) {
    companion object {
        private const val PARENT_CONTENT_KEY = "hilt_shared_decorator_parent_content_key"

        /**
         * 자식 entry가 부모의 ViewModelStore를 공유하도록 설정.
         * @param contentKey 부모 entry의 clazzContentKey 값과 동일해야 함
         */
        fun parent(contentKey: Any) = mapOf(PARENT_CONTENT_KEY to contentKey)
    }
}

/** NavEntry별 ViewModelStore 관리용 ViewModel */
private class HiltEntryViewModel : ViewModel() {
    private val owners = mutableMapOf<Any, ViewModelStore>()

    fun viewModelStoreForKey(key: Any): ViewModelStore = owners.getOrPut(key) { ViewModelStore() }

    fun clearViewModelStoreOwnerForKey(key: Any) {
        owners.remove(key)?.clear()
    }

    override fun onCleared() {
        owners.forEach { (_, store) -> store.clear() }
    }
}

private fun ViewModelStore.getHiltEntryViewModel(): HiltEntryViewModel {
    val provider = ViewModelProvider.create(
        store = this,
        factory = viewModelFactory { initializer { HiltEntryViewModel() } },
    )
    return provider[HiltEntryViewModel::class]
}

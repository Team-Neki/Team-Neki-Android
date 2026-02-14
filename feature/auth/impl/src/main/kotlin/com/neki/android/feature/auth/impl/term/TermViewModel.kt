package com.neki.android.feature.auth.impl.term

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.AuthRepository
import com.neki.android.core.dataapi.repository.TermRepository
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TermViewModel @Inject constructor(
    private val termRepository: TermRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    val store: MviIntentStore<TermState, TermIntent, TermSideEffect> =
        mviIntentStore(
            initialState = TermState(),
            onIntent = ::onIntent,
            initialFetchData = { store.onIntent(TermIntent.EnterTermScreen) },
        )

    private fun onIntent(
        intent: TermIntent,
        state: TermState,
        reduce: (TermState.() -> TermState) -> Unit,
        postSideEffect: (TermSideEffect) -> Unit,
    ) {
        when (intent) {
            TermIntent.EnterTermScreen -> fetchTerms(reduce)

            TermIntent.ClickAgreeAll -> {
                val shouldCheckAll = !state.isAllRequiredTermChecked
                val updatedTerms = state.terms.map { term ->
                    if (term.isRequired) term.copy(isChecked = shouldCheckAll) else term
                }.toImmutableList()
                reduce { copy(terms = updatedTerms) }
            }

            is TermIntent.ClickAgreeTerm -> {
                val updatedTerms = state.terms.map { term ->
                    if (term.id == intent.term.id) term.copy(isChecked = !term.isChecked) else term
                }.toImmutableList()
                reduce { copy(terms = updatedTerms) }
            }

            is TermIntent.ClickTermNavigateUrl -> {
                postSideEffect(TermSideEffect.NavigateUrl(intent.term.url))
            }

            TermIntent.ClickNext -> {
                if (state.isAllRequiredTermChecked) {
                    agreeTerms(state, reduce, postSideEffect)
                }
            }

            TermIntent.ClickBack -> {
                postSideEffect(TermSideEffect.NavigateBack)
            }
        }
    }

    private fun fetchTerms(reduce: (TermState.() -> TermState) -> Unit) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }
            termRepository.getTerms()
                .onSuccess { terms ->
                    reduce { copy(isLoading = false, terms = terms.toImmutableList()) }
                }
                .onFailure { error ->
                    Timber.e(error)
                    reduce { copy(isLoading = false) }
                }
        }
    }

    private fun agreeTerms(
        state: TermState,
        reduce: (TermState.() -> TermState) -> Unit,
        postSideEffect: (TermSideEffect) -> Unit,
    ) = viewModelScope.launch {
        reduce { copy(isLoading = true) }
        val checkedTermIds = state.terms.filter { it.isChecked }.map { it.id }
        termRepository.agreeTerms(checkedTermIds)
            .onSuccess {
                authRepository.setCompletedOnboarding(true)
                postSideEffect(TermSideEffect.NavigateToMain)
            }
            .onFailure { exception ->
                Timber.e(exception)
                postSideEffect(TermSideEffect.ShowToastMessage("약관 동의에 실패했습니다. 다시 시도해주세요."))
            }
        reduce { copy(isLoading = false) }
    }
}

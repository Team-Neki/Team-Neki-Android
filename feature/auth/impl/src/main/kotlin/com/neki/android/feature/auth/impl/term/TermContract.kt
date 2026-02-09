package com.neki.android.feature.auth.impl.term

import com.neki.android.core.model.Term
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class TermState(
    val isLoading: Boolean = false,
    val terms: ImmutableList<Term> = persistentListOf(),
) {
    val isAllRequiredChecked: Boolean
        get() = terms.filter { it.isRequired }.all { it.isChecked }
}

sealed interface TermIntent {
    data object EnterTermScreen : TermIntent
    data object ClickAgreeAll : TermIntent
    data class ClickAgreeTerm(val term: Term) : TermIntent
    data class ClickTermNavigateUrl(val term: Term) : TermIntent
    data object ClickNext : TermIntent
    data object ClickBack : TermIntent
}

sealed interface TermSideEffect {
    data object NavigateToMain : TermSideEffect
    data object NavigateBack : TermSideEffect
    data class NavigateUrl(val url: String) : TermSideEffect
    data class ShowToastMessage(val message: String) : TermSideEffect
}

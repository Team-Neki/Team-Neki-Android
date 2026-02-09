package com.neki.android.core.data.remote.model.response

import com.neki.android.core.model.Term
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TermsResponse(
    @SerialName("terms") val terms: List<TermResponse> = emptyList(),
) {
    @Serializable
    data class TermResponse(
        @SerialName("id") val id: Long = 0L,
        @SerialName("termType") val termType: String = "",
        @SerialName("title") val title: String = "",
        @SerialName("url") val url: String = "",
        @SerialName("isRequired") val isRequired: Boolean = false,
    ) {
        internal fun toModel(): Term = Term(
            id = id,
            title = title,
            url = url,
            isRequired = isRequired,
        )
    }

    fun toModels(): List<Term> = terms.map { it.toModel() }
}

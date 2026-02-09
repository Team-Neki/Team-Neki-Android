package com.neki.android.core.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TermAgreementsRequest(
    @SerialName("agreements") val agreements: List<Agreement>,
) {
    @Serializable
    data class Agreement(
        @SerialName("termId") val termId: Long,
        @SerialName("agreed") val agreed: Boolean,
    )
}

package com.neki.android.core.data.local.model

import kotlinx.serialization.Serializable

@Serializable
internal data class MarketingPopupRecord(
    val shownCount: Int = 0,
    val lastShownAt: Long = 0L,
)

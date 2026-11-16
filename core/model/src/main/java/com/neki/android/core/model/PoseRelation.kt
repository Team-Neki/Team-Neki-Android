package com.neki.android.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class PoseRelation(val displayText: String) {
    FRIEND("친구"),
    COUPLE("커플"),
    SSUM("썸"),
    ;

    override fun toString(): String = displayText
}

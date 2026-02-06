package com.neki.android.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class PeopleCount(val displayText: String, val value: Int) {
    ONE("1인", 1),
    TWO("2인", 2),
    THREE("3인", 3),
    FOUR("4인", 4),
    ;

    override fun toString(): String = displayText
}

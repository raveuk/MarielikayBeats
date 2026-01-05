package com.zimbabeats.core.domain.filter

import com.zimbabeats.core.domain.model.VideoCategory

enum class AgeGroup(val maxAge: Int, val label: String) {
    UNDER_5(5, "Under 5"),
    UNDER_10(10, "Under 10"),
    UNDER_12(12, "Under 12"),
    UNDER_14(14, "Under 14"),
    UNDER_16(16, "Under 16");

    companion object {
        fun fromAge(age: Int): AgeGroup = when {
            age < 5 -> UNDER_5
            age < 10 -> UNDER_10
            age < 12 -> UNDER_12
            age < 14 -> UNDER_14
            else -> UNDER_16
        }
    }
}

data class AgeConfig(
    val maxDuration: Int,
    val strictMode: Boolean,
    val allowedCategories: Set<VideoCategory>,
    val maxTitleLength: Int
)

data class FilterResult(
    val allowed: Boolean,
    val reason: String? = null,
    val score: Int
)

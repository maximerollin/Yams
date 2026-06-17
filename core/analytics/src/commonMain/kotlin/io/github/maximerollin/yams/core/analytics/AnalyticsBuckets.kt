package io.github.maximerollin.yams.core.analytics

public fun Int.toAnalyticsCountBucket(): String =
    when (this) {
        0 -> "0"
        in 1..2 -> "1-2"
        in 3..5 -> "3-5"
        in 6..10 -> "6-10"
        else -> "11+"
    }

public fun Int.toAnalyticsScoreBucket(): String =
    when {
        this <= 0 -> "0"
        this <= 10 -> "1-10"
        this <= 20 -> "11-20"
        this <= 40 -> "21-40"
        this <= 60 -> "41-60"
        this <= 80 -> "61-80"
        this <= 100 -> "81-100"
        else -> "101+"
    }

public fun Float.toAnalyticsAverageBucket(): String =
    when {
        this <= 0f -> "0"
        this <= 5f -> "0-5"
        this <= 10f -> "5-10"
        this <= 15f -> "10-15"
        this <= 20f -> "15-20"
        else -> "20+"
    }

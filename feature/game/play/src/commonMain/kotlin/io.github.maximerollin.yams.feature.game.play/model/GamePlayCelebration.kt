package io.github.maximerollin.yams.feature.game.play.model

import io.github.maximerollin.yams.core.model.ScoreKey

internal const val BigScoreThreshold: Int = 30
internal const val CelebrationFallbackEnterMillis: Long = 120L
internal const val CelebrationFallbackHoldMillis: Long = 800L
internal const val CelebrationFallbackExitMillis: Long = 120L

internal data class GamePlayCelebration(
    val id: Int,
    val type: GamePlayCelebrationType,
    val playerName: String,
    val score: Int,
    val dieFace: Int? = null,
)

internal enum class GamePlayCelebrationType {
    YAMS,
    BIG_SCORE,
}

internal val GamePlayCelebrationType.timeoutMillis: Long
    get() = when (this) {
        GamePlayCelebrationType.YAMS -> 2_200L
        GamePlayCelebrationType.BIG_SCORE -> 1_600L
    }

internal val GamePlayCelebrationType.copyRevealProgress: Float
    get() = when (this) {
        GamePlayCelebrationType.YAMS -> 0.30f
        GamePlayCelebrationType.BIG_SCORE -> 0.26f
    }

internal fun celebrationTypeFor(
    score: Int,
    isYams: Boolean,
): GamePlayCelebrationType? = when {
    score <= 0 -> null
    isYams -> GamePlayCelebrationType.YAMS
    score >= BigScoreThreshold -> GamePlayCelebrationType.BIG_SCORE
    else -> null
}

internal fun GamePlayCelebration?.clearIfCompleted(
    completedId: Int,
): GamePlayCelebration? = takeUnless { celebration -> celebration?.id == completedId }

internal fun fiveOfAKindDieFace(scoreKey: ScoreKey, score: Int): Int? {
    val value = when (scoreKey) {
        ScoreKey.ONES -> 1
        ScoreKey.TWOS -> 2
        ScoreKey.THREES -> 3
        ScoreKey.FOURS -> 4
        ScoreKey.FIVES -> 5
        ScoreKey.SIXES -> 6
        else -> return null
    }
    return value.takeIf { score == value * 5 }
}

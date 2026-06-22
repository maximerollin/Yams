package io.github.maximerollin.yams.feature.game.play.model

internal const val BigScoreThreshold: Int = 30
internal const val CelebrationFallbackEnterMillis: Long = 120L
internal const val CelebrationFallbackHoldMillis: Long = 800L
internal const val CelebrationFallbackExitMillis: Long = 120L

internal data class GamePlayCelebration(
    val id: Int,
    val type: GamePlayCelebrationType,
    val playerName: String,
    val score: Int,
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

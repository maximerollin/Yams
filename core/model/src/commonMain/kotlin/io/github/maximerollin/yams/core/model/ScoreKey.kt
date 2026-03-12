package io.github.maximerollin.yams.core.model

import kotlinx.serialization.Serializable

@Serializable
public data class ScoreKey(public val value: String) {
    init {
        require(value.isNotBlank()) { "Score key must not be blank" }
    }

    public companion object {
        public val ONES: ScoreKey = ScoreKey("ones")
        public val TWOS: ScoreKey = ScoreKey("twos")
        public val THREES: ScoreKey = ScoreKey("threes")
        public val FOURS: ScoreKey = ScoreKey("fours")
        public val FIVES: ScoreKey = ScoreKey("fives")
        public val SIXES: ScoreKey = ScoreKey("sixes")
        public val THREE_OF_A_KIND: ScoreKey = ScoreKey("three_of_a_kind")
        public val FOUR_OF_A_KIND: ScoreKey = ScoreKey("four_of_a_kind")
        public val FULL_HOUSE: ScoreKey = ScoreKey("full_house")
        public val SMALL_STRAIGHT: ScoreKey = ScoreKey("small_straight")
        public val LARGE_STRAIGHT: ScoreKey = ScoreKey("large_straight")
        public val FIVE_OF_A_KIND: ScoreKey = ScoreKey("five_of_a_kind")
        public val EXTRA_FIVE_OF_A_KIND: ScoreKey = ScoreKey("extra_five_of_a_kind")
        public val CHANCE: ScoreKey = ScoreKey("chance")

        public fun custom(ruleId: String): ScoreKey =
            ScoreKey("custom_${ruleId.asScoreKeySegment()}")
    }
}

private fun String.asScoreKeySegment(): String = buildString(length) {
    this@asScoreKeySegment
        .trim()
        .lowercase()
        .forEach { character ->
            append(
                when {
                    character.isLetterOrDigit() -> character
                    character == '_' -> character
                    else -> '_'
                }
            )
        }
}
    .replace(Regex("_+"), "_")
    .trim('_')

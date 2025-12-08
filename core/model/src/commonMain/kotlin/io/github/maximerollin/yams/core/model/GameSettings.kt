package io.github.maximerollin.yams.core.model

import kotlinx.serialization.Serializable

@Serializable
public sealed class GameSettings {
    public abstract val columnCount: Int
    public abstract val threeOfAKindScoring: ThreeOfAKindScoring?
    public abstract val threeOfAKindValue: Int?
    public abstract val fourOfAKindScoring: FourOfAKindScoring?
    public abstract val fourOfAKindValue: Int?
    public abstract val fullHouseValue: Int
    public abstract val smallStraightValue: Int?
    public abstract val largeStraightValue: Int?
    public abstract val fiveOfAKindValue: Int
    public abstract val jokerRule: JokerRule
    public abstract val extraFiveOfAKindValue: Int?
    public abstract val upperBonusThreshold: Int
    public abstract val upperBonusValue: Int
    public abstract val customGameSettings: List<CustomGameSettings>

    public data class YamsSettings(
        override val columnCount: Int = DEFAULT_COLUMN_COUNT,
        override val threeOfAKindScoring: ThreeOfAKindScoring = ThreeOfAKindScoring.SUM_MATCHING_THREE,
        override val fourOfAKindScoring: FourOfAKindScoring = FourOfAKindScoring.SUM_MATCHING_FOUR,
        override val threeOfAKindValue: Int? = null,
        override val fourOfAKindValue: Int? = null,
        override val fullHouseValue: Int = DEFAULT_FULL_HOUSE_VALUE,
        override val smallStraightValue: Int? = DEFAULT_SMALL_STRAIGHT_VALUE,
        override val largeStraightValue: Int? = DEFAULT_LARGE_STRAIGHT_VALUE,
        override val fiveOfAKindValue: Int = DEFAULT_FIVE_OF_A_KIND_VALUE,
        override val jokerRule: JokerRule = JokerRule.DISABLED,
        override val extraFiveOfAKindValue: Int? = DEFAULT_EXTRA_FIVE_OF_A_KIND_VALUE,
        override val upperBonusThreshold: Int = DEFAULT_UPPER_BONUS_THRESHOLD,
        override val upperBonusValue: Int = DEFAULT_UPPER_BONUS_VALUE,
        override val customGameSettings: List<CustomGameSettings> = emptyList(),
    ) : GameSettings()

    public data class YahtzeeSettings(
        override val columnCount: Int = DEFAULT_COLUMN_COUNT,
        override val threeOfAKindScoring: ThreeOfAKindScoring = DEFAULT_THREE_OF_A_KIND_SCORING,
        override val fourOfAKindScoring: FourOfAKindScoring = DEFAULT_FOUR_OF_A_KIND_SCORING,
        override val threeOfAKindValue: Int? = null,
        override val fourOfAKindValue: Int? = null,
        override val fullHouseValue: Int = DEFAULT_FULL_HOUSE_VALUE,
        override val smallStraightValue: Int? = DEFAULT_SMALL_STRAIGHT_VALUE,
        override val largeStraightValue: Int? = DEFAULT_LARGE_STRAIGHT_VALUE,
        override val fiveOfAKindValue: Int = DEFAULT_FIVE_OF_A_KIND_VALUE,
        override val jokerRule: JokerRule = JokerRule.DISABLED,
        override val extraFiveOfAKindValue: Int? = null,
        override val upperBonusThreshold: Int = DEFAULT_UPPER_BONUS_THRESHOLD,
        override val upperBonusValue: Int = DEFAULT_UPPER_BONUS_VALUE,
        override val customGameSettings: List<CustomGameSettings> = emptyList(),
    ) : GameSettings()

    public data class MomsSettings(
        override val columnCount: Int = DEFAULT_COLUMN_COUNT,
        override val threeOfAKindScoring: ThreeOfAKindScoring? = null,
        override val fourOfAKindScoring: FourOfAKindScoring = FourOfAKindScoring.FIXED,
        override val threeOfAKindValue: Int? = null,
        override val fourOfAKindValue: Int? = 20,
        override val fullHouseValue: Int = 20,
        override val smallStraightValue: Int? = DEFAULT_SMALL_STRAIGHT_VALUE,
        override val largeStraightValue: Int? = null,
        override val fiveOfAKindValue: Int = DEFAULT_FIVE_OF_A_KIND_VALUE,
        override val jokerRule: JokerRule = JokerRule.DISABLED,
        override val extraFiveOfAKindValue: Int? = null,
        override val upperBonusThreshold: Int = 60,
        override val upperBonusValue: Int = 30,
        override val customGameSettings: List<CustomGameSettings> = emptyList(),
    ) : GameSettings()

    public companion object {
        public const val DEFAULT_COLUMN_COUNT: Int = 1
        public val DEFAULT_THREE_OF_A_KIND_SCORING: ThreeOfAKindScoring =
            ThreeOfAKindScoring.SUM_ALL_FIVE_DICE
        public val DEFAULT_FOUR_OF_A_KIND_SCORING: FourOfAKindScoring =
            FourOfAKindScoring.SUM_ALL_FIVE_DICE
        public val DEFAULT_THREE_OF_A_KIND_VALUE: Int? = null
        public val DEFAULT_FOUR_OF_A_KIND_VALUE: Int? = null
        public const val DEFAULT_FULL_HOUSE_VALUE: Int = 25
        public const val DEFAULT_SMALL_STRAIGHT_VALUE: Int = 30
        public const val DEFAULT_LARGE_STRAIGHT_VALUE: Int = 40
        public const val DEFAULT_FIVE_OF_A_KIND_VALUE: Int = 50
        public val DEFAULT_JOKER_RULE: JokerRule = JokerRule.ENABLED
        public const val DEFAULT_EXTRA_FIVE_OF_A_KIND_VALUE: Int = 100
        public const val DEFAULT_UPPER_BONUS_THRESHOLD: Int = 63
        public const val DEFAULT_UPPER_BONUS_VALUE: Int = 35
    }
}

@Serializable
public data class CustomGameSettings(
    val title: String,
    val scoring: CustomGameSettingsScoring,
    val value: Int?,
)

@Serializable
public enum class CustomGameSettingsScoring {
    SUM_ALL_FIVE_DICE,
    FIXED
}

@Serializable
public enum class ThreeOfAKindScoring {
    SUM_MATCHING_THREE,
    SUM_ALL_FIVE_DICE,
    FIXED,
    FIXED_CUSTOM,
}

@Serializable
public enum class FourOfAKindScoring {
    SUM_MATCHING_FOUR,
    SUM_ALL_FIVE_DICE,
    FIXED,
    FIXED_CUSTOM,
}

@Serializable
public enum class JokerRule {
    ENABLED, // Joker Yahtzee autorisé (variante US/UK)
    DISABLED // Pas de joker officiel (variante Yam's FR)
}
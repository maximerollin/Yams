package io.github.maximerollin.yams.core.model

import kotlinx.serialization.Serializable

@Serializable
public sealed class GameSettings {
    public abstract val ruleSet: RuleSet
    public abstract val columnCount: Int
    public abstract val chanceValue: SettingsScoring?
    public abstract val threeOfAKindScoring: SettingsScoring?
    public abstract val threeOfAKindValue: Int?
    public abstract val fourOfAKindScoring: SettingsScoring?
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
        override val ruleSet: RuleSet = DEFAULT_RULE_SET_VALUE,
        override val columnCount: Int = DEFAULT_COLUMN_COUNT,
        override val chanceValue: SettingsScoring? = DEFAULT_CHANCE_VALUE,
        override val threeOfAKindScoring: SettingsScoring = SettingsScoring.SUM_MATCHING_THREE,
        override val fourOfAKindScoring: SettingsScoring = SettingsScoring.SUM_MATCHING_FOUR,
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
        override val ruleSet: RuleSet = RuleSet.YAHTZEE,
        override val columnCount: Int = DEFAULT_COLUMN_COUNT,
        override val chanceValue: SettingsScoring? = DEFAULT_CHANCE_VALUE,
        override val threeOfAKindScoring: SettingsScoring = DEFAULT_THREE_OF_A_KIND_SCORING,
        override val fourOfAKindScoring: SettingsScoring = DEFAULT_FOUR_OF_A_KIND_SCORING,
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

    public data class CustomSettings(
        override val ruleSet: RuleSet = DEFAULT_RULE_SET_VALUE,
        override val columnCount: Int = DEFAULT_COLUMN_COUNT,
        override val chanceValue: SettingsScoring? = DEFAULT_CHANCE_VALUE,
        override val threeOfAKindScoring: SettingsScoring = DEFAULT_THREE_OF_A_KIND_SCORING,
        override val fourOfAKindScoring: SettingsScoring = DEFAULT_FOUR_OF_A_KIND_SCORING,
        override val threeOfAKindValue: Int? = DEFAULT_THREE_OF_A_KIND_VALUE,
        override val fourOfAKindValue: Int? = DEFAULT_FOUR_OF_A_KIND_VALUE,
        override val fullHouseValue: Int = DEFAULT_FULL_HOUSE_VALUE,
        override val smallStraightValue: Int? = DEFAULT_SMALL_STRAIGHT_VALUE,
        override val largeStraightValue: Int? = DEFAULT_LARGE_STRAIGHT_VALUE,
        override val fiveOfAKindValue: Int = DEFAULT_FIVE_OF_A_KIND_VALUE,
        override val jokerRule: JokerRule = DEFAULT_JOKER_RULE,
        override val extraFiveOfAKindValue: Int? = DEFAULT_EXTRA_FIVE_OF_A_KIND_VALUE,
        override val upperBonusThreshold: Int = DEFAULT_UPPER_BONUS_THRESHOLD,
        override val upperBonusValue: Int = DEFAULT_UPPER_BONUS_VALUE,
        override val customGameSettings: List<CustomGameSettings> = emptyList(),
    ) : GameSettings()

    public companion object {
        public val DEFAULT_RULE_SET_VALUE: RuleSet = RuleSet.YAMS
        public const val DEFAULT_COLUMN_COUNT: Int = 1
        public val DEFAULT_CHANCE_VALUE: SettingsScoring = SettingsScoring.SUM_ALL_FIVE_DICE
        public val DEFAULT_THREE_OF_A_KIND_SCORING: SettingsScoring =
            SettingsScoring.SUM_ALL_FIVE_DICE
        public val DEFAULT_FOUR_OF_A_KIND_SCORING: SettingsScoring =
            SettingsScoring.SUM_ALL_FIVE_DICE
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

    @Serializable
    public enum class RuleSet {
        YAMS,
        YAHTZEE,
        CUSTOM
    }

    @Serializable
    public data class CustomGameSettings(
        val title: String,
        val scoring: SettingsScoring,
        val value: Int?,
        val description: String?,
    )

    @Serializable
    public enum class SettingsScoring {
        SUM_ALL_FIVE_DICE,
        SUM_MATCHING_THREE,
        SUM_MATCHING_FOUR,
        FIXED,
        FIXED_CUSTOM
    }

    @Serializable
    public enum class JokerRule {
        ENABLED,
        DISABLED,
    }
}
package io.github.maximerollin.yams.data.game.mapper

import io.github.maximerollin.yams.core.database.entity.CustomGameSettingsEntity
import io.github.maximerollin.yams.core.database.entity.GameSettingsEntity
import io.github.maximerollin.yams.core.database.entity.RuleSetEntity
import io.github.maximerollin.yams.core.database.entity.SettingsScoringEntity
import io.github.maximerollin.yams.core.model.GameSettings

internal fun GameSettings.asEntity() = GameSettingsEntity(
    ruleSet = when (ruleSet) {
        GameSettings.RuleSet.YAMS -> RuleSetEntity.YAMS
        GameSettings.RuleSet.YAHTZEE -> RuleSetEntity.YAHTZEE
        GameSettings.RuleSet.CUSTOM -> RuleSetEntity.CUSTOM
    },
    columnCount = columnCount,
    chanceValue = when (chanceValue) {
        GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE -> SettingsScoringEntity.SUM_ALL_FIVE_DICE
        GameSettings.SettingsScoring.SUM_MATCHING_THREE -> SettingsScoringEntity.SUM_MATCHING_THREE
        GameSettings.SettingsScoring.SUM_MATCHING_FOUR -> SettingsScoringEntity.SUM_MATCHING_FOUR
        GameSettings.SettingsScoring.FIXED -> SettingsScoringEntity.FIXED
        GameSettings.SettingsScoring.FIXED_CUSTOM -> SettingsScoringEntity.FIXED_CUSTOM
        null -> null
    },
    isChanceEnabled = isChanceEnabled,
    threeOfAKindScoring = when (threeOfAKindScoring) {
        GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE -> SettingsScoringEntity.SUM_ALL_FIVE_DICE
        GameSettings.SettingsScoring.SUM_MATCHING_THREE -> SettingsScoringEntity.SUM_MATCHING_THREE
        GameSettings.SettingsScoring.SUM_MATCHING_FOUR -> SettingsScoringEntity.SUM_MATCHING_FOUR
        GameSettings.SettingsScoring.FIXED -> SettingsScoringEntity.FIXED
        GameSettings.SettingsScoring.FIXED_CUSTOM -> SettingsScoringEntity.FIXED_CUSTOM
        else -> SettingsScoringEntity.SUM_ALL_FIVE_DICE
    },
    threeOfAKindValue = threeOfAKindValue,
    isThreeOfAKindEnabled = isThreeOfAKindEnabled,
    fourOfAKindScoring = when (fourOfAKindScoring) {
        GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE -> SettingsScoringEntity.SUM_ALL_FIVE_DICE
        GameSettings.SettingsScoring.SUM_MATCHING_THREE -> SettingsScoringEntity.SUM_MATCHING_THREE
        GameSettings.SettingsScoring.SUM_MATCHING_FOUR -> SettingsScoringEntity.SUM_MATCHING_FOUR
        GameSettings.SettingsScoring.FIXED -> SettingsScoringEntity.FIXED
        GameSettings.SettingsScoring.FIXED_CUSTOM -> SettingsScoringEntity.FIXED_CUSTOM
        else -> SettingsScoringEntity.SUM_ALL_FIVE_DICE
    },
    fourOfAKindValue = fourOfAKindValue,
    isFourOfAKindEnabled = isFourOfAKindEnabled,
    fullHouseValue = fullHouseValue,
    isFullHouseEnabled = isFullHouseEnabled,
    smallStraightValue = smallStraightValue,
    isSmallStraightEnabled = isSmallStraightEnabled,
    largeStraightValue = largeStraightValue,
    isLargeStraightEnabled = isLargeStraightEnabled,
    fiveOfAKindValue = fiveOfAKindValue,
    isFiveOfAKindEnabled = isFiveOfAKindEnabled,
    jokerRule = jokerRule,
    extraFiveOfAKindValue = extraFiveOfAKindValue,
    isExtraFiveOfAKindEnabled = isExtraFiveOfAKindEnabled,
    upperBonusThreshold = upperBonusThreshold,
    upperBonusValue = upperBonusValue,
    isUpperBonusEnabled = isUpperBonusEnabled,
    areCustomRulesEnabled = areCustomRulesEnabled,
    customGameSettings = customGameSettings.map { it -> it.asEntity() }
)

internal fun GameSettings.CustomGameSettings.asEntity() = CustomGameSettingsEntity(
    title = title,
    id = id,
    scoring = when (scoring) {
        GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE -> SettingsScoringEntity.SUM_ALL_FIVE_DICE
        GameSettings.SettingsScoring.SUM_MATCHING_THREE -> SettingsScoringEntity.SUM_MATCHING_THREE
        GameSettings.SettingsScoring.SUM_MATCHING_FOUR -> SettingsScoringEntity.SUM_MATCHING_FOUR
        GameSettings.SettingsScoring.FIXED -> SettingsScoringEntity.FIXED
        GameSettings.SettingsScoring.FIXED_CUSTOM -> SettingsScoringEntity.FIXED_CUSTOM
    },
    value = value,
    description = description,
    isEnabled = isEnabled
)

package io.github.maximerollin.yams.data.game.mapper

import io.github.maximerollin.yams.core.database.entity.CustomGameSettingsEntity
import io.github.maximerollin.yams.core.database.entity.GameEntity
import io.github.maximerollin.yams.core.database.entity.RuleSetEntity
import io.github.maximerollin.yams.core.database.entity.SettingsScoringEntity
import io.github.maximerollin.yams.core.model.Game
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.GameSettings

internal fun GameEntity.asExternalModel(): Game {
    return when (finishedAt) {
        null -> asInProgressGameExternalModel()
        else -> asFinishedGameExternalModel()
    }
}

internal fun GameEntity.asInProgressGameExternalModel(): Game.GameInProgress {
    return Game.GameInProgress(
        id = GameId(value = id),
        settings = gameSettings(),
        startedAt = createdAt,
    )
}

internal fun GameEntity.asFinishedGameExternalModel(): Game.GameFinished {
    return Game.GameFinished(
        id = GameId(value = id),
        settings = gameSettings(),
        startedAt = createdAt,
        finishedAt = finishedAt ?: createdAt,
        photo = photo,
        gameNumber = gameNumber,
        yamCount = 0, // TODO : remove or add in entities and stuff
    )
}

private fun GameEntity.gameSettings(): GameSettings {
    val entitySettings = settings

    return when (entitySettings.ruleSet) {
        RuleSetEntity.YAMS -> GameSettings.YamsSettings(
            columnCount = entitySettings.columnCount,
            chanceValue = entitySettings.chanceValue?.asExternalModel(),
            isChanceEnabled = entitySettings.isChanceEnabled,
            threeOfAKindScoring = entitySettings.threeOfAKindScoring?.asExternalModel()
                ?: GameSettings.YamsSettings().threeOfAKindScoring,
            threeOfAKindValue = entitySettings.threeOfAKindValue,
            isThreeOfAKindEnabled = entitySettings.isThreeOfAKindEnabled,
            fourOfAKindScoring = entitySettings.fourOfAKindScoring?.asExternalModel()
                ?: GameSettings.YamsSettings().fourOfAKindScoring,
            fourOfAKindValue = entitySettings.fourOfAKindValue,
            isFourOfAKindEnabled = entitySettings.isFourOfAKindEnabled,
            fullHouseValue = entitySettings.fullHouseValue,
            isFullHouseEnabled = entitySettings.isFullHouseEnabled,
            smallStraightValue = entitySettings.smallStraightValue,
            isSmallStraightEnabled = entitySettings.isSmallStraightEnabled,
            largeStraightValue = entitySettings.largeStraightValue,
            isLargeStraightEnabled = entitySettings.isLargeStraightEnabled,
            fiveOfAKindValue = entitySettings.fiveOfAKindValue,
            isFiveOfAKindEnabled = entitySettings.isFiveOfAKindEnabled,
            jokerRule = entitySettings.jokerRule,
            extraFiveOfAKindValue = entitySettings.extraFiveOfAKindValue,
            isExtraFiveOfAKindEnabled = entitySettings.isExtraFiveOfAKindEnabled,
            upperBonusThreshold = entitySettings.upperBonusThreshold,
            upperBonusValue = entitySettings.upperBonusValue,
            isUpperBonusEnabled = entitySettings.isUpperBonusEnabled,
            areCustomRulesEnabled = entitySettings.areCustomRulesEnabled,
            customGameSettings = entitySettings.customGameSettings.asExternalModel(),
        )

        RuleSetEntity.YAHTZEE -> GameSettings.YahtzeeSettings(
            columnCount = entitySettings.columnCount,
            chanceValue = entitySettings.chanceValue?.asExternalModel(),
            isChanceEnabled = entitySettings.isChanceEnabled,
            threeOfAKindScoring = entitySettings.threeOfAKindScoring?.asExternalModel()
                ?: GameSettings.YahtzeeSettings().threeOfAKindScoring,
            threeOfAKindValue = entitySettings.threeOfAKindValue,
            isThreeOfAKindEnabled = entitySettings.isThreeOfAKindEnabled,
            fourOfAKindScoring = entitySettings.fourOfAKindScoring?.asExternalModel()
                ?: GameSettings.YahtzeeSettings().fourOfAKindScoring,
            fourOfAKindValue = entitySettings.fourOfAKindValue,
            isFourOfAKindEnabled = entitySettings.isFourOfAKindEnabled,
            fullHouseValue = entitySettings.fullHouseValue,
            isFullHouseEnabled = entitySettings.isFullHouseEnabled,
            smallStraightValue = entitySettings.smallStraightValue,
            isSmallStraightEnabled = entitySettings.isSmallStraightEnabled,
            largeStraightValue = entitySettings.largeStraightValue,
            isLargeStraightEnabled = entitySettings.isLargeStraightEnabled,
            fiveOfAKindValue = entitySettings.fiveOfAKindValue,
            isFiveOfAKindEnabled = entitySettings.isFiveOfAKindEnabled,
            jokerRule = entitySettings.jokerRule,
            extraFiveOfAKindValue = entitySettings.extraFiveOfAKindValue,
            isExtraFiveOfAKindEnabled = entitySettings.isExtraFiveOfAKindEnabled,
            upperBonusThreshold = entitySettings.upperBonusThreshold,
            upperBonusValue = entitySettings.upperBonusValue,
            isUpperBonusEnabled = entitySettings.isUpperBonusEnabled,
            areCustomRulesEnabled = entitySettings.areCustomRulesEnabled,
            customGameSettings = entitySettings.customGameSettings.asExternalModel(),
        )

        RuleSetEntity.CUSTOM -> GameSettings.CustomSettings(
            columnCount = entitySettings.columnCount,
            chanceValue = entitySettings.chanceValue?.asExternalModel(),
            isChanceEnabled = entitySettings.isChanceEnabled,
            threeOfAKindScoring = entitySettings.threeOfAKindScoring?.asExternalModel()
                ?: GameSettings.CustomSettings().threeOfAKindScoring,
            threeOfAKindValue = entitySettings.threeOfAKindValue,
            isThreeOfAKindEnabled = entitySettings.isThreeOfAKindEnabled,
            fourOfAKindScoring = entitySettings.fourOfAKindScoring?.asExternalModel()
                ?: GameSettings.CustomSettings().fourOfAKindScoring,
            fourOfAKindValue = entitySettings.fourOfAKindValue,
            isFourOfAKindEnabled = entitySettings.isFourOfAKindEnabled,
            fullHouseValue = entitySettings.fullHouseValue,
            isFullHouseEnabled = entitySettings.isFullHouseEnabled,
            smallStraightValue = entitySettings.smallStraightValue,
            isSmallStraightEnabled = entitySettings.isSmallStraightEnabled,
            largeStraightValue = entitySettings.largeStraightValue,
            isLargeStraightEnabled = entitySettings.isLargeStraightEnabled,
            fiveOfAKindValue = entitySettings.fiveOfAKindValue,
            isFiveOfAKindEnabled = entitySettings.isFiveOfAKindEnabled,
            jokerRule = entitySettings.jokerRule,
            extraFiveOfAKindValue = entitySettings.extraFiveOfAKindValue,
            isExtraFiveOfAKindEnabled = entitySettings.isExtraFiveOfAKindEnabled,
            upperBonusThreshold = entitySettings.upperBonusThreshold,
            upperBonusValue = entitySettings.upperBonusValue,
            isUpperBonusEnabled = entitySettings.isUpperBonusEnabled,
            areCustomRulesEnabled = entitySettings.areCustomRulesEnabled,
            customGameSettings = entitySettings.customGameSettings.asExternalModel(),
        )
    }
}

private fun SettingsScoringEntity.asExternalModel(): GameSettings.SettingsScoring {
    return when (this) {
        SettingsScoringEntity.SUM_ALL_FIVE_DICE -> GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE
        SettingsScoringEntity.SUM_MATCHING_THREE -> GameSettings.SettingsScoring.SUM_MATCHING_THREE
        SettingsScoringEntity.SUM_MATCHING_FOUR -> GameSettings.SettingsScoring.SUM_MATCHING_FOUR
        SettingsScoringEntity.FIXED -> GameSettings.SettingsScoring.FIXED
        SettingsScoringEntity.FIXED_CUSTOM -> GameSettings.SettingsScoring.FIXED_CUSTOM
    }
}

private fun List<CustomGameSettingsEntity>.asExternalModel():
        List<GameSettings.CustomGameSettings> {
    return map { customSetting ->
        GameSettings.CustomGameSettings(
            title = customSetting.title,
            id = customSetting.id,
            scoring = customSetting.scoring.asExternalModel(),
            value = customSetting.value,
            description = customSetting.description,
            isEnabled = customSetting.isEnabled,
        )
    }
}

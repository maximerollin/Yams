package io.github.maximerollin.yams.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.vinceglb.filekit.PlatformFile
import kotlin.time.Instant

@Entity(tableName = "Game")
public data class GameEntity(
    @PrimaryKey
    val id: String,
    @Embedded("settings")
    val settings: GameSettingsEntity,
    val status: GameStatusEntity,
    val createdAt: Instant,
    val updatedAt: Instant,
    val finishedAt: Instant?,
    val photo: PlatformFile?,
    val gameNumber: Int,
)

public data class GameSettingsEntity(
    val ruleSet: RuleSetEntity,
    val columnCount: Int,
    val chanceValue: SettingsScoringEntity?,
    val isChanceEnabled: Boolean,
    val threeOfAKindScoring: SettingsScoringEntity?,
    val threeOfAKindValue: Int?,
    val isThreeOfAKindEnabled: Boolean,
    val fourOfAKindScoring: SettingsScoringEntity?,
    val fourOfAKindValue: Int?,
    val isFourOfAKindEnabled: Boolean,
    val fullHouseValue: Int,
    val isFullHouseEnabled: Boolean,
    val smallStraightValue: Int?,
    val isSmallStraightEnabled: Boolean,
    val largeStraightValue: Int?,
    val isLargeStraightEnabled: Boolean,
    val fiveOfAKindValue: Int,
    val isFiveOfAKindEnabled: Boolean,
    val jokerRule: Boolean,
    val extraFiveOfAKindValue: Int?,
    val isExtraFiveOfAKindEnabled: Boolean,
    val upperBonusThreshold: Int,
    val upperBonusValue: Int,
    val isUpperBonusEnabled: Boolean,
    val areCustomRulesEnabled: Boolean,
    val customGameSettings: List<CustomGameSettingsEntity>,
)

public enum class SettingsScoringEntity {
    SUM_ALL_FIVE_DICE,
    SUM_MATCHING_THREE,
    SUM_MATCHING_FOUR,
    FIXED,
    FIXED_CUSTOM
}

public enum class RuleSetEntity {
    YAMS,
    YAHTZEE,
    CUSTOM,
}

public enum class GameStatusEntity {
    IN_PROGRESS,
    FINISHED,
    CANCELLED,
}

public data class CustomGameSettingsEntity(
    val title: String,
    val id: String,
    val scoring: SettingsScoringEntity,
    val value: Int?,
    val description: String?,
    val isEnabled: Boolean = true,
)

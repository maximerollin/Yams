package io.github.maximerollin.yams.feature.game.play.mock

import io.github.maximerollin.yams.core.mocks.UserMocks
import io.github.maximerollin.yams.core.model.Game
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.core.model.ScoreKey
import io.github.maximerollin.yams.data.game.model.Player
import io.github.maximerollin.yams.feature.game.play.model.GamePlayStateUi
import io.github.maximerollin.yams.feature.game.play.model.GameStatus
import io.github.maximerollin.yams.feature.game.play.model.PlayerState
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal fun gamePlayPreviewUiState(columnCount: Int = 1): GamePlayStateUi {
    val normalizedColumnCount = columnCount.coerceAtLeast(1)
    val settings = GameSettings.CustomSettings(
        columnCount = normalizedColumnCount,
        customGameSettings = listOf(
            GameSettings.CustomGameSettings(
                title = "Double paire",
                scoring = GameSettings.SettingsScoring.FIXED_CUSTOM,
                value = 15,
                description = "Deux paires différentes rapportent 15 pts",
            ),
            GameSettings.CustomGameSettings(
                title = "Tour du roi",
                scoring = GameSettings.SettingsScoring.FIXED_CUSTOM,
                value = 35,
                description = "Cinq dés supérieurs ou égaux à 3 rapportent 35 pts",
            ),
        ),
    )
    val gameId = GameId("preview-game")
    val game = Game.GameInProgress(
        id = gameId,
        settings = settings,
        startedAt = Clock.System.now(),
    )

    val draftPlayerStates = listOf(
        previewPlayerState(
            gameId = gameId,
            userIndex = 0,
            name = "Maxime",
            scoreEntries = mapOf(
                ScoreKey.ONES to columnValues(normalizedColumnCount, 2, null, 1),
                ScoreKey.TWOS to columnValues(normalizedColumnCount, 6, 4, null),
                ScoreKey.THREES to columnValues(normalizedColumnCount, 9, null, null),
                ScoreKey.FOURS to columnValues(normalizedColumnCount, null, 8, null),
                ScoreKey.FIVES to columnValues(normalizedColumnCount, 10, null, null),
                ScoreKey.SIXES to columnValues(normalizedColumnCount, 12, null, null),
                ScoreKey.THREE_OF_A_KIND to columnValues(normalizedColumnCount, 18, null, null),
                ScoreKey.FOUR_OF_A_KIND to columnValues(normalizedColumnCount, null, 24, null),
                ScoreKey.FULL_HOUSE to columnValues(normalizedColumnCount, 25, null, null),
                ScoreKey.SMALL_STRAIGHT to columnValues(normalizedColumnCount, 30, null, null),
                ScoreKey.LARGE_STRAIGHT to columnValues(normalizedColumnCount, null, null, null),
                ScoreKey.FIVE_OF_A_KIND to columnValues(normalizedColumnCount, null, null, null),
                ScoreKey.CHANCE to columnValues(normalizedColumnCount, 21, 19, null),
                ScoreKey.custom(settings.customGameSettings[0].id) to columnValues(
                    normalizedColumnCount,
                    15,
                    null,
                    null,
                ),
                ScoreKey.custom(settings.customGameSettings[1].id) to columnValues(
                    normalizedColumnCount,
                    null,
                    null,
                    null,
                ),
            ),
            settings = settings,
        ),
        previewPlayerState(
            gameId = gameId,
            userIndex = 1,
            name = "Lina",
            scoreEntries = mapOf(
                ScoreKey.ONES to columnValues(normalizedColumnCount, 3, 1, null, null),
                ScoreKey.TWOS to columnValues(normalizedColumnCount, 6, 4, null, null),
                ScoreKey.THREES to columnValues(normalizedColumnCount, 9, 6, 3, null),
                ScoreKey.FOURS to columnValues(normalizedColumnCount, 16, 8, null, null),
                ScoreKey.FIVES to columnValues(normalizedColumnCount, 20, null, 10, null),
                ScoreKey.SIXES to columnValues(normalizedColumnCount, 24, 12, null, null),
                ScoreKey.THREE_OF_A_KIND to columnValues(normalizedColumnCount, 19, null, 15, null),
                ScoreKey.FOUR_OF_A_KIND to columnValues(normalizedColumnCount, 24, null, null, null),
                ScoreKey.FULL_HOUSE to columnValues(normalizedColumnCount, null, 25, null, null),
                ScoreKey.SMALL_STRAIGHT to columnValues(normalizedColumnCount, 30, null, 30, null),
                ScoreKey.LARGE_STRAIGHT to columnValues(normalizedColumnCount, 40, null, null, null),
                ScoreKey.FIVE_OF_A_KIND to columnValues(normalizedColumnCount, 50, null, null, null),
                ScoreKey.CHANCE to columnValues(normalizedColumnCount, null, 21, 18, null),
                ScoreKey.custom(settings.customGameSettings[0].id) to columnValues(
                    normalizedColumnCount,
                    null,
                    15,
                    null,
                    null,
                ),
                ScoreKey.custom(settings.customGameSettings[1].id) to columnValues(
                    normalizedColumnCount,
                    35,
                    null,
                    null,
                    null,
                ),
            ),
            settings = settings,
        ),
        previewPlayerState(
            gameId = gameId,
            userIndex = 2,
            name = "Noa",
            scoreEntries = mapOf(
                ScoreKey.ONES to columnValues(normalizedColumnCount, null, 1, null),
                ScoreKey.TWOS to columnValues(normalizedColumnCount, 2, null, null),
                ScoreKey.THREES to columnValues(normalizedColumnCount, 9, null, null),
                ScoreKey.FOURS to columnValues(normalizedColumnCount, 8, null, null),
                ScoreKey.FIVES to columnValues(normalizedColumnCount, 15, 10, null),
                ScoreKey.SIXES to columnValues(normalizedColumnCount, null, null, null),
                ScoreKey.THREE_OF_A_KIND to columnValues(normalizedColumnCount, null, null, null),
                ScoreKey.FOUR_OF_A_KIND to columnValues(normalizedColumnCount, null, null, null),
                ScoreKey.FULL_HOUSE to columnValues(normalizedColumnCount, 25, null, null),
                ScoreKey.SMALL_STRAIGHT to columnValues(normalizedColumnCount, null, 30, null),
                ScoreKey.LARGE_STRAIGHT to columnValues(normalizedColumnCount, null, null, null),
                ScoreKey.FIVE_OF_A_KIND to columnValues(normalizedColumnCount, null, null, null),
                ScoreKey.CHANCE to columnValues(normalizedColumnCount, 23, 17, null),
                ScoreKey.custom(settings.customGameSettings[0].id) to columnValues(
                    normalizedColumnCount,
                    15,
                    null,
                    null,
                ),
                ScoreKey.custom(settings.customGameSettings[1].id) to columnValues(
                    normalizedColumnCount,
                    null,
                    null,
                    null,
                ),
            ),
            settings = settings,
        ),
    )

    val ranksByPlayerId = draftPlayerStates
        .groupBy(PlayerState::score)
        .entries
        .sortedByDescending { it.key }
        .fold(initial = 1 to emptyMap<String, Int>()) { (rank, ranks), entry ->
            val nextRanks = ranks + entry.value.associate { playerState ->
                playerState.player.userId.value to rank
            }
            rank + entry.value.size to nextRanks
        }
        .second

    val playerStates = draftPlayerStates.map { playerState ->
        playerState.copy(rank = ranksByPlayerId.getValue(playerState.player.userId.value))
    }

    return GamePlayStateUi(
        game = game,
        status = GameStatus.ONGOING,
        playerStates = playerStates,
        currentPlayer = playerStates[1].player,
    )
}

private fun previewPlayerState(
    gameId: GameId,
    userIndex: Int,
    name: String,
    scoreEntries: Map<ScoreKey, List<Int?>>,
    settings: GameSettings,
    extraFiveOfAKindScores: List<Int> = List(settings.columnCount) { 0 },
): PlayerState {
    val user = UserMocks.generate(name)
    val extraFiveOfAKindValue = settings.extraFiveOfAKindValue
    val extraFiveOfAKindCount = if (
        settings.isExtraFiveOfAKindEnabled &&
        extraFiveOfAKindValue != null &&
        extraFiveOfAKindValue > 0
    ) {
        extraFiveOfAKindScores.sumOf { score -> score / extraFiveOfAKindValue }
    } else {
        0
    }

    return PlayerState(
        player = Player(
            userId = user.id,
            name = user.name,
            avatar = user.avatar,
            gameId = gameId,
            userIndex = userIndex,
        ),
        scoreEntries = scoreEntries,
        extraFiveOfAKindScores = extraFiveOfAKindScores,
        score = previewScore(scoreEntries, extraFiveOfAKindScores, settings),
        rank = 0,
        fiveOfAKindCount = scoreEntries[ScoreKey.FIVE_OF_A_KIND]
            .orEmpty()
            .count { score -> (score ?: 0) > 0 } + extraFiveOfAKindCount,
    )
}

private fun previewScore(
    scoreEntries: Map<ScoreKey, List<Int?>>,
    extraFiveOfAKindScores: List<Int>,
    settings: GameSettings,
): Int {
    val baseScore = scoreEntries.values.sumOf { scores ->
        scores.sumOf { it ?: 0 }
    } + extraFiveOfAKindScores.sum()
    if (!settings.isUpperBonusEnabled) {
        return baseScore
    }

    val upperScoreKeys = setOf(
        ScoreKey.ONES,
        ScoreKey.TWOS,
        ScoreKey.THREES,
        ScoreKey.FOURS,
        ScoreKey.FIVES,
        ScoreKey.SIXES,
    )
    val upperBonus = List(settings.columnCount) { columnIndex ->
        val subtotal = upperScoreKeys.sumOf { key ->
            scoreEntries[key]?.getOrNull(columnIndex) ?: 0
        }
        if (subtotal >= settings.upperBonusThreshold) settings.upperBonusValue else 0
    }.sum()

    return baseScore + upperBonus
}

private fun columnValues(
    columnCount: Int,
    vararg values: Int?,
): List<Int?> = List(columnCount) { columnIndex ->
    values.getOrNull(columnIndex)
}

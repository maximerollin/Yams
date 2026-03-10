package io.github.maximerollin.yams.feature.game.play.mock

import io.github.maximerollin.yams.core.mocks.UserMocks
import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.feature.game.play.ScoreKey
import io.github.maximerollin.yams.feature.game.play.model.GamePlayPlayerSheet
import io.github.maximerollin.yams.feature.game.play.model.GamePlayStateUi

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

    return GamePlayStateUi(
        settings = settings,
        currentTurnPlayerIndex = 1,
        players = listOf(
            GamePlayPlayerSheet(
                user = UserMocks.generate("Maxime"),
                scores = mapOf(
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
                    ScoreKey.LARGE_STRAIGHT to columnValues(
                        normalizedColumnCount,
                        null,
                        null,
                        null
                    ),
                    ScoreKey.FIVE_OF_A_KIND to columnValues(
                        normalizedColumnCount,
                        null,
                        null,
                        null
                    ),
                    ScoreKey.EXTRA_FIVE_OF_A_KIND to columnValues(
                        normalizedColumnCount,
                        null,
                        null,
                        null
                    ),
                    ScoreKey.CHANCE to columnValues(normalizedColumnCount, 21, 19, null),
                    ScoreKey.custom("Double paire") to columnValues(
                        normalizedColumnCount,
                        15,
                        null,
                        null
                    ),
                    ScoreKey.custom("Tour du roi") to columnValues(
                        normalizedColumnCount,
                        null,
                        null,
                        null
                    ),
                ),
            ),
            GamePlayPlayerSheet(
                user = UserMocks.generate("Lina"),
                scores = mapOf(
                    ScoreKey.ONES to columnValues(normalizedColumnCount, 3, 1, null, null),
                    ScoreKey.TWOS to columnValues(normalizedColumnCount, 6, 4, null, null),
                    ScoreKey.THREES to columnValues(normalizedColumnCount, 9, 6, 3, null),
                    ScoreKey.FOURS to columnValues(normalizedColumnCount, 16, 8, null, null),
                    ScoreKey.FIVES to columnValues(normalizedColumnCount, 20, null, 10, null),
                    ScoreKey.SIXES to columnValues(normalizedColumnCount, 24, 12, null, null),
                    ScoreKey.THREE_OF_A_KIND to columnValues(
                        normalizedColumnCount,
                        19,
                        null,
                        15,
                        null
                    ),
                    ScoreKey.FOUR_OF_A_KIND to columnValues(
                        normalizedColumnCount,
                        24,
                        null,
                        null,
                        null
                    ),
                    ScoreKey.FULL_HOUSE to columnValues(
                        normalizedColumnCount,
                        null,
                        25,
                        null,
                        null
                    ),
                    ScoreKey.SMALL_STRAIGHT to columnValues(
                        normalizedColumnCount,
                        30,
                        null,
                        30,
                        null
                    ),
                    ScoreKey.LARGE_STRAIGHT to columnValues(
                        normalizedColumnCount,
                        40,
                        null,
                        null,
                        null
                    ),
                    ScoreKey.FIVE_OF_A_KIND to columnValues(
                        normalizedColumnCount,
                        50,
                        null,
                        null,
                        null
                    ),
                    ScoreKey.EXTRA_FIVE_OF_A_KIND to columnValues(
                        normalizedColumnCount,
                        null,
                        null,
                        null,
                        null
                    ),
                    ScoreKey.CHANCE to columnValues(normalizedColumnCount, null, 21, 18, null),
                    ScoreKey.custom("Double paire") to columnValues(
                        normalizedColumnCount,
                        null,
                        15,
                        null,
                        null
                    ),
                    ScoreKey.custom("Tour du roi") to columnValues(
                        normalizedColumnCount,
                        35,
                        null,
                        null,
                        null
                    ),
                ),
            ),
            GamePlayPlayerSheet(
                user = UserMocks.generate("Noa"),
                scores = mapOf(
                    ScoreKey.ONES to columnValues(normalizedColumnCount, null, 1, null),
                    ScoreKey.TWOS to columnValues(normalizedColumnCount, 2, null, null),
                    ScoreKey.THREES to columnValues(normalizedColumnCount, 9, null, null),
                    ScoreKey.FOURS to columnValues(normalizedColumnCount, 8, null, null),
                    ScoreKey.FIVES to columnValues(normalizedColumnCount, 15, 10, null),
                    ScoreKey.SIXES to columnValues(normalizedColumnCount, null, null, null),
                    ScoreKey.THREE_OF_A_KIND to columnValues(
                        normalizedColumnCount,
                        null,
                        null,
                        null
                    ),
                    ScoreKey.FOUR_OF_A_KIND to columnValues(
                        normalizedColumnCount,
                        null,
                        null,
                        null
                    ),
                    ScoreKey.FULL_HOUSE to columnValues(normalizedColumnCount, 25, null, null),
                    ScoreKey.SMALL_STRAIGHT to columnValues(normalizedColumnCount, null, 30, null),
                    ScoreKey.LARGE_STRAIGHT to columnValues(
                        normalizedColumnCount,
                        null,
                        null,
                        null
                    ),
                    ScoreKey.FIVE_OF_A_KIND to columnValues(
                        normalizedColumnCount,
                        null,
                        null,
                        null
                    ),
                    ScoreKey.EXTRA_FIVE_OF_A_KIND to columnValues(
                        normalizedColumnCount,
                        null,
                        null,
                        null
                    ),
                    ScoreKey.CHANCE to columnValues(normalizedColumnCount, 23, 17, null),
                    ScoreKey.custom("Double paire") to columnValues(
                        normalizedColumnCount,
                        15,
                        null,
                        null
                    ),
                    ScoreKey.custom("Tour du roi") to columnValues(
                        normalizedColumnCount,
                        null,
                        null,
                        null
                    ),
                ),
            ),
        ),
    )
}

private fun columnValues(
    columnCount: Int,
    vararg values: Int?,
): List<Int?> = List(columnCount) { columnIndex ->
    values.getOrNull(columnIndex)
}

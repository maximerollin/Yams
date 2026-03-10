package io.github.maximerollin.yams.feature.game.play.utils

import io.github.maximerollin.yams.core.model.Game
import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.core.model.ScoreKey
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.data.game.model.Player
import io.github.maximerollin.yams.data.game.model.ScoreEntry
import io.github.maximerollin.yams.feature.game.play.model.GameStatus
import io.github.maximerollin.yams.feature.game.play.model.PlayerState

internal class GamePlayStateLogic {

    companion object {
        private const val UPPER_SCORE_ENTRY_COUNT = 6
        private val upperScoreKeyValues = setOf(
            ScoreKey.Companion.ONES.value,
            ScoreKey.Companion.TWOS.value,
            ScoreKey.Companion.THREES.value,
            ScoreKey.Companion.FOURS.value,
            ScoreKey.Companion.FIVES.value,
            ScoreKey.Companion.SIXES.value,
        )

        fun getPlayerScore(
            scoreEntries: List<ScoreEntry>,
            gameSettings: GameSettings,
        ): Int {
            val baseScore = scoreEntries.sumOf(ScoreEntry::score)
            if (!gameSettings.isUpperBonusEnabled) {
                return baseScore
            }

            val upperBonus = scoreEntries
                .filter { it.cell.key.value in upperScoreKeyValues }
                .groupBy { it.cell.columnIndex }
                .values
                .sumOf { columnEntries ->
                    val subtotal = columnEntries.sumOf(ScoreEntry::score)
                    if (subtotal >= gameSettings.upperBonusThreshold) {
                        gameSettings.upperBonusValue
                    } else {
                        0
                    }
                }

            return baseScore + upperBonus
        }

        fun getRanks(scoreByPlayer: Map<UserId, Int>): Map<UserId, Int> {
            val ranks = mutableMapOf<UserId, Int>()
            var rank = 1
            scoreByPlayer.entries
                .groupBy { it.value }
                .entries
                .sortedByDescending { it.key }
                .forEach { (_, entries) ->
                    entries.forEach { entry ->
                        ranks[entry.key] = rank
                    }
                    rank += entries.size
                }
            return ranks
        }

        fun getCurrentPlayer(
            players: List<Player>,
            scoreEntriesByUser: Map<UserId, List<ScoreEntry>>,
        ): Player {
            return players
                .minByOrNull { scoreEntriesByUser[it.userId]?.size ?: 0 }
                ?: error("No players")
        }

        fun getGameStatus(playerStates: List<PlayerState>, game: Game): GameStatus {
            val totalScoreEntries = getGameTotalScoreEntries(game.settings)
            val allPlayersPlayedAllTurns = playerStates.all { playerState ->
                val turnsPlayed = playerState.scoreEntries.values.sumOf { columnScores ->
                    columnScores.count { it != null }
                }
                turnsPlayed == totalScoreEntries
            }

            if (!allPlayersPlayedAllTurns) {
                return GameStatus.ONGOING
            }

            return GameStatus.ENDED
        }

        fun getGameTotalScoreEntries(gameSettings: GameSettings): Int {
            val enabledStandardRuleCount = listOf(
                gameSettings.isThreeOfAKindEnabled,
                gameSettings.isFourOfAKindEnabled,
                gameSettings.isFullHouseEnabled,
                gameSettings.isSmallStraightEnabled,
                gameSettings.isLargeStraightEnabled,
                gameSettings.isFiveOfAKindEnabled,
                gameSettings.isExtraFiveOfAKindEnabled && gameSettings.extraFiveOfAKindValue != null,
                gameSettings.isChanceEnabled,
            ).count { it }

            val enabledCustomRuleCount = if (gameSettings.areCustomRulesEnabled) {
                gameSettings.customGameSettings.count { it.isEnabled }
            } else {
                0
            }

            val scoreEntriesPerColumn =
                UPPER_SCORE_ENTRY_COUNT + enabledStandardRuleCount + enabledCustomRuleCount

            return scoreEntriesPerColumn * gameSettings.columnCount
        }
    }
}

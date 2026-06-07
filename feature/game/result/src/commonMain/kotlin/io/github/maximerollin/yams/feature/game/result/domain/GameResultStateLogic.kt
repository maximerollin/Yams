package io.github.maximerollin.yams.feature.game.result.domain

import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.core.model.ScoreKey
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.data.game.model.ScoreEntry

internal class GameResultStateLogic {

    companion object {
        private val upperScoreKeyValues = setOf(
            ScoreKey.ONES.value,
            ScoreKey.TWOS.value,
            ScoreKey.THREES.value,
            ScoreKey.FOURS.value,
            ScoreKey.FIVES.value,
            ScoreKey.SIXES.value,
        )

        fun getPlayerScore(
            scoreEntries: List<ScoreEntry>,
            gameSettings: GameSettings,
        ): Int {
            val extraFiveOfAKindValue = gameSettings.extraFiveOfAKindValue
            val baseScore = scoreEntries.sumOf(ScoreEntry::score)
            val derivedExtraFiveOfAKindBonus = if (
                gameSettings.isExtraFiveOfAKindEnabled &&
                extraFiveOfAKindValue != null
            ) {
                scoreEntries.count { scoreEntry -> scoreEntry.awardsExtraFiveOfAKindBonus } * extraFiveOfAKindValue
            } else {
                0
            }

            if (!gameSettings.isUpperBonusEnabled) {
                return baseScore + derivedExtraFiveOfAKindBonus
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

            return baseScore + derivedExtraFiveOfAKindBonus + upperBonus
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

        fun getFiveOfAKindCount(scoreEntries: List<ScoreEntry>): Int =
            scoreEntries.count { scoreEntry ->
                scoreEntry.cell.key == ScoreKey.FIVE_OF_A_KIND && scoreEntry.score > 0
            } + scoreEntries.count { scoreEntry -> scoreEntry.awardsExtraFiveOfAKindBonus }
    }
}

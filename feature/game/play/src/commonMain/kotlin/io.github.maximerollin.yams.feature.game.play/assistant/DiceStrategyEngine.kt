package io.github.maximerollin.yams.feature.game.play.assistant

import io.github.maximerollin.yams.core.model.ScoreKey
import io.github.maximerollin.yams.feature.game.play.DiceScoreCell
import io.github.maximerollin.yams.feature.game.play.DiceScoreContext
import io.github.maximerollin.yams.feature.game.play.scoreDiceForOpenCells

internal class DiceStrategyEngine {
    fun recommend(
        context: DiceAssistantContext,
        roll: ConfirmedDiceRoll,
    ): DiceRecommendation {
        val scoreContext = context.toScoreContext()
        if (roll.rollIndex == RollIndex.THREE) {
            return bestScoreRecommendation(
                dice = roll.faces,
                scoreContext = scoreContext,
            )
        }

        val memo = mutableMapOf<StrategyState, Double>()
        val bestKeep = keepCandidates(roll.faces)
            .map { keepFaces ->
                val expectedValue = expectedValueForKeep(
                    keepFaces = keepFaces,
                    rollIndex = roll.rollIndex,
                    scoreContext = scoreContext,
                    memo = memo,
                )
                KeepEvaluation(
                    keepFaces = keepFaces,
                    expectedValue = expectedValue,
                )
            }
            .sortedWith(
                compareByDescending<KeepEvaluation> { it.expectedValue }
                    .thenByDescending { it.keepFaces.size }
                    .thenByDescending { it.keepFaces.sum() }
            )
            .firstOrNull()

        return if (bestKeep == null) {
            DiceRecommendation.NoAvailableMove()
        } else {
            DiceRecommendation.KeepDice(
                keepFaces = bestKeep.keepFaces,
                rerollCount = 5 - bestKeep.keepFaces.size,
                expectedValue = bestKeep.expectedValue,
            )
        }
    }

    private fun bestScoreRecommendation(
        dice: List<Int>,
        scoreContext: DiceScoreContext,
    ): DiceRecommendation {
        val bestCell = bestScoreCell(
            dice = dice,
            scoreContext = scoreContext,
        ) ?: return DiceRecommendation.NoAvailableMove()

        return DiceRecommendation.ScoreCell(
            key = bestCell.key,
            columnIndex = bestCell.columnIndex,
            score = bestCell.score,
            awardsExtraFiveOfAKindBonus = bestCell.awardsExtraFiveOfAKindBonus,
            expectedValue = bestCell.valueWithBonuses.toDouble(),
        )
    }

    private fun expectedValue(
        dice: List<Int>,
        rollIndex: RollIndex,
        scoreContext: DiceScoreContext,
        memo: MutableMap<StrategyState, Double>,
    ): Double {
        val state = StrategyState(
            dice = dice.sorted(),
            rollIndex = rollIndex,
        )
        memo[state]?.let { return it }

        val value = if (rollIndex == RollIndex.THREE) {
            bestScoreCell(
                dice = state.dice,
                scoreContext = scoreContext,
            )?.valueWithBonuses?.toDouble() ?: 0.0
        } else {
            keepCandidates(state.dice)
                .maxOfOrNull { keepFaces ->
                    expectedValueForKeep(
                        keepFaces = keepFaces,
                        rollIndex = rollIndex,
                        scoreContext = scoreContext,
                        memo = memo,
                    )
                } ?: 0.0
        }

        memo[state] = value
        return value
    }

    private fun expectedValueForKeep(
        keepFaces: List<Int>,
        rollIndex: RollIndex,
        scoreContext: DiceScoreContext,
        memo: MutableMap<StrategyState, Double>,
    ): Double {
        val rerollCount = 5 - keepFaces.size
        val outcomes = outcomesForDiceCount(rerollCount)
        val totalWeight = outcomes.sumOf(DiceOutcome::weight).coerceAtLeast(1)
        return outcomes.sumOf { outcome ->
            outcome.weight * expectedValue(
                dice = (keepFaces + outcome.faces).sorted(),
                rollIndex = rollIndex.next(),
                scoreContext = scoreContext,
                memo = memo,
            )
        } / totalWeight
    }

    private fun bestScoreCell(
        dice: List<Int>,
        scoreContext: DiceScoreContext,
    ): DiceScoreCell? =
        scoreDiceForOpenCells(
            dice = dice,
            context = scoreContext,
        )
            .sortedWith(
                compareByDescending<DiceScoreCell> { it.valueWithBonuses }
                    .thenByDescending { it.score }
                    .thenBy { it.columnIndex }
                    .thenBy { scoreKeySortValue(it.key) }
            )
            .firstOrNull()

    private fun keepCandidates(dice: List<Int>): List<List<Int>> {
        val candidates = buildSet {
            val maxMask = 1 shl dice.size
            for (mask in 0 until maxMask) {
                add(
                    dice
                        .filterIndexed { index, _ -> mask and (1 shl index) != 0 }
                        .sorted()
                )
            }
        }
        return candidates.sortedWith(
            compareBy<List<Int>> { it.size }
                .thenBy { it.joinToString(separator = "") }
        )
    }

    private fun outcomesForDiceCount(count: Int): List<DiceOutcome> =
        outcomeCache.getOrPut(count) {
            if (count == 0) {
                listOf(DiceOutcome(faces = emptyList(), weight = 1))
            } else {
                val groupedOutcomes = mutableMapOf<List<Int>, Int>()

                fun roll(depth: Int, faces: MutableList<Int>) {
                    if (depth == count) {
                        val key = faces.sorted()
                        groupedOutcomes[key] = (groupedOutcomes[key] ?: 0) + 1
                        return
                    }

                    for (face in 1..6) {
                        faces.add(face)
                        roll(depth = depth + 1, faces = faces)
                        faces.removeAt(faces.lastIndex)
                    }
                }

                roll(depth = 0, faces = mutableListOf())
                groupedOutcomes.map { (faces, weight) ->
                    DiceOutcome(faces = faces, weight = weight)
                }
            }
        }

    private fun DiceAssistantContext.toScoreContext(): DiceScoreContext =
        DiceScoreContext(
            settings = settings,
            scoreEntries = scoreEntries,
            extraFiveOfAKindScores = extraFiveOfAKindScores,
        )

    private fun scoreKeySortValue(key: ScoreKey): String = key.value

    private data class StrategyState(
        val dice: List<Int>,
        val rollIndex: RollIndex,
    )

    private data class KeepEvaluation(
        val keepFaces: List<Int>,
        val expectedValue: Double,
    )

    private data class DiceOutcome(
        val faces: List<Int>,
        val weight: Int,
    )

    private companion object {
        val outcomeCache: MutableMap<Int, List<DiceOutcome>> = mutableMapOf()
    }
}

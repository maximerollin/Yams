package io.github.maximerollin.yams.feature.game.play.assistant

import io.github.maximerollin.yams.core.model.GameSettings
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
                objectives = bestObjectivesForKeep(
                    keepFaces = bestKeep.keepFaces,
                    scoreContext = scoreContext,
                ),
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

    private fun bestObjectivesForKeep(
        keepFaces: List<Int>,
        scoreContext: DiceScoreContext,
    ): List<DiceObjective> {
        val bestByScoreKey = outcomesForDiceCount(5 - keepFaces.size)
            .flatMap { outcome ->
                val targetFaces = (keepFaces + outcome.faces).sorted()
                scoreDiceForOpenCells(
                    dice = targetFaces,
                    context = scoreContext,
                )
                    .filter { it.score > 0 }
                    .map { cell ->
                        ObjectiveEvaluation(
                            targetFaces = targetFaces,
                            cell = cell,
                        )
                    }
            }
            .groupBy { it.cell.key }
            .mapValues { (_, evaluations) ->
                evaluations.maxWithOrNull(objectiveComparator)!!
            }
            .values

        return bestByScoreKey
            .sortedWith(objectiveComparator.reversed())
            .distinctBy(ObjectiveEvaluation::targetFaces)
            .take(3)
            .map { evaluation ->
                DiceObjective(
                    key = evaluation.cell.key,
                    columnIndex = evaluation.cell.columnIndex,
                    faces = objectiveFaces(
                        key = evaluation.cell.key,
                        targetFaces = evaluation.targetFaces,
                        keepFaces = keepFaces,
                        settings = scoreContext.settings,
                    ),
                    score = evaluation.cell.score,
                    awardsExtraFiveOfAKindBonus = evaluation.cell.awardsExtraFiveOfAKindBonus,
                )
            }
    }

    private fun objectiveFaces(
        key: ScoreKey,
        targetFaces: List<Int>,
        keepFaces: List<Int>,
        settings: GameSettings,
    ): List<Int> = when (key) {
        ScoreKey.THREE_OF_A_KIND -> matchingObjectiveFaces(
            targetFaces = targetFaces,
            requiredCount = 3,
            scoring = settings.threeOfAKindScoring,
        )

        ScoreKey.FOUR_OF_A_KIND -> matchingObjectiveFaces(
            targetFaces = targetFaces,
            requiredCount = 4,
            scoring = settings.fourOfAKindScoring,
        )

        ScoreKey.SMALL_STRAIGHT -> smallStraightFaces(
            targetFaces = targetFaces,
            keepFaces = keepFaces,
        ).ifEmpty { targetFaces }

        else -> targetFaces
    }

    private fun matchingObjectiveFaces(
        targetFaces: List<Int>,
        requiredCount: Int,
        scoring: GameSettings.SettingsScoring?,
    ): List<Int> = when (scoring) {
        GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE -> targetFaces
        GameSettings.SettingsScoring.SUM_MATCHING_THREE ->
            matchingFaces(targetFaces, requiredCount = 3)

        GameSettings.SettingsScoring.SUM_MATCHING_FOUR ->
            matchingFaces(targetFaces, requiredCount = 4)

        GameSettings.SettingsScoring.FIXED,
        GameSettings.SettingsScoring.FIXED_CUSTOM -> matchingFaces(targetFaces, requiredCount)

        null -> targetFaces
    }

    private fun matchingFaces(
        faces: List<Int>,
        requiredCount: Int,
    ): List<Int> {
        val matchingFace = faces
            .groupingBy { it }
            .eachCount()
            .filterValues { it >= requiredCount }
            .keys
            .maxOrNull()
            ?: return faces

        return List(requiredCount) { matchingFace }
    }

    private fun smallStraightFaces(
        targetFaces: List<Int>,
        keepFaces: List<Int>,
    ): List<Int> = smallStraightPatterns
        .filter { pattern -> pattern.all(targetFaces::contains) }
        .maxWithOrNull(
            compareBy<List<Int>> { pattern -> keepFaces.count(pattern::contains) }
                .thenByDescending { pattern -> pattern.sum() },
        )
        .orEmpty()

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

    private data class ObjectiveEvaluation(
        val targetFaces: List<Int>,
        val cell: DiceScoreCell,
    )

    private companion object {
        val outcomeCache: MutableMap<Int, List<DiceOutcome>> = mutableMapOf()

        val smallStraightPatterns: List<List<Int>> = listOf(
            listOf(1, 2, 3, 4),
            listOf(2, 3, 4, 5),
            listOf(3, 4, 5, 6),
        )

        val objectiveComparator: Comparator<ObjectiveEvaluation> =
            compareBy<ObjectiveEvaluation> { it.cell.score }
                .thenBy { it.cell.valueWithBonuses }
                .thenByDescending { it.targetFaces.sum() }
                .thenByDescending { it.targetFaces.joinToString(separator = "") }
    }
}

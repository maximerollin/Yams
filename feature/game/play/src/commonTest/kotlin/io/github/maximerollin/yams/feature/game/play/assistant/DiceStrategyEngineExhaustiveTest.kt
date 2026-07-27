package io.github.maximerollin.yams.feature.game.play.assistant

import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.core.model.ScoreKey
import io.github.maximerollin.yams.feature.game.play.DiceScoreCell
import io.github.maximerollin.yams.feature.game.play.DiceScoreContext
import io.github.maximerollin.yams.feature.game.play.scoreDiceForOpenCells
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DiceStrategyEngineExhaustiveTest {
    private val engine = DiceStrategyEngine()

    @Test
    fun everyOrderedRollSatisfiesStrategyInvariantsForStandardYams() {
        assertEquals(ORDERED_ROLL_COUNT, orderedRolls.size)
        assertEquals(ORDERED_ROLL_COUNT, orderedRolls.toSet().size)
        assertEquals(CANONICAL_ROLL_COUNT, canonicalRolls.size)

        assertScenarioExhaustively(standardScenario)
    }

    @Test
    fun everyOrderedRollSatisfiesStrategyInvariantsForComplexRules() {
        assertScenarioExhaustively(complexScenario)
    }

    private fun assertScenarioExhaustively(scenario: StrategyScenario) {
        RollIndex.entries.forEach { rollIndex ->
            val recommendationsByCanonicalRoll = canonicalRolls.associateWith { faces ->
                engine.recommend(
                    context = scenario.context,
                    roll = confirmedRoll(faces = faces, rollIndex = rollIndex),
                )
            }

            determinismRolls.forEach { faces ->
                assertEquals(
                    recommendationsByCanonicalRoll.getValue(faces),
                    engine.recommend(
                        context = scenario.context,
                        roll = confirmedRoll(faces = faces, rollIndex = rollIndex),
                    ),
                    "${scenario.name}, $rollIndex, $faces must be deterministic",
                )
                representativePermutations(faces).forEach { permutedFaces ->
                    assertEquals(
                        recommendationsByCanonicalRoll.getValue(faces),
                        engine.recommend(
                            context = scenario.context,
                            roll = confirmedRoll(faces = permutedFaces, rollIndex = rollIndex),
                        ),
                        "${scenario.name}, $rollIndex, $faces -> $permutedFaces",
                    )
                }
            }

            orderedRolls.forEach { orderedFaces ->
                val expected = recommendationsByCanonicalRoll.getValue(orderedFaces.sorted())
                val recommendation = if (rollIndex == RollIndex.THREE) {
                    engine.recommend(
                        context = scenario.context,
                        roll = confirmedRoll(faces = orderedFaces, rollIndex = rollIndex),
                    ).also { actual ->
                        assertEquals(
                            expected,
                            actual,
                            "${scenario.name}, $rollIndex, $orderedFaces is order-dependent",
                        )
                    }
                } else {
                    expected
                }
                assertRecommendationInvariants(
                    scenario = scenario,
                    faces = orderedFaces,
                    rollIndex = rollIndex,
                    recommendation = recommendation,
                )
            }
        }
    }

    private fun assertRecommendationInvariants(
        scenario: StrategyScenario,
        faces: List<Int>,
        rollIndex: RollIndex,
        recommendation: DiceRecommendation,
    ) {
        assertTrue(
            recommendation.expectedValue.isFinite(),
            "${scenario.name}, $rollIndex, $faces has a non-finite value",
        )

        val openCells = scoreDiceForOpenCells(
            dice = faces,
            context = scenario.context.toScoreContext(),
        )

        when (rollIndex) {
            RollIndex.ONE,
            RollIndex.TWO -> assertKeepRecommendationIsLegal(
                scenario = scenario,
                faces = faces,
                rollIndex = rollIndex,
                recommendation = recommendation,
            )

            RollIndex.THREE -> assertThirdRollMatchesSharedScoring(
                scenario = scenario,
                faces = faces,
                recommendation = recommendation,
                openCells = openCells,
            )
        }
    }

    private fun assertKeepRecommendationIsLegal(
        scenario: StrategyScenario,
        faces: List<Int>,
        rollIndex: RollIndex,
        recommendation: DiceRecommendation,
    ) {
        val keep = assertIs<DiceRecommendation.KeepDice>(
            recommendation,
            "${scenario.name}, $rollIndex, $faces should recommend dice to keep",
        )
        assertEquals(keep.keepFaces.sorted(), keep.keepFaces)
        assertTrue(
            keep.keepFaces.isMultisetSubsetOf(faces),
            "${scenario.name}, $rollIndex, $faces cannot keep ${keep.keepFaces}",
        )
        assertEquals(5 - keep.keepFaces.size, keep.rerollCount)
        assertTrue(keep.rerollCount in 0..5)
    }

    private fun assertThirdRollMatchesSharedScoring(
        scenario: StrategyScenario,
        faces: List<Int>,
        recommendation: DiceRecommendation,
        openCells: List<DiceScoreCell>,
    ) {
        val score = assertIs<DiceRecommendation.ScoreCell>(
            recommendation,
            "${scenario.name}, third roll $faces should recommend an open score cell",
        )
        val sharedScore = openCells.firstOrNull {
            it.key == score.key && it.columnIndex == score.columnIndex
        }
        assertTrue(
            sharedScore != null,
            "${scenario.name}, third roll $faces targets a filled or disabled cell",
        )
        assertEquals(sharedScore.score, score.score)
        assertEquals(sharedScore.awardsExtraFiveOfAKindBonus, score.awardsExtraFiveOfAKindBonus)
        assertEquals(sharedScore.valueWithBonuses.toDouble(), score.expectedValue)
        assertEquals(
            openCells.maxOf(DiceScoreCell::valueWithBonuses).toDouble(),
            score.expectedValue,
            "${scenario.name}, third roll $faces does not maximize shared scoring",
        )
        assertCellIsOpen(
            scenario = scenario,
            key = score.key,
            columnIndex = score.columnIndex,
        )
    }

    private fun assertCellIsOpen(
        scenario: StrategyScenario,
        key: ScoreKey,
        columnIndex: Int,
    ) {
        assertTrue(columnIndex in 0 until scenario.context.settings.columnCount.coerceAtLeast(1))
        assertTrue(
            scenario.context.scoreEntries[key]?.getOrNull(columnIndex) == null,
            "${scenario.name} recommended filled cell $key in column $columnIndex",
        )
    }

    private fun List<Int>.isMultisetSubsetOf(other: List<Int>): Boolean {
        val available = other.groupingBy { it }.eachCount()
        return groupingBy { it }
            .eachCount()
            .all { (face, count) -> count <= available.getOrElse(face) { 0 } }
    }

    private fun representativePermutations(faces: List<Int>): Set<List<Int>> = setOf(
        faces.reversed(),
        faces.drop(2) + faces.take(2),
    )

    private fun DiceAssistantContext.toScoreContext(): DiceScoreContext =
        DiceScoreContext(
            settings = settings,
            scoreEntries = scoreEntries,
            extraFiveOfAKindScores = extraFiveOfAKindScores,
        )

    private fun confirmedRoll(
        faces: List<Int>,
        rollIndex: RollIndex,
    ): ConfirmedDiceRoll =
        ConfirmedDiceRoll(
            faces = faces,
            rollIndex = rollIndex,
        )

    private data class StrategyScenario(
        val name: String,
        val context: DiceAssistantContext,
    )

    private companion object {
        const val ORDERED_ROLL_COUNT = 7_776
        const val CANONICAL_ROLL_COUNT = 252

        val orderedRolls: List<List<Int>> = buildList {
            for (first in 1..6) {
                for (second in 1..6) {
                    for (third in 1..6) {
                        for (fourth in 1..6) {
                            for (fifth in 1..6) {
                                add(listOf(first, second, third, fourth, fifth))
                            }
                        }
                    }
                }
            }
        }

        val canonicalRolls: List<List<Int>> =
            orderedRolls.map(List<Int>::sorted).distinct()

        val determinismRolls: List<List<Int>> = listOf(
            listOf(1, 1, 1, 1, 1),
            listOf(1, 1, 1, 1, 2),
            listOf(1, 1, 1, 2, 2),
            listOf(1, 1, 1, 2, 3),
            listOf(1, 1, 2, 2, 3),
            listOf(1, 2, 3, 4, 5),
            listOf(2, 3, 4, 5, 6),
        )

        val standardScoreKeys: List<ScoreKey> = listOf(
            ScoreKey.ONES,
            ScoreKey.TWOS,
            ScoreKey.THREES,
            ScoreKey.FOURS,
            ScoreKey.FIVES,
            ScoreKey.SIXES,
            ScoreKey.THREE_OF_A_KIND,
            ScoreKey.FOUR_OF_A_KIND,
            ScoreKey.FULL_HOUSE,
            ScoreKey.SMALL_STRAIGHT,
            ScoreKey.LARGE_STRAIGHT,
            ScoreKey.FIVE_OF_A_KIND,
            ScoreKey.CHANCE,
        )

        val standardScenario: StrategyScenario = StrategyScenario(
            name = "standard Yams near game end",
            context = DiceAssistantContext(
                settings = GameSettings.YamsSettings(),
                scoreEntries = standardScoreKeys
                    .filterNot { it == ScoreKey.CHANCE }
                    .associateWith { listOf(0) },
            ),
        )

        val complexScenario: StrategyScenario = StrategyScenario(
            name = "two columns with bonuses, filled cells and custom rules",
            context = DiceAssistantContext(
                settings = GameSettings.YamsSettings(
                    columnCount = 2,
                    jokerRule = true,
                    customGameSettings = listOf(
                        GameSettings.CustomGameSettings(
                            title = "Fixed custom",
                            id = "fixed_custom",
                            scoring = GameSettings.SettingsScoring.FIXED_CUSTOM,
                            value = 42,
                            description = null,
                        ),
                        GameSettings.CustomGameSettings(
                            title = "Dice sum",
                            id = "dice_sum",
                            scoring = GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE,
                            value = null,
                            description = null,
                        ),
                    ),
                ),
                scoreEntries = mapOf(
                    ScoreKey.ONES to listOf(3, 0),
                    ScoreKey.TWOS to listOf(6, 0),
                    ScoreKey.THREES to listOf(9, 0),
                    ScoreKey.FOURS to listOf(12, 0),
                    ScoreKey.FIVES to listOf(15, 0),
                    ScoreKey.SIXES to listOf(null, 0),
                    ScoreKey.THREE_OF_A_KIND to listOf(0, 0),
                    ScoreKey.FOUR_OF_A_KIND to listOf(0, 0),
                    ScoreKey.FULL_HOUSE to listOf(0, 0),
                    ScoreKey.SMALL_STRAIGHT to listOf(0, 0),
                    ScoreKey.LARGE_STRAIGHT to listOf(0, 0),
                    ScoreKey.FIVE_OF_A_KIND to listOf(50, null),
                    ScoreKey.CHANCE to listOf(18, 22),
                    ScoreKey.custom("fixed_custom") to listOf(42, null),
                    ScoreKey.custom("dice_sum") to listOf(20, null),
                ),
            ),
        )
    }
}

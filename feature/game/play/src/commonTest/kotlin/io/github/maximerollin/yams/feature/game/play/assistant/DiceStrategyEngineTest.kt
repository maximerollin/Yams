package io.github.maximerollin.yams.feature.game.play.assistant

import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.core.model.ScoreKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DiceStrategyEngineTest {
    private val engine = DiceStrategyEngine()

    @Test
    fun filledCategoriesAreIgnored() {
        val recommendation = engine.recommend(
            context = context(
                scoreEntries = mapOf(
                    ScoreKey.FIVE_OF_A_KIND to listOf(50),
                ),
            ),
            roll = confirmedRoll(
                faces = listOf(6, 6, 6, 6, 6),
                rollIndex = RollIndex.THREE,
            ),
        )

        val score = assertIs<DiceRecommendation.ScoreCell>(recommendation)
        assertFalse(score.key == ScoreKey.FIVE_OF_A_KIND)
    }

    @Test
    fun thirdRollRecommendsScoreCell() {
        val recommendation = engine.recommend(
            context = context(),
            roll = confirmedRoll(
                faces = listOf(6, 6, 6, 6, 6),
                rollIndex = RollIndex.THREE,
            ),
        )

        val score = assertIs<DiceRecommendation.ScoreCell>(recommendation)
        assertEquals(ScoreKey.FIVE_OF_A_KIND, score.key)
        assertEquals(50, score.score)
    }

    @Test
    fun firstAndSecondRollRecommendDiceToKeep() {
        val recommendation = engine.recommend(
            context = context(),
            roll = confirmedRoll(
                faces = listOf(6, 6, 6, 2, 3),
                rollIndex = RollIndex.TWO,
            ),
        )

        val keep = assertIs<DiceRecommendation.KeepDice>(recommendation)
        assertEquals(listOf(6, 6, 6), keep.keepFaces)
        assertEquals(2, keep.rerollCount)
    }

    @Test
    fun upperBonusNearThresholdCanChangeBestScore() {
        val recommendation = engine.recommend(
            context = context(
                scoreEntries = mapOf(
                    ScoreKey.ONES to listOf(5),
                    ScoreKey.TWOS to listOf(10),
                    ScoreKey.THREES to listOf(15),
                    ScoreKey.FOURS to listOf(20),
                    ScoreKey.FIVES to listOf(10),
                ),
            ),
            roll = confirmedRoll(
                faces = listOf(6, 6, 6, 6, 6),
                rollIndex = RollIndex.THREE,
            ),
        )

        val score = assertIs<DiceRecommendation.ScoreCell>(recommendation)
        assertEquals(ScoreKey.SIXES, score.key)
        assertEquals(65.0, score.expectedValue)
    }

    @Test
    fun extraYamsBonusIsIncludedWhenFiveOfAKindWasAlreadyScored() {
        val recommendation = engine.recommend(
            context = context(
                scoreEntries = mapOf(
                    ScoreKey.FIVE_OF_A_KIND to listOf(50),
                ),
            ),
            roll = confirmedRoll(
                faces = listOf(6, 6, 6, 6, 6),
                rollIndex = RollIndex.THREE,
            ),
        )

        val score = assertIs<DiceRecommendation.ScoreCell>(recommendation)
        assertTrue(score.awardsExtraFiveOfAKindBonus)
        assertEquals(130.0, score.expectedValue)
    }

    @Test
    fun multipleColumnsCanRecommendOpenCellInSecondColumn() {
        val settings = GameSettings.YamsSettings(columnCount = 2)
        val columnZeroFilled = allStandardScoreKeys.associateWith { listOf(0, null) }

        val recommendation = engine.recommend(
            context = context(
                settings = settings,
                scoreEntries = columnZeroFilled,
            ),
            roll = confirmedRoll(
                faces = listOf(6, 6, 6, 6, 6),
                rollIndex = RollIndex.THREE,
            ),
        )

        val score = assertIs<DiceRecommendation.ScoreCell>(recommendation)
        assertEquals(1, score.columnIndex)
    }

    @Test
    fun customRulesAreIgnoredWhenTheirConditionIsNotMachineReadable() {
        val customKey = ScoreKey.custom("prime")
        val recommendation = engine.recommend(
            context = context(
                settings = GameSettings.YamsSettings(
                    customGameSettings = listOf(
                        GameSettings.CustomGameSettings(
                            title = "Prime",
                            id = "prime",
                            scoring = GameSettings.SettingsScoring.FIXED_CUSTOM,
                            value = 42,
                            description = null,
                        ),
                    ),
                ),
            ),
            roll = confirmedRoll(
                faces = listOf(1, 2, 3, 4, 5),
                rollIndex = RollIndex.THREE,
            ),
        )

        val score = assertIs<DiceRecommendation.ScoreCell>(recommendation)
        assertFalse(score.key == customKey)
    }

    private fun context(
        settings: GameSettings = GameSettings.YamsSettings(),
        scoreEntries: Map<ScoreKey, List<Int?>> = emptyMap(),
    ): DiceAssistantContext =
        DiceAssistantContext(
            settings = settings,
            scoreEntries = scoreEntries,
        )

    private fun confirmedRoll(
        faces: List<Int>,
        rollIndex: RollIndex,
    ): ConfirmedDiceRoll =
        ConfirmedDiceRoll(
            faces = faces,
            rollIndex = rollIndex,
        )

    private companion object {
        val allStandardScoreKeys = listOf(
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
    }
}

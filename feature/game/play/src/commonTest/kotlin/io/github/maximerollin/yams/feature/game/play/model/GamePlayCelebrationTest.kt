package io.github.maximerollin.yams.feature.game.play.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class GamePlayCelebrationTest {
    @Test
    fun zeroValueYamsDoesNotCelebrate() {
        assertNull(celebrationTypeFor(score = 0, isYams = true))
    }

    @Test
    fun scoreBelowThresholdDoesNotCelebrate() {
        assertNull(celebrationTypeFor(score = 29, isYams = false))
    }

    @Test
    fun thresholdScoreCreatesBigScoreCelebration() {
        assertEquals(
            GamePlayCelebrationType.BIG_SCORE,
            celebrationTypeFor(score = 30, isYams = false),
        )
    }

    @Test
    fun yamsTakesPrecedenceOverBigScore() {
        assertEquals(
            GamePlayCelebrationType.YAMS,
            celebrationTypeFor(score = 50, isYams = true),
        )
    }

    @Test
    fun eachTypeUsesItsApprovedTimeout() {
        assertEquals(2_200L, GamePlayCelebrationType.YAMS.timeoutMillis)
        assertEquals(1_600L, GamePlayCelebrationType.BIG_SCORE.timeoutMillis)
    }

    @Test
    fun matchingCompletionClearsCurrentCelebration() {
        val celebration = celebration(id = 7)

        assertNull(celebration.clearIfCompleted(completedId = 7))
    }

    @Test
    fun staleCompletionKeepsNewerCelebration() {
        val celebration = celebration(id = 8)

        assertSame(celebration, celebration.clearIfCompleted(completedId = 7))
    }

    private fun celebration(id: Int) = GamePlayCelebration(
        id = id,
        type = GamePlayCelebrationType.YAMS,
        playerName = "Lina",
        score = 50,
    )
}

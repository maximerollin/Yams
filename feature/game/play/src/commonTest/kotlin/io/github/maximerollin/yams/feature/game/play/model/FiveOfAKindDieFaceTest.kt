package io.github.maximerollin.yams.feature.game.play.model

import io.github.maximerollin.yams.core.model.ScoreKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FiveOfAKindDieFaceTest {
    @Test
    fun upperRowFiveOfAKindReturnsItsFace() {
        assertEquals(5, fiveOfAKindDieFace(ScoreKey.FIVES, score = 25))
        assertEquals(6, fiveOfAKindDieFace(ScoreKey.SIXES, score = 30))
        assertEquals(1, fiveOfAKindDieFace(ScoreKey.ONES, score = 5))
    }

    @Test
    fun upperRowWithNonMatchingScoreReturnsNull() {
        assertNull(fiveOfAKindDieFace(ScoreKey.FIVES, score = 20))
    }

    @Test
    fun dedicatedYamsCellReturnsNull() {
        assertNull(fiveOfAKindDieFace(ScoreKey.FIVE_OF_A_KIND, score = 50))
    }
}

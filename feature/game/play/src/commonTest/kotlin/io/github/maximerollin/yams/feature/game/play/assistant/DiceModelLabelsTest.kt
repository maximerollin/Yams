package io.github.maximerollin.yams.feature.game.play.assistant

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DiceModelLabelsTest {
    @Test
    fun mapsSupportedModelLabelsToFaces() {
        assertEquals(1, DiceModelLabels.faceFromLabel("1"))
        assertEquals(2, DiceModelLabels.faceFromLabel("die_2"))
        assertEquals(3, DiceModelLabels.faceFromLabel("dice-3"))
        assertEquals(4, DiceModelLabels.faceFromLabel("face 4"))
        assertEquals(5, DiceModelLabels.faceFromLabel("five"))
        assertEquals(6, DiceModelLabels.faceFromLabel("d6"))
    }

    @Test
    fun ignoresUnknownLabels() {
        assertNull(DiceModelLabels.faceFromLabel("dice"))
        assertNull(DiceModelLabels.faceFromLabel("background"))
        assertNull(DiceModelLabels.faceFromLabel(null))
    }
}

package io.github.maximerollin.yams.feature.game.play.assistant

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DiceDetectionStabilizerTest {
    @Test
    fun returnsFirstCompleteDetectionWhenOneFrameIsRequired() {
        val stabilizer = DiceDetectionStabilizer(requiredConsistentFrames = 1)
        val detection = frame()

        assertEquals(detection, stabilizer.accept(detection))
    }

    @Test
    fun returnsBestFrameAfterThreeConsistentDetections() {
        val stabilizer = DiceDetectionStabilizer()
        val first = frame(confidence = 0.70f)
        val best = frame(confidence = 0.94f, offset = 0.01f)
        val third = frame(confidence = 0.82f, offset = 0.02f)

        assertNull(stabilizer.accept(first))
        assertNull(stabilizer.accept(best))
        assertEquals(best, stabilizer.accept(third))
    }

    @Test
    fun incompleteDetectionResetsConsistencySequence() {
        val stabilizer = DiceDetectionStabilizer()

        assertNull(stabilizer.accept(frame()))
        assertNull(stabilizer.accept(frame().take(4)))
        assertNull(stabilizer.accept(frame()))
        assertNull(stabilizer.accept(frame(offset = 0.01f)))
        val best = frame(confidence = 0.95f, offset = 0.02f)
        assertEquals(best, stabilizer.accept(best))
    }

    @Test
    fun changedFaceRestartsConsistencySequence() {
        val stabilizer = DiceDetectionStabilizer()
        val changedFaces = frame().mapIndexed { index, die ->
            if (index == 0) die.copy(face = 6) else die
        }

        assertNull(stabilizer.accept(frame()))
        assertNull(stabilizer.accept(changedFaces))
        assertNull(stabilizer.accept(changedFaces))
        assertEquals(changedFaces, stabilizer.accept(changedFaces))
    }

    @Test
    fun movedDiceRestartsConsistencySequence() {
        val stabilizer = DiceDetectionStabilizer(maximumCenterDistance = 0.05f)
        val movedFrame = frame(offset = 0.10f)

        assertNull(stabilizer.accept(frame()))
        assertNull(stabilizer.accept(movedFrame))
        assertNull(stabilizer.accept(movedFrame))
        assertEquals(movedFrame, stabilizer.accept(movedFrame))
    }

    @Test
    fun faceAgreementIsEnoughWhenBoundsAreUnavailable() {
        val stabilizer = DiceDetectionStabilizer(requiredConsistentFrames = 2)
        val first = frame().map { it.copy(bounds = null) }
        val second = first.reversed().map { it.copy(confidence = 0.95f) }

        assertNull(stabilizer.accept(first))
        assertEquals(second, stabilizer.accept(second))
    }

    private fun frame(
        confidence: Float = 0.85f,
        offset: Float = 0f,
    ): List<DetectedDie> = (1..5).map { index ->
        DetectedDie(
            face = index,
            confidence = confidence,
            bounds = DiceBounds(
                left = 0.10f * index + offset,
                top = 0.10f * index + offset,
                width = 0.08f,
                height = 0.08f,
            ),
        )
    }
}

package io.github.maximerollin.yams.feature.game.play.assistant

import io.github.maximerollin.yams.core.model.GameSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DiceAssistantWorkflowTest {
    @Test
    fun permissionDeniedCreatesPermissionState() {
        assertIs<DiceAssistantUiState.PermissionDenied>(
            DiceAssistantWorkflow.permissionDenied(),
        )
    }

    @Test
    fun incompleteDetectionRequiresCorrection() {
        val state = DiceAssistantWorkflow.stateForDetections(
            rollIndex = RollIndex.ONE,
            detections = listOf(
                DetectedDie(face = 1, confidence = 0.9f),
                DetectedDie(face = 2, confidence = 0.9f),
                DetectedDie(face = 3, confidence = 0.9f),
            ),
        )

        val correction = assertIs<DiceAssistantUiState.Correction>(state)
        assertEquals(CorrectionReason.DETECTION_COUNT, correction.reason)
        assertEquals(5, correction.faces.size)
    }

    @Test
    fun lowConfidenceDetectionRequiresCorrection() {
        val state = DiceAssistantWorkflow.stateForDetections(
            rollIndex = RollIndex.TWO,
            detections = listOf(
                DetectedDie(face = 1, confidence = 0.9f),
                DetectedDie(face = 2, confidence = 0.9f),
                DetectedDie(face = 3, confidence = 0.6f),
                DetectedDie(face = 4, confidence = 0.9f),
                DetectedDie(face = 5, confidence = 0.9f),
            ),
        )

        val correction = assertIs<DiceAssistantUiState.Correction>(state)
        assertEquals(CorrectionReason.LOW_CONFIDENCE, correction.reason)
    }

    @Test
    fun exactlyFiveValidFacesAreRequiredBeforeRecommendation() {
        val invalidState = DiceAssistantWorkflow.confirm(
            rollIndex = RollIndex.THREE,
            faces = listOf(1, 2, 3, 4),
            context = DiceAssistantContext(
                settings = GameSettings.YamsSettings(),
                scoreEntries = emptyMap(),
            ),
        )
        assertIs<DiceAssistantUiState.Correction>(invalidState)

        val validState = DiceAssistantWorkflow.confirm(
            rollIndex = RollIndex.THREE,
            faces = listOf(1, 2, 3, 4, 5),
            context = DiceAssistantContext(
                settings = GameSettings.YamsSettings(),
                scoreEntries = emptyMap(),
            ),
        )

        assertIs<DiceAssistantUiState.Recommendation>(validState)
    }

    @Test
    fun correctionFacesKeepDetectedValuesAndPadToFiveDice() {
        val faces = DiceAssistantWorkflow.correctionFacesFrom(
            listOf(
                DetectedDie(face = 6, confidence = 0.9f),
                DetectedDie(face = 5, confidence = 0.9f),
            ),
        )

        assertEquals(listOf(6, 5, 1, 1, 1), faces)
        assertTrue(DiceAssistantWorkflow.canConfirm(faces))
    }
}

package io.github.maximerollin.yams.feature.game.play.assistant

internal object DiceAssistantWorkflow {
    private const val ConfidenceThreshold = 0.76f

    fun stateForDetections(
        rollIndex: RollIndex,
        detections: List<DetectedDie>,
    ): DiceAssistantUiState {
        return if (detections.size == 5 && detections.all { it.confidence >= ConfidenceThreshold }) {
            DiceAssistantUiState.Correction(
                rollIndex = rollIndex,
                detectedDice = detections,
                faces = detections.map(DetectedDie::face),
                reason = CorrectionReason.USER_REQUEST,
            )
        } else {
            DiceAssistantUiState.Correction(
                rollIndex = rollIndex,
                detectedDice = detections,
                faces = correctionFacesFrom(detections),
                reason = if (detections.size == 5) {
                    CorrectionReason.LOW_CONFIDENCE
                } else {
                    CorrectionReason.DETECTION_COUNT
                },
            )
        }
    }

    fun correctionFacesFrom(detections: List<DetectedDie>): List<Int> =
        (detections.map(DetectedDie::face).take(5) + List(5) { 1 }).take(5)

    fun canConfirm(faces: List<Int>): Boolean =
        faces.size == 5 && faces.all { it in 1..6 }

    fun confirm(
        rollIndex: RollIndex,
        faces: List<Int>,
        context: DiceAssistantContext,
        strategyEngine: DiceStrategyEngine = DiceStrategyEngine(),
    ): DiceAssistantUiState {
        if (!canConfirm(faces)) {
            return DiceAssistantUiState.Correction(
                rollIndex = rollIndex,
                detectedDice = emptyList(),
                faces = correctionFacesFrom(faces.map { DetectedDie(face = it.coerceIn(1, 6), confidence = 1f) }),
                reason = CorrectionReason.DETECTION_COUNT,
            )
        }

        val roll = ConfirmedDiceRoll(
            faces = faces,
            rollIndex = rollIndex,
        )
        return DiceAssistantUiState.Recommendation(
            roll = roll,
            recommendation = strategyEngine.recommend(
                context = context,
                roll = roll,
            ),
        )
    }

    fun permissionDenied(): DiceAssistantUiState =
        DiceAssistantUiState.PermissionDenied
}

package io.github.maximerollin.yams.feature.game.play.assistant

import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.core.model.ScoreKey

internal data class DiceAssistantContext(
    val settings: GameSettings,
    val scoreEntries: Map<ScoreKey, List<Int?>>,
    val extraFiveOfAKindScores: List<Int> = emptyList(),
)

internal data class DetectedDie(
    val face: Int,
    val confidence: Float,
    val bounds: DiceBounds? = null,
) {
    init {
        require(face in 1..6) { "Detected die face must be between 1 and 6" }
        require(confidence in 0f..1f) { "Detected die confidence must be between 0 and 1" }
    }
}

internal data class DiceBounds(
    val left: Float,
    val top: Float,
    val width: Float,
    val height: Float,
)

internal data class ConfirmedDiceRoll(
    val faces: List<Int>,
    val rollIndex: RollIndex,
) {
    init {
        require(faces.size == 5) { "A confirmed roll must contain exactly 5 dice" }
        require(faces.all { it in 1..6 }) { "Confirmed die faces must be between 1 and 6" }
    }
}

internal enum class RollIndex(
    val remainingRerolls: Int,
) {
    ONE(remainingRerolls = 2),
    TWO(remainingRerolls = 1),
    THREE(remainingRerolls = 0);

    fun next(): RollIndex = when (this) {
        ONE -> TWO
        TWO -> THREE
        THREE -> THREE
    }
}

internal sealed interface DiceRecommendation {
    val expectedValue: Double

    data class KeepDice(
        val keepFaces: List<Int>,
        val rerollCount: Int,
        override val expectedValue: Double,
    ) : DiceRecommendation

    data class ScoreCell(
        val key: ScoreKey,
        val columnIndex: Int,
        val score: Int,
        val awardsExtraFiveOfAKindBonus: Boolean,
        override val expectedValue: Double,
    ) : DiceRecommendation

    data class NoAvailableMove(
        override val expectedValue: Double = 0.0,
    ) : DiceRecommendation
}

internal sealed interface DiceAssistantUiState {
    data class Scanning(
        val rollIndex: RollIndex,
        val detectedDice: List<DetectedDie> = emptyList(),
    ) : DiceAssistantUiState

    data class Correction(
        val rollIndex: RollIndex,
        val detectedDice: List<DetectedDie>,
        val faces: List<Int>,
        val reason: CorrectionReason,
    ) : DiceAssistantUiState

    data class Calculating(
        val roll: ConfirmedDiceRoll,
    ) : DiceAssistantUiState

    data class Recommendation(
        val roll: ConfirmedDiceRoll,
        val recommendation: DiceRecommendation,
    ) : DiceAssistantUiState

    data object PermissionDenied : DiceAssistantUiState

    data object Error : DiceAssistantUiState
}

internal enum class CorrectionReason {
    LOW_CONFIDENCE,
    DETECTION_COUNT,
    USER_REQUEST,
}

internal interface DiceRecognitionEngine {
    val isAvailable: Boolean
    val unavailableMessage: String?
}

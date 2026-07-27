package io.github.maximerollin.yams.feature.game.play.assistant

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

internal interface DiceCameraPreview {
    @Composable
    fun Content(
        modifier: Modifier,
        onDetections: (List<DetectedDie>) -> Unit,
        onPermissionDenied: () -> Unit,
        onCameraError: (String) -> Unit,
    )
}

@Composable
internal expect fun rememberDiceRecognitionEngine(): DiceRecognitionEngine

@Composable
internal expect fun rememberDiceCameraPreview(
    recognitionEngine: DiceRecognitionEngine,
): DiceCameraPreview

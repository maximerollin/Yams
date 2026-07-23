package io.github.maximerollin.yams.feature.game.play.assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
internal actual fun rememberDiceRecognitionEngine(): DiceRecognitionEngine =
    remember { JvmDiceRecognitionEngine() }

@Composable
internal actual fun rememberDiceCameraPreview(
    recognitionEngine: DiceRecognitionEngine,
): DiceCameraPreview =
    remember(recognitionEngine) { JvmDiceCameraPreview(recognitionEngine) }

private class JvmDiceRecognitionEngine : DiceRecognitionEngine {
    override val isAvailable: Boolean = false
    override val unavailableMessage: String =
        "Le modèle local de dés n'est disponible que sur mobile."
}

private class JvmDiceCameraPreview(
    private val recognitionEngine: DiceRecognitionEngine,
) : DiceCameraPreview {
    @Composable
    override fun Content(
        modifier: Modifier,
        onDetections: (List<DetectedDie>) -> Unit,
        onPermissionDenied: () -> Unit,
        onCameraError: (String) -> Unit,
    ) {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (recognitionEngine.isAvailable) {
                    "Scan local des dés"
                } else {
                    "Modèle local de dés non installé"
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

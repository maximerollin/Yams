package io.github.maximerollin.yams.feature.game.play.assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import platform.UIKit.UIView

/**
 * Objective-C compatible result returned by the native iOS camera and inference pipeline.
 */
public class IosDiceDetection(
    public val face: Int,
    public val confidence: Float,
    public val left: Float,
    public val top: Float,
    public val width: Float,
    public val height: Float,
)

/**
 * Implemented by the iOS app target so AVFoundation and TensorFlow Lite remain native to iOS.
 */
public interface IosDiceAssistantBridge {
    public val isModelAvailable: Boolean
    public val unavailableMessage: String?

    public fun makePreviewView(): UIView

    public fun start(
        onDetections: (List<IosDiceDetection>) -> Unit,
        onPermissionDenied: () -> Unit,
        onError: (String) -> Unit,
    )

    public fun stop()
}

private object IosDiceAssistantBridgeRegistry {
    var bridge: IosDiceAssistantBridge? = null
}

public fun installIosDiceAssistantBridge(bridge: IosDiceAssistantBridge?) {
    IosDiceAssistantBridgeRegistry.bridge?.stop()
    IosDiceAssistantBridgeRegistry.bridge = bridge
}

@Composable
internal actual fun rememberDiceRecognitionEngine(): DiceRecognitionEngine {
    val bridge = IosDiceAssistantBridgeRegistry.bridge
    return remember(bridge) { IosDiceRecognitionEngine(bridge) }
}

@Composable
internal actual fun rememberDiceCameraPreview(
    recognitionEngine: DiceRecognitionEngine,
): DiceCameraPreview =
    remember(recognitionEngine) { IosDiceCameraPreview(recognitionEngine) }

private class IosDiceRecognitionEngine(
    val bridge: IosDiceAssistantBridge?,
) : DiceRecognitionEngine {
    override val isAvailable: Boolean
        get() = bridge?.isModelAvailable == true

    override val unavailableMessage: String?
        get() = bridge?.unavailableMessage
            ?: "Le pipeline caméra iOS n'est pas installé."
}

private class IosDiceCameraPreview(
    private val recognitionEngine: DiceRecognitionEngine,
) : DiceCameraPreview {
    @Composable
    override fun Content(
        modifier: Modifier,
        onDetections: (List<DetectedDie>) -> Unit,
        onPermissionDenied: () -> Unit,
        onCameraError: (String) -> Unit,
    ) {
        val bridge = (recognitionEngine as? IosDiceRecognitionEngine)?.bridge
        if (bridge == null || !recognitionEngine.isAvailable) {
            CameraMessage(
                message = recognitionEngine.unavailableMessage
                    ?: "Modèle local de dés indisponible.",
                modifier = modifier,
            )
            return
        }

        DisposableEffect(bridge, onDetections, onPermissionDenied, onCameraError) {
            bridge.start(
                onDetections = { detections ->
                    onDetections(detections.mapNotNull(IosDiceDetection::toDetectedDieOrNull))
                },
                onPermissionDenied = onPermissionDenied,
                onError = onCameraError,
            )
            onDispose {
                bridge.stop()
            }
        }

        UIKitView(
            factory = bridge::makePreviewView,
            modifier = modifier,
            onRelease = {
                bridge.stop()
            },
        )
    }
}

private fun IosDiceDetection.toDetectedDieOrNull(): DetectedDie? {
    if (face !in 1..6 || !confidence.isFinite()) return null
    if (
        !left.isFinite() ||
        !top.isFinite() ||
        !width.isFinite() ||
        !height.isFinite()
    ) {
        return null
    }

    val normalizedLeft = left.coerceIn(0f, 1f)
    val normalizedTop = top.coerceIn(0f, 1f)
    val normalizedRight = (left + width).coerceIn(normalizedLeft, 1f)
    val normalizedBottom = (top + height).coerceIn(normalizedTop, 1f)
    if (normalizedRight <= normalizedLeft || normalizedBottom <= normalizedTop) return null

    return DetectedDie(
        face = face,
        confidence = confidence.coerceIn(0f, 1f),
        bounds = DiceBounds(
            left = normalizedLeft,
            top = normalizedTop,
            width = normalizedRight - normalizedLeft,
            height = normalizedBottom - normalizedTop,
        ),
    )
}

@Composable
private fun CameraMessage(
    message: String,
    modifier: Modifier,
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

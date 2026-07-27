package io.github.maximerollin.yams.feature.game.play.assistant

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.SystemClock
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

@Composable
internal actual fun rememberDiceRecognitionEngine(): DiceRecognitionEngine {
    val context = LocalContext.current.applicationContext
    val engine = remember(context) { AndroidYoloDiceRecognitionEngine(context) }
    DisposableEffect(engine) {
        onDispose {
            engine.close()
        }
    }
    return engine
}

@Composable
internal actual fun rememberDiceCameraPreview(
    recognitionEngine: DiceRecognitionEngine,
): DiceCameraPreview =
    remember(recognitionEngine) { AndroidDiceCameraPreview(recognitionEngine) }

private class AndroidDiceCameraPreview(
    private val recognitionEngine: DiceRecognitionEngine,
) : DiceCameraPreview {
    private companion object {
        const val AnalysisIntervalMillis = 350L
    }

    @Composable
    override fun Content(
        modifier: Modifier,
        onDetections: (List<DetectedDie>) -> Unit,
        onPermissionDenied: () -> Unit,
        onCameraError: (String) -> Unit,
    ) {
        val modelEngine = recognitionEngine as? AndroidCameraDiceRecognitionEngine
        if (modelEngine == null || !recognitionEngine.isAvailable) {
            CameraMessage(
                message = recognitionEngine.unavailableMessage ?: "Modèle local de dés indisponible.",
                modifier = modifier,
            )
            return
        }

        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
        var previewView by remember { mutableStateOf<PreviewView?>(null) }
        val lastAnalysisAt = remember { AtomicLong(0L) }
        var hasPermission by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA,
                ) == PackageManager.PERMISSION_GRANTED,
            )
        }
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { granted ->
            hasPermission = granted
            if (!granted) onPermissionDenied()
        }

        LaunchedEffect(Unit) {
            if (!hasPermission) {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        DisposableEffect(modelEngine, onDetections, onCameraError) {
            modelEngine.updateCallbacks(
                onDetections = onDetections,
                onError = onCameraError,
            )
            onDispose {}
        }

        DisposableEffect(modelEngine, analysisExecutor) {
            analysisExecutor.execute(modelEngine::prepare)
            onDispose {
                analysisExecutor.shutdown()
            }
        }

        if (!hasPermission) {
            CameraMessage(
                message = "Autorisation caméra en attente",
                modifier = modifier,
            )
            return
        }

        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = { factoryContext ->
                PreviewView(factoryContext).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    previewView = this
                }
            },
        )

        DisposableEffect(previewView, lifecycleOwner, modelEngine) {
            val view = previewView
            if (view == null) {
                onDispose {}
            } else {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                var cameraProvider: ProcessCameraProvider? = null

                cameraProviderFuture.addListener(
                    {
                        try {
                            cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder()
                                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                                .setTargetRotation(view.display.rotation)
                                .build()
                                .also { it.setSurfaceProvider(view.surfaceProvider) }
                            val imageAnalysis = buildImageAnalysis(
                                view = view,
                                modelEngine = modelEngine,
                                analysisExecutor = analysisExecutor,
                                lastAnalysisAt = lastAnalysisAt,
                            )

                            cameraProvider?.unbindAll()
                            cameraProvider?.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageAnalysis,
                            )
                        } catch (_: SecurityException) {
                            onPermissionDenied()
                        } catch (exception: Exception) {
                            Log.e("DiceYolo", "Unable to bind CameraX use cases.", exception)
                            onCameraError("Impossible d'ouvrir la caméra.")
                        }
                    },
                    ContextCompat.getMainExecutor(context),
                )

                onDispose {
                    cameraProvider?.unbindAll()
                }
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun buildImageAnalysis(
        view: PreviewView,
        modelEngine: AndroidCameraDiceRecognitionEngine,
        analysisExecutor: ExecutorService,
        lastAnalysisAt: AtomicLong,
    ): ImageAnalysis =
        ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(view.display.rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
            .also { analysis ->
                analysis.setAnalyzer(analysisExecutor) { imageProxy ->
                    val now = SystemClock.uptimeMillis()
                    if (now - lastAnalysisAt.get() < AnalysisIntervalMillis) {
                        imageProxy.close()
                        return@setAnalyzer
                    }
                    lastAnalysisAt.set(now)
                    modelEngine.recognize(imageProxy)
                }
            }
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

package io.github.maximerollin.yams.feature.game.play.assistant

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.camera.core.ImageProxy
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.Tensor
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.gpu.GpuDelegateFactory
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal interface AndroidCameraDiceRecognitionEngine : DiceRecognitionEngine {
    fun prepare()

    fun updateCallbacks(
        onDetections: (List<DetectedDie>) -> Unit,
        onError: (String) -> Unit,
    )

    fun recognize(imageProxy: ImageProxy)
    fun close()
}

internal class AndroidYoloDiceRecognitionEngine(
    private val context: Context,
) : AndroidCameraDiceRecognitionEngine {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val paint = Paint(Paint.FILTER_BITMAP_FLAG)
    private val rotationMatrix = Matrix()
    private val rotationBounds = RectF()
    private val modelLock = Any()
    private val analysisInProgress = AtomicBoolean(false)
    private var interpreter: Interpreter? = null
    private var gpuDelegate: GpuDelegate? = null
    private var accelerator = DiceAccelerator.CPU
    private var gpuBackend = NoGpuBackend
    private var inputSize = DiceModelConfig.InputSize
    private var inputBuffer: ByteBuffer? = null
    private var inputPixels = IntArray(0)
    private var cameraBitmap: Bitmap? = null
    private var rotatedBitmap: Bitmap? = null
    private var modelBitmap: Bitmap? = null
    private var outputValues: Array<Array<FloatArray>>? = null
    private var outputLayout: OutputLayout? = null
    private var outputAnchors: Int = 0
    private var isWarmedUp = false
    private var onDetections: (List<DetectedDie>) -> Unit = {}
    private var onError: (String) -> Unit = {}
    @Volatile
    private var isClosed = false

    override val isAvailable: Boolean
        get() = context.hasDiceModelAsset(DiceModelConfig.ModelAssetName)

    override val unavailableMessage: String?
        get() = if (isAvailable) {
            null
        } else {
            "Ajoutez ${DiceModelConfig.ModelAssetName} dans feature/game/play/src/androidMain/assets puis recompilez l'app."
        }

    override fun prepare() {
        if (isClosed || !isAvailable) return

        runCatching {
            synchronized(modelLock) {
                if (isClosed || isWarmedUp) return
                val model = interpreter ?: setupInterpreter() ?: return
                val warmupInput = zeroedInputBuffer()

                val preparedModel = runCatching {
                    model.run(warmupInput, requireNotNull(outputValues))
                    model
                }.getOrElse { exception ->
                    if (accelerator != DiceAccelerator.GPU) throw exception
                    Log.w(Tag, "GPU warmup failed; falling back to CPU.", exception)
                    closeActiveInterpreter()
                    val cpuModel = setupInterpreter(allowGpu = false) ?: throw exception
                    cpuModel.run(zeroedInputBuffer(), requireNotNull(outputValues))
                    cpuModel
                }
                isWarmedUp = true
                check(preparedModel === interpreter)
            }
        }.onFailure { exception ->
            Log.e(Tag, "Unable to warm up dice model.", exception)
            postError("Impossible de préparer le modèle local de dés.")
        }
    }

    override fun updateCallbacks(
        onDetections: (List<DetectedDie>) -> Unit,
        onError: (String) -> Unit,
    ) {
        this.onDetections = onDetections
        this.onError = onError
    }

    override fun recognize(imageProxy: ImageProxy) {
        if (isClosed || !analysisInProgress.compareAndSet(false, true)) {
            imageProxy.close()
            return
        }

        if (!isAvailable) {
            imageProxy.close()
            analysisInProgress.set(false)
            postError(unavailableMessage ?: "Modèle local de dés indisponible.")
            return
        }

        val model = synchronized(modelLock) {
            if (isClosed) null else interpreter ?: setupInterpreter()
        } ?: run {
            imageProxy.close()
            analysisInProgress.set(false)
            return
        }

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val bitmapBuffer = cameraBitmap(
            width = imageProxy.width,
            height = imageProxy.height,
        )

        try {
            imageProxy.planes[0].buffer.rewind()
            bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer)
        } catch (exception: RuntimeException) {
            Log.e(Tag, "CameraX frame is not RGBA_8888.", exception)
            analysisInProgress.set(false)
            postError("Flux caméra incompatible avec le modèle local.")
            return
        } finally {
            imageProxy.close()
        }

        runCatching {
            recognizeBitmap(
                interpreter = model,
                bitmap = bitmapBuffer,
                rotationDegrees = rotationDegrees,
            )
        }.onSuccess { dice ->
            mainHandler.post {
                onDetections(dice)
            }
        }.onFailure { exception ->
            val didFallBack = synchronized(modelLock) {
                if (!isClosed && accelerator == DiceAccelerator.GPU) {
                    Log.w(Tag, "GPU inference failed; falling back to CPU.", exception)
                    closeActiveInterpreter()
                    setupInterpreter(allowGpu = false) != null
                } else {
                    false
                }
            }
            if (!didFallBack) {
                Log.e(Tag, "YOLO detection failed.", exception)
                postError("Analyse du modèle de dés indisponible (${exception.javaClass.simpleName}).")
            }
        }.also {
            analysisInProgress.set(false)
        }
    }

    override fun close() {
        isClosed = true
        synchronized(modelLock) {
            closeActiveInterpreter()
            inputBuffer = null
            inputPixels = IntArray(0)
            outputValues = null
            outputLayout = null
            outputAnchors = 0
            isWarmedUp = false
        }
        cameraBitmap = null
        rotatedBitmap = null
        modelBitmap = null
    }

    private fun setupInterpreter(allowGpu: Boolean = true): Interpreter? {
        val modelBuffer = runCatching {
            context.loadModelBuffer(DiceModelConfig.ModelAssetName)
        }.onFailure { exception ->
            Log.e(Tag, "Unable to read dice model asset.", exception)
            postError("Impossible de charger le modèle local de dés.")
        }.getOrNull() ?: return null
        if (allowGpu && isGpuDelegateSupported()) {
            createGpuInterpreter(
                modelBuffer = modelBuffer,
                forceBackend = null,
                backendLabel = AutomaticGpuBackend,
            )?.let { return it }
            createGpuInterpreter(
                modelBuffer = modelBuffer,
                forceBackend = GpuDelegateFactory.Options.GpuBackend.OPENCL,
                backendLabel = OpenClGpuBackend,
            )?.let { return it }
            createGpuInterpreter(
                modelBuffer = modelBuffer,
                forceBackend = GpuDelegateFactory.Options.GpuBackend.OPENGL,
                backendLabel = OpenGlGpuBackend,
            )?.let { return it }
            Log.w(Tag, "GPU model initialization failed with AUTO, OPENCL and OPENGL; falling back to CPU.")
        }

        return createInterpreter(
            modelBuffer = modelBuffer,
            delegate = null,
            targetAccelerator = DiceAccelerator.CPU,
            backendLabel = NoGpuBackend,
        )
    }

    private fun isGpuDelegateSupported(): Boolean = runCatching {
        CompatibilityList().use { compatibilityList ->
            compatibilityList.isDelegateSupportedOnThisDevice.also { isSupported ->
                if (!isSupported) {
                    Log.i(Tag, "GPU delegate is not supported on this device; using CPU.")
                }
            }
        }
    }.onFailure { exception ->
        Log.w(Tag, "Unable to check GPU delegate support; using CPU.", exception)
    }.getOrDefault(false)

    private fun createGpuInterpreter(
        modelBuffer: ByteBuffer,
        forceBackend: GpuDelegateFactory.Options.GpuBackend?,
        backendLabel: String,
    ): Interpreter? {
        val delegate = runCatching {
            CompatibilityList().use { compatibilityList ->
                val options = compatibilityList.bestOptionsForThisDevice
                if (forceBackend != null) {
                    options.setForceBackend(forceBackend)
                }
                GpuDelegate(options)
            }
        }.onFailure { exception ->
            Log.w(
                Tag,
                "GPU backend=$backendLabel could not be created; trying fallback " +
                    "(${exception.javaClass.simpleName}: ${exception.message}).",
            )
        }.getOrNull() ?: return null

        return createInterpreter(
            modelBuffer = modelBuffer,
            delegate = delegate,
            targetAccelerator = DiceAccelerator.GPU,
            backendLabel = backendLabel,
        ).also { loadedInterpreter ->
            if (loadedInterpreter == null) {
                delegate.close()
            }
        }
    }

    private fun createInterpreter(
        modelBuffer: ByteBuffer,
        delegate: GpuDelegate?,
        targetAccelerator: DiceAccelerator,
        backendLabel: String,
    ): Interpreter? {
        val options = Interpreter.Options().apply {
            if (delegate != null) {
                addDelegate(delegate)
            } else {
                setNumThreads(CpuThreadCount)
            }
        }

        return runCatching {
            modelBuffer.rewind()
            val loadedInterpreter = Interpreter(modelBuffer, options)
            runCatching {
                inputSize = loadedInterpreter.requiredInputSize()
                prepareOutputBuffer(loadedInterpreter)
            }.onFailure {
                loadedInterpreter.close()
            }.getOrThrow()
            loadedInterpreter
        }.onSuccess { loadedInterpreter ->
            interpreter = loadedInterpreter
            gpuDelegate = delegate
            accelerator = targetAccelerator
            gpuBackend = backendLabel
            Log.i(
                Tag,
                "Loaded ${DiceModelConfig.ModelAssetName} accelerator=${accelerator.name} " +
                    "backend=$gpuBackend " +
                    "input=${loadedInterpreter.inputShape()} output=${loadedInterpreter.outputShape()} " +
                    "inputSize=$inputSize.",
            )
        }.onFailure { exception ->
            val message =
                "Unable to load YOLO model with ${targetAccelerator.name} backend=$backendLabel"
            if (targetAccelerator == DiceAccelerator.GPU) {
                Log.w(
                    Tag,
                    "$message; trying fallback " +
                        "(${exception.javaClass.simpleName}: ${exception.message}).",
                )
            } else {
                Log.e(Tag, message, exception)
                postError("Impossible de charger le modèle local de dés.")
            }
        }.getOrNull()
    }

    private fun closeActiveInterpreter() {
        interpreter?.close()
        interpreter = null
        gpuDelegate?.close()
        gpuDelegate = null
        accelerator = DiceAccelerator.CPU
        gpuBackend = NoGpuBackend
    }

    private fun recognizeBitmap(
        interpreter: Interpreter,
        bitmap: Bitmap,
        rotationDegrees: Int,
    ): List<DetectedDie> {
        val input = fillInputBuffer(bitmap, rotationDegrees)

        return synchronized(modelLock) {
            check(!isClosed && this.interpreter === interpreter) {
                "Dice model interpreter was closed before inference."
            }
            val output = requireNotNull(outputValues)
            val layout = requireNotNull(outputLayout)

            interpreter.run(input.buffer, output)

            when (layout) {
                OutputLayout.CHANNEL_FIRST ->
                    decodeChannelFirst(output[0], outputAnchors, input.transform)

                OutputLayout.CHANNEL_LAST ->
                    decodeChannelLast(output[0], outputAnchors, input.transform)
            }
        }
    }

    private fun fillInputBuffer(
        bitmap: Bitmap,
        rotationDegrees: Int,
    ): ModelInput {
        val rotatedBitmap = bitmap.rotate(rotationDegrees)
        val rotatedWidth = rotatedBitmap.width
        val rotatedHeight = rotatedBitmap.height
        val scale = min(
            inputSize.toFloat() / rotatedWidth,
            inputSize.toFloat() / rotatedHeight,
        )
        val scaledWidth = (rotatedWidth * scale).roundToInt().coerceAtLeast(1)
        val scaledHeight = (rotatedHeight * scale).roundToInt().coerceAtLeast(1)
        val padX = (inputSize - scaledWidth) / 2f
        val padY = (inputSize - scaledHeight) / 2f

        val modelBitmap = modelInputBitmap()
        val canvas = Canvas(modelBitmap)
        canvas.drawColor(LetterboxColor)
        canvas.drawBitmap(
            rotatedBitmap,
            null,
            RectF(padX, padY, padX + scaledWidth, padY + scaledHeight),
            paint,
        )

        if (inputPixels.size != inputSize * inputSize) {
            inputPixels = IntArray(inputSize * inputSize)
        }
        val buffer = inputBuffer()
        modelBitmap.getPixels(inputPixels, 0, inputSize, 0, 0, inputSize, inputSize)
        var pixelIndex = 0
        while (pixelIndex < inputPixels.size) {
            val pixel = inputPixels[pixelIndex]
            buffer.putFloat(Color.red(pixel) / 255f)
            buffer.putFloat(Color.green(pixel) / 255f)
            buffer.putFloat(Color.blue(pixel) / 255f)
            pixelIndex += 1
        }
        buffer.rewind()

        return ModelInput(
            buffer = buffer,
            transform = LetterboxTransform(
                scale = scale,
                padX = padX,
                padY = padY,
                imageWidth = rotatedWidth,
                imageHeight = rotatedHeight,
            ),
        )
    }

    private fun cameraBitmap(
        width: Int,
        height: Int,
    ): Bitmap {
        val existingBitmap = cameraBitmap
        if (
            existingBitmap != null &&
            existingBitmap.width == width &&
            existingBitmap.height == height &&
            !existingBitmap.isRecycled
        ) {
            return existingBitmap
        }

        existingBitmap?.recycle()
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            .also { cameraBitmap = it }
    }

    private fun modelInputBitmap(): Bitmap {
        val existingBitmap = modelBitmap
        if (
            existingBitmap != null &&
            existingBitmap.width == inputSize &&
            existingBitmap.height == inputSize &&
            !existingBitmap.isRecycled
        ) {
            return existingBitmap
        }

        existingBitmap?.recycle()
        return Bitmap.createBitmap(inputSize, inputSize, Bitmap.Config.ARGB_8888)
            .also { modelBitmap = it }
    }

    private fun decodeChannelFirst(
        output: Array<FloatArray>,
        anchors: Int,
        transform: LetterboxTransform,
    ): List<DetectedDie> {
        val candidates = buildList {
            repeat(anchors) { anchorIndex ->
                val classScore = bestClassScore { classIndex ->
                    output[BoxChannels + classIndex][anchorIndex]
                } ?: return@repeat
                if (classScore.confidence < DiceModelConfig.MinimumConfidence) return@repeat

                val candidate = output.toCandidate(
                    anchorIndex = anchorIndex,
                    face = classScore.face,
                    confidence = classScore.confidence,
                    transform = transform,
                )
                if (candidate != null) add(candidate)
            }
        }

        return candidates.nonMaxSuppressed()
    }

    private fun decodeChannelLast(
        output: Array<FloatArray>,
        anchors: Int,
        transform: LetterboxTransform,
    ): List<DetectedDie> {
        val candidates = buildList {
            repeat(anchors) { anchorIndex ->
                val row = output[anchorIndex]
                val classScore = bestClassScore { classIndex ->
                    row[BoxChannels + classIndex]
                } ?: return@repeat
                if (classScore.confidence < DiceModelConfig.MinimumConfidence) return@repeat

                val candidate = row.toCandidate(
                    face = classScore.face,
                    confidence = classScore.confidence,
                    transform = transform,
                )
                if (candidate != null) add(candidate)
            }
        }

        return candidates.nonMaxSuppressed()
    }

    private fun Array<FloatArray>.toCandidate(
        anchorIndex: Int,
        face: Int,
        confidence: Float,
        transform: LetterboxTransform,
    ): YoloCandidate? =
        boxToCandidate(
            centerX = this[0][anchorIndex],
            centerY = this[1][anchorIndex],
            width = this[2][anchorIndex],
            height = this[3][anchorIndex],
            face = face,
            confidence = confidence,
            transform = transform,
        )

    private fun FloatArray.toCandidate(
        face: Int,
        confidence: Float,
        transform: LetterboxTransform,
    ): YoloCandidate? =
        boxToCandidate(
            centerX = this[0],
            centerY = this[1],
            width = this[2],
            height = this[3],
            face = face,
            confidence = confidence,
            transform = transform,
        )

    private fun boxToCandidate(
        centerX: Float,
        centerY: Float,
        width: Float,
        height: Float,
        face: Int,
        confidence: Float,
        transform: LetterboxTransform,
    ): YoloCandidate? {
        val coordinatesAreNormalized = listOf(centerX, centerY, width, height).all { it in -2f..2f }
        val normalizedScale = if (coordinatesAreNormalized) inputSize.toFloat() else 1f
        val boxCenterX = centerX * normalizedScale
        val boxCenterY = centerY * normalizedScale
        val boxWidth = width * normalizedScale
        val boxHeight = height * normalizedScale

        val inputLeft = boxCenterX - boxWidth / 2f
        val inputTop = boxCenterY - boxHeight / 2f
        val inputRight = boxCenterX + boxWidth / 2f
        val inputBottom = boxCenterY + boxHeight / 2f

        val imageLeft = ((inputLeft - transform.padX) / transform.scale).coerceIn(0f, transform.imageWidth.toFloat())
        val imageTop = ((inputTop - transform.padY) / transform.scale).coerceIn(0f, transform.imageHeight.toFloat())
        val imageRight = ((inputRight - transform.padX) / transform.scale).coerceIn(0f, transform.imageWidth.toFloat())
        val imageBottom = ((inputBottom - transform.padY) / transform.scale).coerceIn(0f, transform.imageHeight.toFloat())

        if (imageRight <= imageLeft || imageBottom <= imageTop) return null

        return YoloCandidate(
            face = face,
            confidence = confidence,
            left = imageLeft,
            top = imageTop,
            right = imageRight,
            bottom = imageBottom,
            bounds = DiceBounds(
                left = imageLeft / transform.imageWidth,
                top = imageTop / transform.imageHeight,
                width = (imageRight - imageLeft) / transform.imageWidth,
                height = (imageBottom - imageTop) / transform.imageHeight,
            ),
        )
    }

    private fun bestClassScore(scoreAt: (Int) -> Float): ClassScore? {
        var bestClassIndex = -1
        var bestConfidence = 0f
        repeat(ClassCount) { classIndex ->
            val score = scoreAt(classIndex).asConfidence()
            if (score > bestConfidence) {
                bestClassIndex = classIndex
                bestConfidence = score
            }
        }

        return if (bestClassIndex >= 0) {
            ClassScore(face = bestClassIndex + 1, confidence = bestConfidence)
        } else {
            null
        }
    }

    private fun List<YoloCandidate>.nonMaxSuppressed(): List<DetectedDie> {
        val selected = mutableListOf<YoloCandidate>()
        sortedByDescending(YoloCandidate::confidence).forEach { candidate ->
            if (selected.size >= DiceModelConfig.MaxResults) {
                return@forEach
            }
            if (selected.any { it.iou(candidate) >= DiceModelConfig.IouThreshold }) {
                return@forEach
            }
            selected += candidate
        }

        return selected
            .sortedWith(compareBy<YoloCandidate> { it.top }.thenBy { it.left })
            .map { candidate ->
                DetectedDie(
                    face = candidate.face,
                    confidence = candidate.confidence.coerceIn(0f, 1f),
                    bounds = candidate.bounds,
                )
            }
    }

    private fun YoloCandidate.iou(other: YoloCandidate): Float {
        val intersectionLeft = max(left, other.left)
        val intersectionTop = max(top, other.top)
        val intersectionRight = min(right, other.right)
        val intersectionBottom = min(bottom, other.bottom)
        val intersectionWidth = max(0f, intersectionRight - intersectionLeft)
        val intersectionHeight = max(0f, intersectionBottom - intersectionTop)
        val intersectionArea = intersectionWidth * intersectionHeight
        val unionArea = area + other.area - intersectionArea
        return if (unionArea > 0f) intersectionArea / unionArea else 0f
    }

    private fun Float.asConfidence(): Float =
        if (this in 0f..1f) this else (1f / (1f + exp(-this))).coerceIn(0f, 1f)

    private fun Bitmap.rotate(rotationDegrees: Int): Bitmap {
        val normalizedRotation = ((rotationDegrees % FullRotation) + FullRotation) % FullRotation
        if (normalizedRotation == 0) return this

        rotationMatrix.reset()
        rotationMatrix.setRotate(normalizedRotation.toFloat())
        rotationBounds.set(0f, 0f, width.toFloat(), height.toFloat())
        rotationMatrix.mapRect(rotationBounds)
        rotationMatrix.postTranslate(-rotationBounds.left, -rotationBounds.top)

        val rotatedWidth = rotationBounds.width().roundToInt().coerceAtLeast(1)
        val rotatedHeight = rotationBounds.height().roundToInt().coerceAtLeast(1)
        val reusableBitmap = rotatedBitmap(
            width = rotatedWidth,
            height = rotatedHeight,
        )
        Canvas(reusableBitmap).apply {
            drawColor(Color.BLACK)
            drawBitmap(this@rotate, rotationMatrix, paint)
        }
        return reusableBitmap
    }

    private fun rotatedBitmap(
        width: Int,
        height: Int,
    ): Bitmap {
        val existingBitmap = rotatedBitmap
        if (
            existingBitmap != null &&
            existingBitmap.width == width &&
            existingBitmap.height == height &&
            !existingBitmap.isRecycled
        ) {
            return existingBitmap
        }

        existingBitmap?.recycle()
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            .also { rotatedBitmap = it }
    }

    private fun postError(message: String) {
        if (isClosed) return
        mainHandler.post {
            onError(message)
        }
    }

    private fun Interpreter.inputShape(): String =
        getInputTensor(0).describe()

    private fun Interpreter.outputShape(): String =
        getOutputTensor(0).describe()

    private fun Tensor.describe(): String =
        shape().joinToString(prefix = "[", postfix = "]")

    private fun Interpreter.requiredInputSize(): Int {
        val shape = getInputTensor(0).shape()
        return when {
            shape.size == 4 && shape[3] == RgbChannels -> shape[1]
            shape.size == 4 && shape[1] == RgbChannels -> shape[2]
            else -> DiceModelConfig.InputSize
        }
    }

    private fun prepareOutputBuffer(interpreter: Interpreter) {
        val shape = interpreter.getOutputTensor(0).shape()
        require(shape.size == 3 && shape[0] == 1) {
            "Unsupported YOLO output shape: ${shape.joinToString(prefix = "[", postfix = "]")}"
        }

        outputLayout = when {
            shape[1] == OutputChannels -> OutputLayout.CHANNEL_FIRST
            shape[2] == OutputChannels -> OutputLayout.CHANNEL_LAST
            else -> error(
                "Unsupported YOLO output shape: ${shape.joinToString(prefix = "[", postfix = "]")}",
            )
        }
        outputAnchors = when (outputLayout) {
            OutputLayout.CHANNEL_FIRST -> shape[2]
            OutputLayout.CHANNEL_LAST -> shape[1]
            null -> error("YOLO output layout was not resolved")
        }
        outputValues = Array(shape[0]) {
            Array(shape[1]) {
                FloatArray(shape[2])
            }
        }
    }

    private fun inputBuffer(): ByteBuffer {
        val byteCount = inputSize * inputSize * RgbChannels * FloatBytes
        val existingBuffer = inputBuffer
        if (existingBuffer != null && existingBuffer.capacity() == byteCount) {
            existingBuffer.rewind()
            return existingBuffer
        }

        return ByteBuffer
            .allocateDirect(byteCount)
            .order(ByteOrder.nativeOrder())
            .also { inputBuffer = it }
    }

    private fun zeroedInputBuffer(): ByteBuffer {
        val buffer = inputBuffer()
        buffer.clear()
        repeat(buffer.capacity() / FloatBytes) {
            buffer.putFloat(0f)
        }
        buffer.rewind()
        return buffer
    }

    private companion object {
        const val Tag = "DiceYolo"
        const val CpuThreadCount = 4
        const val NoGpuBackend = "NONE"
        const val AutomaticGpuBackend = "AUTO"
        const val OpenClGpuBackend = "OPENCL"
        const val OpenGlGpuBackend = "OPENGL"
        const val ClassCount = 6
        const val BoxChannels = 4
        const val OutputChannels = BoxChannels + ClassCount
        const val RgbChannels = 3
        const val FloatBytes = 4
        const val FullRotation = 360
        val LetterboxColor: Int = Color.rgb(114, 114, 114)
    }
}

private data class ModelInput(
    val buffer: ByteBuffer,
    val transform: LetterboxTransform,
)

private enum class DiceAccelerator {
    CPU,
    GPU,
}

private enum class OutputLayout {
    CHANNEL_FIRST,
    CHANNEL_LAST,
}

private fun Context.hasDiceModelAsset(assetName: String): Boolean =
    runCatching {
        assets.open(assetName).use { true }
    }.getOrElse { exception ->
        if (exception !is IOException) {
            Log.e("DiceYolo", "Unable to check model asset.", exception)
        }
        false
    }

private fun Context.loadModelBuffer(assetName: String): ByteBuffer {
    val bytes = assets.open(assetName).use { input ->
        input.readBytes()
    }
    return ByteBuffer
        .allocateDirect(bytes.size)
        .order(ByteOrder.nativeOrder())
        .apply {
            put(bytes)
            rewind()
        }
}

private data class LetterboxTransform(
    val scale: Float,
    val padX: Float,
    val padY: Float,
    val imageWidth: Int,
    val imageHeight: Int,
)

private data class ClassScore(
    val face: Int,
    val confidence: Float,
)

private data class YoloCandidate(
    val face: Int,
    val confidence: Float,
    val bounds: DiceBounds,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    val area: Float = (right - left) * (bottom - top)
}

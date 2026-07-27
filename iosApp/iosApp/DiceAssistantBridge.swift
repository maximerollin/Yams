import AVFoundation
import CoreImage
import Foundation
import TensorFlowLite
import UIKit
import YamsApp

final class DiceAssistantBridge: NSObject, PlayIosDiceAssistantBridge {
    private let captureSession = AVCaptureSession()
    private let sessionQueue = DispatchQueue(
        label: "io.github.maximerollin.yams.dice-camera"
    )
    private let inferenceQueue = DispatchQueue(
        label: "io.github.maximerollin.yams.dice-inference",
        qos: .userInitiated
    )
    private let stateLock = NSLock()
    private let modelURL = Bundle.main.url(
        forResource: DiceYoloDetector.modelName,
        withExtension: DiceYoloDetector.modelExtension
    )

    private weak var previewView: DiceCameraPreviewView?
    private var isSessionConfigured = false
    private var callbackGeneration = 0
    private var onDetections: (([PlayIosDiceDetection]) -> Void)?
    private var onPermissionDenied: (() -> Void)?
    private var onError: ((String) -> Void)?
    private var detector: DiceYoloDetector?
    private var detectorInitializationError: Error?

    var isModelAvailable: Bool {
        modelURL != nil
    }

    var unavailableMessage: String? {
        guard modelURL == nil else { return nil }
        return "Le modèle local dice_yolo_v8n_960_float32.tflite est absent de l'application."
    }

    func makePreviewView() -> UIView {
        let view = DiceCameraPreviewView()
        view.previewLayer.session = captureSession
        view.previewLayer.videoGravity = .resizeAspectFill
        previewView = view
        updatePreviewOrientation()
        return view
    }

    func start(
        onDetections: @escaping ([PlayIosDiceDetection]) -> Void,
        onPermissionDenied: @escaping () -> Void,
        onError: @escaping (String) -> Void
    ) {
        stateLock.withLock {
            callbackGeneration += 1
            self.onDetections = onDetections
            self.onPermissionDenied = onPermissionDenied
            self.onError = onError
        }

        guard isModelAvailable else {
            deliverError(unavailableMessage ?? "Modèle local de dés indisponible.")
            return
        }

        switch AVCaptureDevice.authorizationStatus(for: .video) {
        case .authorized:
            startCaptureSession()
        case .notDetermined:
            AVCaptureDevice.requestAccess(for: .video) { [weak self] granted in
                guard let self else { return }
                if granted {
                    self.startCaptureSession()
                } else {
                    self.deliverPermissionDenied()
                }
            }
        case .denied, .restricted:
            deliverPermissionDenied()
        @unknown default:
            deliverError("État d'autorisation caméra inconnu.")
        }
    }

    func stop() {
        stateLock.withLock {
            callbackGeneration += 1
            onDetections = nil
            onPermissionDenied = nil
            onError = nil
        }
        sessionQueue.async { [weak self] in
            guard let self, self.captureSession.isRunning else { return }
            self.captureSession.stopRunning()
        }
    }

    private func startCaptureSession() {
        sessionQueue.async { [weak self] in
            guard let self else { return }
            do {
                try self.configureSessionIfNeeded()
                guard self.hasActiveCallbacks(), !self.captureSession.isRunning else { return }
                self.captureSession.startRunning()
            } catch {
                self.deliverError("Impossible d'ouvrir la caméra : \(error.localizedDescription)")
            }
        }
    }

    private func configureSessionIfNeeded() throws {
        guard !isSessionConfigured else { return }

        captureSession.beginConfiguration()
        defer { captureSession.commitConfiguration() }

        if captureSession.canSetSessionPreset(.hd1920x1080) {
            captureSession.sessionPreset = .hd1920x1080
        } else {
            captureSession.sessionPreset = .high
        }

        guard let camera = AVCaptureDevice.default(
            .builtInWideAngleCamera,
            for: .video,
            position: .back
        ) else {
            throw DiceCameraError.backCameraUnavailable
        }

        let input = try AVCaptureDeviceInput(device: camera)
        guard captureSession.canAddInput(input) else {
            throw DiceCameraError.cameraInputUnavailable
        }
        captureSession.addInput(input)

        let output = AVCaptureVideoDataOutput()
        output.alwaysDiscardsLateVideoFrames = true
        output.videoSettings = [
            kCVPixelBufferPixelFormatTypeKey as String:
                kCVPixelFormatType_32BGRA
        ]
        output.setSampleBufferDelegate(self, queue: inferenceQueue)
        guard captureSession.canAddOutput(output) else {
            throw DiceCameraError.cameraOutputUnavailable
        }
        captureSession.addOutput(output)
        configurePortraitOrientation(output.connection(with: .video))

        isSessionConfigured = true
        DispatchQueue.main.async { [weak self] in
            self?.updatePreviewOrientation()
        }
    }

    private func configurePortraitOrientation(_ connection: AVCaptureConnection?) {
        guard let connection else { return }
        if #available(iOS 17.0, *), connection.isVideoRotationAngleSupported(90) {
            connection.videoRotationAngle = 90
        } else if connection.isVideoOrientationSupported {
            connection.videoOrientation = .portrait
        }
    }

    private func updatePreviewOrientation() {
        configurePortraitOrientation(previewView?.previewLayer.connection)
    }

    private func detectorInstance() throws -> DiceYoloDetector {
        if let detector {
            return detector
        }
        if let detectorInitializationError {
            throw detectorInitializationError
        }
        guard let modelURL else {
            throw DiceYoloError.modelMissing
        }

        do {
            let detector = try DiceYoloDetector(modelPath: modelURL.path)
            self.detector = detector
            return detector
        } catch {
            detectorInitializationError = error
            throw error
        }
    }

    private func hasActiveCallbacks() -> Bool {
        stateLock.withLock { onDetections != nil }
    }

    private func deliver(
        detections: [PlayIosDiceDetection],
        generation: Int
    ) {
        DispatchQueue.main.async { [weak self] in
            guard let self else { return }
            let callback = self.stateLock.withLock {
                self.callbackGeneration == generation ? self.onDetections : nil
            }
            callback?(detections)
        }
    }

    private func deliverPermissionDenied() {
        DispatchQueue.main.async { [weak self] in
            guard let self else { return }
            let callback = self.stateLock.withLock { self.onPermissionDenied }
            callback?()
        }
    }

    private func deliverError(_ message: String) {
        DispatchQueue.main.async { [weak self] in
            guard let self else { return }
            let callback = self.stateLock.withLock { self.onError }
            callback?(message)
        }
    }
}

extension DiceAssistantBridge: AVCaptureVideoDataOutputSampleBufferDelegate {
    func captureOutput(
        _ output: AVCaptureOutput,
        didOutput sampleBuffer: CMSampleBuffer,
        from connection: AVCaptureConnection
    ) {
        guard let pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer) else {
            return
        }
        let generation = stateLock.withLock { callbackGeneration }
        guard hasActiveCallbacks() else { return }

        do {
            let detections = try detectorInstance().detect(pixelBuffer: pixelBuffer)
            deliver(detections: detections, generation: generation)
        } catch {
            deliverError("Analyse du modèle de dés impossible : \(error.localizedDescription)")
        }
    }
}

private final class DiceCameraPreviewView: UIView {
    override class var layerClass: AnyClass {
        AVCaptureVideoPreviewLayer.self
    }

    var previewLayer: AVCaptureVideoPreviewLayer {
        layer as! AVCaptureVideoPreviewLayer
    }
}

private final class DiceYoloDetector {
    static let modelName = "dice_yolo_v8n_960_float32"
    static let modelExtension = "tflite"

    private static let inputSize = 960
    private static let classCount = 6
    private static let boxChannels = 4
    private static let outputChannels = boxChannels + classCount
    private static let minimumConfidence: Float = 0.25
    private static let iouThreshold: Float = 0.45
    private static let maximumResults = 5
    private static let letterboxComponent: CGFloat = 114.0 / 255.0

    private let interpreter: Interpreter
    private let context = CIContext(options: [.cacheIntermediates: false])
    private let colorSpace = CGColorSpaceCreateDeviceRGB()
    private var modelPixelBuffer: CVPixelBuffer
    private var inputData = Data(
        count: inputSize * inputSize * 3 * MemoryLayout<Float>.size
    )

    init(modelPath: String) throws {
        var options = Interpreter.Options()
        options.threadCount = min(ProcessInfo.processInfo.activeProcessorCount, 4)
        options.isXNNPackEnabled = true
        interpreter = try Interpreter(modelPath: modelPath, options: options)
        try interpreter.allocateTensors()

        let input = try interpreter.input(at: 0)
        guard input.dataType == .float32,
              input.shape.dimensions == [1, Self.inputSize, Self.inputSize, 3] else {
            throw DiceYoloError.unsupportedInput(input.shape.dimensions)
        }

        let output = try interpreter.output(at: 0)
        guard output.dataType == .float32,
              output.shape.dimensions.count == 3,
              output.shape.dimensions[0] == 1,
              output.shape.dimensions.contains(Self.outputChannels) else {
            throw DiceYoloError.unsupportedOutput(output.shape.dimensions)
        }

        var buffer: CVPixelBuffer?
        let attributes: [CFString: Any] = [
            kCVPixelBufferCGImageCompatibilityKey: true,
            kCVPixelBufferCGBitmapContextCompatibilityKey: true,
            kCVPixelBufferMetalCompatibilityKey: true,
        ]
        let status = CVPixelBufferCreate(
            kCFAllocatorDefault,
            Self.inputSize,
            Self.inputSize,
            kCVPixelFormatType_32BGRA,
            attributes as CFDictionary,
            &buffer
        )
        guard status == kCVReturnSuccess, let buffer else {
            throw DiceYoloError.inputBufferCreationFailed(status)
        }
        modelPixelBuffer = buffer
    }

    func detect(pixelBuffer: CVPixelBuffer) throws -> [PlayIosDiceDetection] {
        let transform = renderLetterboxed(pixelBuffer)
        try fillInputData()
        try interpreter.copy(inputData, toInputAt: 0)
        try interpreter.invoke()
        let output = try interpreter.output(at: 0)
        return try decode(output: output, transform: transform)
    }

    private func renderLetterboxed(_ sourceBuffer: CVPixelBuffer) -> LetterboxTransform {
        let sourceWidth = CGFloat(CVPixelBufferGetWidth(sourceBuffer))
        let sourceHeight = CGFloat(CVPixelBufferGetHeight(sourceBuffer))
        let modelSize = CGFloat(Self.inputSize)
        let scale = min(modelSize / sourceWidth, modelSize / sourceHeight)
        let scaledWidth = sourceWidth * scale
        let scaledHeight = sourceHeight * scale
        let padX = (modelSize - scaledWidth) / 2
        let padY = (modelSize - scaledHeight) / 2
        let targetBounds = CGRect(x: 0, y: 0, width: modelSize, height: modelSize)

        let source = CIImage(cvPixelBuffer: sourceBuffer)
            .transformed(by: CGAffineTransform(scaleX: scale, y: scale))
            .transformed(by: CGAffineTransform(translationX: padX, y: padY))
        let background = CIImage(
            color: CIColor(
                red: Self.letterboxComponent,
                green: Self.letterboxComponent,
                blue: Self.letterboxComponent
            )
        ).cropped(to: targetBounds)
        context.render(
            source.composited(over: background),
            to: modelPixelBuffer,
            bounds: targetBounds,
            colorSpace: colorSpace
        )

        return LetterboxTransform(
            scale: Float(scale),
            padX: Float(padX),
            padY: Float(padY),
            imageWidth: Float(sourceWidth),
            imageHeight: Float(sourceHeight)
        )
    }

    private func fillInputData() throws {
        CVPixelBufferLockBaseAddress(modelPixelBuffer, .readOnly)
        defer { CVPixelBufferUnlockBaseAddress(modelPixelBuffer, .readOnly) }

        guard let baseAddress = CVPixelBufferGetBaseAddress(modelPixelBuffer) else {
            throw DiceYoloError.inputBufferUnavailable
        }
        let rowBytes = CVPixelBufferGetBytesPerRow(modelPixelBuffer)
        let source = baseAddress.assumingMemoryBound(to: UInt8.self)

        inputData.withUnsafeMutableBytes { rawOutput in
            let output = rawOutput.bindMemory(to: Float.self)
            var outputIndex = 0
            for y in 0..<Self.inputSize {
                let row = source.advanced(by: y * rowBytes)
                for x in 0..<Self.inputSize {
                    let pixel = row.advanced(by: x * 4)
                    output[outputIndex] = Float(pixel[2]) / 255
                    output[outputIndex + 1] = Float(pixel[1]) / 255
                    output[outputIndex + 2] = Float(pixel[0]) / 255
                    outputIndex += 3
                }
            }
        }
    }

    private func decode(
        output: Tensor,
        transform: LetterboxTransform
    ) throws -> [PlayIosDiceDetection] {
        let shape = output.shape.dimensions
        let channelFirst = shape[1] == Self.outputChannels
        let anchors = channelFirst ? shape[2] : shape[1]
        guard (channelFirst || shape[2] == Self.outputChannels),
              output.data.count == shape.reduce(1, *) * MemoryLayout<Float>.size else {
            throw DiceYoloError.unsupportedOutput(shape)
        }

        return output.data.withUnsafeBytes { rawOutput in
            let values = rawOutput.bindMemory(to: Float.self)
            var candidates: [YoloCandidate] = []
            candidates.reserveCapacity(64)

            for anchor in 0..<anchors {
                var bestClass = -1
                var bestConfidence: Float = 0
                for classIndex in 0..<Self.classCount {
                    let channel = Self.boxChannels + classIndex
                    let rawScore = channelFirst
                        ? values[channel * anchors + anchor]
                        : values[anchor * Self.outputChannels + channel]
                    let confidence = Self.confidence(rawScore)
                    if confidence > bestConfidence {
                        bestClass = classIndex
                        bestConfidence = confidence
                    }
                }
                guard bestClass >= 0, bestConfidence >= Self.minimumConfidence else {
                    continue
                }

                let value: (Int) -> Float = { channel in
                    channelFirst
                        ? values[channel * anchors + anchor]
                        : values[anchor * Self.outputChannels + channel]
                }
                if let candidate = Self.makeCandidate(
                    centerX: value(0),
                    centerY: value(1),
                    width: value(2),
                    height: value(3),
                    face: bestClass + 1,
                    confidence: bestConfidence,
                    transform: transform
                ) {
                    candidates.append(candidate)
                }
            }

            return Self.nonMaxSuppressed(candidates)
        }
    }

    private static func makeCandidate(
        centerX: Float,
        centerY: Float,
        width: Float,
        height: Float,
        face: Int,
        confidence: Float,
        transform: LetterboxTransform
    ) -> YoloCandidate? {
        let coordinates = [centerX, centerY, width, height]
        let normalized = coordinates.allSatisfy { (-2...2).contains($0) }
        let coordinateScale = normalized ? Float(inputSize) : 1
        let boxCenterX = centerX * coordinateScale
        let boxCenterY = centerY * coordinateScale
        let boxWidth = width * coordinateScale
        let boxHeight = height * coordinateScale

        let inputLeft = boxCenterX - boxWidth / 2
        let inputTop = boxCenterY - boxHeight / 2
        let inputRight = boxCenterX + boxWidth / 2
        let inputBottom = boxCenterY + boxHeight / 2
        let imageLeft = ((inputLeft - transform.padX) / transform.scale)
            .clamped(to: 0...transform.imageWidth)
        let imageTop = ((inputTop - transform.padY) / transform.scale)
            .clamped(to: 0...transform.imageHeight)
        let imageRight = ((inputRight - transform.padX) / transform.scale)
            .clamped(to: 0...transform.imageWidth)
        let imageBottom = ((inputBottom - transform.padY) / transform.scale)
            .clamped(to: 0...transform.imageHeight)
        guard imageRight > imageLeft, imageBottom > imageTop else { return nil }

        return YoloCandidate(
            face: face,
            confidence: confidence,
            left: imageLeft,
            top: imageTop,
            right: imageRight,
            bottom: imageBottom,
            bounds: PlayIosDiceDetection(
                face: Int32(face),
                confidence: confidence.clamped(to: 0...1),
                left: imageLeft / transform.imageWidth,
                top: imageTop / transform.imageHeight,
                width: (imageRight - imageLeft) / transform.imageWidth,
                height: (imageBottom - imageTop) / transform.imageHeight
            )
        )
    }

    private static func nonMaxSuppressed(
        _ candidates: [YoloCandidate]
    ) -> [PlayIosDiceDetection] {
        var selected: [YoloCandidate] = []
        for candidate in candidates.sorted(by: { $0.confidence > $1.confidence }) {
            guard selected.count < maximumResults else { break }
            guard !selected.contains(where: { $0.iou(with: candidate) >= iouThreshold }) else {
                continue
            }
            selected.append(candidate)
        }
        return selected
            .sorted {
                $0.top == $1.top ? $0.left < $1.left : $0.top < $1.top
            }
            .map(\.bounds)
    }

    private static func confidence(_ value: Float) -> Float {
        guard !(0...1).contains(value) else { return value }
        return (1 / (1 + exp(-value))).clamped(to: 0...1)
    }
}

private struct LetterboxTransform {
    let scale: Float
    let padX: Float
    let padY: Float
    let imageWidth: Float
    let imageHeight: Float
}

private struct YoloCandidate {
    let face: Int
    let confidence: Float
    let left: Float
    let top: Float
    let right: Float
    let bottom: Float
    let bounds: PlayIosDiceDetection

    private var area: Float {
        (right - left) * (bottom - top)
    }

    func iou(with other: YoloCandidate) -> Float {
        let intersectionLeft = max(left, other.left)
        let intersectionTop = max(top, other.top)
        let intersectionRight = min(right, other.right)
        let intersectionBottom = min(bottom, other.bottom)
        let intersectionWidth = max(0, intersectionRight - intersectionLeft)
        let intersectionHeight = max(0, intersectionBottom - intersectionTop)
        let intersectionArea = intersectionWidth * intersectionHeight
        let unionArea = area + other.area - intersectionArea
        return unionArea > 0 ? intersectionArea / unionArea : 0
    }
}

private enum DiceCameraError: LocalizedError {
    case backCameraUnavailable
    case cameraInputUnavailable
    case cameraOutputUnavailable

    var errorDescription: String? {
        switch self {
        case .backCameraUnavailable:
            "Caméra arrière indisponible."
        case .cameraInputUnavailable:
            "Entrée caméra indisponible."
        case .cameraOutputUnavailable:
            "Flux d'analyse caméra indisponible."
        }
    }
}

private enum DiceYoloError: LocalizedError {
    case modelMissing
    case unsupportedInput([Int])
    case unsupportedOutput([Int])
    case inputBufferCreationFailed(CVReturn)
    case inputBufferUnavailable

    var errorDescription: String? {
        switch self {
        case .modelMissing:
            "Modèle TFLite absent."
        case .unsupportedInput(let shape):
            "Entrée YOLO non supportée : \(shape)."
        case .unsupportedOutput(let shape):
            "Sortie YOLO non supportée : \(shape)."
        case .inputBufferCreationFailed(let status):
            "Création du buffer d'entrée impossible (\(status))."
        case .inputBufferUnavailable:
            "Buffer d'entrée inaccessible."
        }
    }
}

private extension NSLock {
    func withLock<T>(_ action: () -> T) -> T {
        lock()
        defer { unlock() }
        return action()
    }
}

private extension Comparable {
    func clamped(to range: ClosedRange<Self>) -> Self {
        min(max(self, range.lowerBound), range.upperBound)
    }
}

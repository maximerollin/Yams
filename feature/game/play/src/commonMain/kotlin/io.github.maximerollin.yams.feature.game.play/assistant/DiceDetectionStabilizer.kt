package io.github.maximerollin.yams.feature.game.play.assistant

internal class DiceDetectionStabilizer(
    private val requiredConsistentFrames: Int = 3,
    private val maximumCenterDistance: Float = 0.12f,
) {
    private val consistentFrames = mutableListOf<List<DetectedDie>>()

    init {
        require(requiredConsistentFrames > 0)
        require(maximumCenterDistance > 0f)
    }

    fun accept(detections: List<DetectedDie>): List<DetectedDie>? {
        if (detections.size != DiceModelConfig.MaxResults) {
            reset()
            return null
        }

        val baseline = consistentFrames.firstOrNull()
        if (baseline != null && !baseline.isConsistentWith(detections)) {
            reset()
        }
        consistentFrames += detections

        if (consistentFrames.size < requiredConsistentFrames) return null

        return consistentFrames
            .maxBy { frame -> frame.map(DetectedDie::confidence).average() }
            .also { reset() }
    }

    fun reset() {
        consistentFrames.clear()
    }

    private fun List<DetectedDie>.isConsistentWith(other: List<DetectedDie>): Boolean {
        if (map(DetectedDie::face).sorted() != other.map(DetectedDie::face).sorted()) {
            return false
        }
        if (any { it.bounds == null } || other.any { it.bounds == null }) {
            return true
        }

        val unmatched = other.toMutableList()
        for (baselineDie in this) {
            val closestIndex = unmatched.indices
                .filter { unmatched[it].face == baselineDie.face }
                .minByOrNull { baselineDie.centerDistanceSquared(unmatched[it]) }
                ?: return false
            val centerDistance = baselineDie.centerDistanceSquared(unmatched[closestIndex])
            if (centerDistance > maximumCenterDistance * maximumCenterDistance) {
                return false
            }
            unmatched.removeAt(closestIndex)
        }
        return true
    }
}

private fun DetectedDie.centerDistanceSquared(other: DetectedDie): Float {
    val firstBounds = requireNotNull(bounds)
    val otherBounds = requireNotNull(other.bounds)
    val horizontalDistance = firstBounds.centerX - otherBounds.centerX
    val verticalDistance = firstBounds.centerY - otherBounds.centerY
    return horizontalDistance * horizontalDistance + verticalDistance * verticalDistance
}

private val DiceBounds.centerX: Float
    get() = left + width / 2f

private val DiceBounds.centerY: Float
    get() = top + height / 2f

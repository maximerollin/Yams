package io.github.maximerollin.yams.feature.game.play.assistant

internal object DiceModelConfig {
    const val ModelAssetName: String = "dice_yolo_v8n_960_float32.tflite"
    const val InputSize: Int = 960
    const val MinimumConfidence: Float = 0.25f
    const val IouThreshold: Float = 0.45f
    const val MaxResults: Int = 5
}

internal object DiceModelLabels {
    fun faceFromLabel(label: String?): Int? {
        val normalized = label
            ?.lowercase()
            ?.trim()
            ?.replace(Regex("[^a-z0-9]+"), "_")
            ?.trim('_')
            ?: return null

        return FaceNames.entries.firstOrNull { faceName ->
            normalized in faceName.aliases
        }?.face
    }

    private enum class FaceNames(
        val face: Int,
        val aliases: Set<String>,
    ) {
        ONE(1, setOf("1", "one", "die_1", "dice_1", "face_1", "d1")),
        TWO(2, setOf("2", "two", "die_2", "dice_2", "face_2", "d2")),
        THREE(3, setOf("3", "three", "die_3", "dice_3", "face_3", "d3")),
        FOUR(4, setOf("4", "four", "die_4", "dice_4", "face_4", "d4")),
        FIVE(5, setOf("5", "five", "die_5", "dice_5", "face_5", "d5")),
        SIX(6, setOf("6", "six", "die_6", "dice_6", "face_6", "d6")),
    }
}

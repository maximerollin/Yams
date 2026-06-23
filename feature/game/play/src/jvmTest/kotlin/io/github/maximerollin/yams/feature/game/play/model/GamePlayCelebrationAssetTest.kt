package io.github.maximerollin.yams.feature.game.play.model

import io.github.alexzhirkevich.compottie.LottieComposition
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class GamePlayCelebrationAssetTest {
    private fun parse(name: String): LottieComposition =
        LottieComposition.parse(
            File("src/commonMain/composeResources/files/$name").readText(),
        )

    @Test
    fun allYamsFaceVariantsParseWith80Frames() {
        for (face in 1..6) {
            val composition = parse("celebration_yams_$face.json")
            assertEquals(80f, composition.endFrame, "face $face endFrame")
        }
    }

    @Test
    fun bigScoreAssetParsesWith54Frames() {
        val composition = parse("celebration_big_score.json")
        assertEquals(54f, composition.endFrame)
    }
}

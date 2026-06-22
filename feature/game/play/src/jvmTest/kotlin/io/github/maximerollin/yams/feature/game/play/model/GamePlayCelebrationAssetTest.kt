package io.github.maximerollin.yams.feature.game.play.model

import io.github.alexzhirkevich.compottie.LottieComposition
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class GamePlayCelebrationAssetTest {
    @Test
    fun bundledYamsAssetParsesWithCompottie() {
        val composition = LottieComposition.parse(
            File("src/commonMain/composeResources/files/celebration_yams.json").readText(),
        )

        assertEquals(108f, composition.endFrame)
    }

    @Test
    fun bundledBigScoreAssetParsesWithCompottie() {
        val composition = LottieComposition.parse(
            File("src/commonMain/composeResources/files/celebration_big_score.json").readText(),
        )

        assertEquals(72f, composition.endFrame)
    }
}

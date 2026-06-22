package io.github.maximerollin.yams.feature.game.play.model

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Guards the contract the celebration overlay relies on: the progress passed to
 * [rememberLottiePainter] must be read from a snapshot State *inside* the lambda.
 *
 * compottie wraps the progress lambda in a derivedStateOf, which only recomputes when a
 * snapshot State is read during its calculation. If the overlay captures the progress value
 * in the composition scope and passes a constant `{ value }`, the painter freezes on the
 * first frame (progress ~0 = blank) and the animation never plays - while static
 * `previewProgress` screenshots still look correct. See [GamePlayCelebrationOverlay].
 */
class GamePlayCelebrationLottieTest {
    private fun render(progressInsideLambda: Boolean): Boolean {
        val json = File("src/commonMain/composeResources/files/celebration_yams.json").readText()
        var progress by mutableStateOf(0f)
        val scene = ImageComposeScene(width = 540, height = 540, density = Density(1f)) {
            val composition = rememberLottieComposition {
                LottieCompositionSpec.JsonString(json)
            }.value
            val captured = progress
            Image(
                painter = rememberLottiePainter(
                    composition = composition,
                    progress = if (progressInsideLambda) {
                        { progress }
                    } else {
                        { captured }
                    },
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
        }
        try {
            var t = 0L
            repeat(80) { scene.render(t); Thread.sleep(4); t += 16_666_666L }
            val atStart = scene.render(t).encodeToData()!!.bytes
            progress = 0.5f
            repeat(4) { scene.render(t); Thread.sleep(4); t += 16_666_666L }
            val atMid = scene.render(t).encodeToData()!!.bytes
            return !atStart.contentEquals(atMid)
        } finally {
            scene.close()
        }
    }

    @Test
    fun painterRedrawsWhenProgressStateIsReadInsideLambda() {
        assertTrue(
            render(progressInsideLambda = true),
            "Lottie painter must redraw as progress advances when the State is read inside the lambda",
        )
    }

    @Test
    fun capturingProgressOutsideLambdaFreezesPainter() {
        // Documents the failure mode the overlay must avoid.
        assertTrue(
            !render(progressInsideLambda = false),
            "Capturing progress in the composition scope is expected to freeze the painter",
        )
    }
}

package io.github.maximerollin.yams.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.vinceglb.confettikit.compose.ConfettiKit
import io.github.vinceglb.confettikit.core.Angle
import io.github.vinceglb.confettikit.core.Party
import io.github.vinceglb.confettikit.core.Position
import io.github.vinceglb.confettikit.core.Spread
import io.github.vinceglb.confettikit.core.emitter.Emitter
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
public fun YamsCelebrationConfetti(
    modifier: Modifier = Modifier,
    onFinished: () -> Unit = {},
) {
    var isVisible by remember { mutableStateOf(true) }
    val parties = remember { yamsCelebrationConfettiParties() }

    if (!isVisible) return

    ConfettiKit(
        modifier = modifier,
        parties = parties,
        onParticleSystemEnded = { _, activeSystems ->
            if (activeSystems == 0) {
                isVisible = false
                onFinished()
            }
        },
    )
}

private fun yamsCelebrationConfettiParties(): List<Party> {
    val colors = listOf(0xd4af37, 0x50b788, 0x4d96ff, 0xff6b6b, 0xffc857)
    val rain = Party(
        speed = 0f,
        maxSpeed = 16f,
        damping = 0.92f,
        angle = Angle.BOTTOM,
        spread = Spread.ROUND,
        colors = colors,
        emitter = Emitter(duration = 3.seconds).perSecond(85),
        position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0)),
    )

    return listOf(
        Party(
            speed = 0f,
            maxSpeed = 32f,
            damping = 0.9f,
            spread = Spread.ROUND,
            colors = colors,
            emitter = Emitter(duration = 160.milliseconds).max(130),
            position = Position.Relative(0.5, 0.25),
        ),
        rain.copy(delay = 180),
    )
}

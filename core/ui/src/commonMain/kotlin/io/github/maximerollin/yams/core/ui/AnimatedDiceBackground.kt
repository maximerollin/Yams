package io.github.maximerollin.yams.core.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Animated dice background with floating dice moving in circular patterns
 */
@Composable
public fun AnimatedDiceBackground(
    diceColor: Color,
    modifier: Modifier = Modifier,
    diceCount: Int = 15,
    isAnimationEnabled: Boolean = true,
    randomSeed: Int? = null,
) {
    val circlesCount = 3
    val baseDuration = 50_000L

    // Remember random configurations for each dice path
    val diceConfigs = remember(diceCount, randomSeed) {
        val random = randomSeed?.let { Random(it) } ?: Random
        List(diceCount) {
            DiceConfig(
                circleIndex = random.nextInt(circlesCount),
                startAngle = random.nextFloat() * 360f,
                diceValue = random.nextInt(1, 7),
                rotationSpeed = random.nextFloat() * 2f - 1f, // -1 to 1
                sizeDp = 20f + random.nextFloat() * 15f,
                duration = baseDuration + random.nextLong(-20_000, 40_000)
            )
        }
    }

    // Create animations for each circle path
    val circleAnimations = if (isAnimationEnabled) {
        (0 until circlesCount).map { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "circle_$index")
            val angle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = (baseDuration + index * 15_000).toInt(),
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "angle_$index"
            )
            angle
        }
    } else {
        List(circlesCount) { 0f }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val baseRadius = size.width * 0.3f
        val radiusIncrement = size.width * 0.15f
        
        diceConfigs.forEach { config ->
            val circleAngle = circleAnimations[config.circleIndex]
            val radius = baseRadius + radiusIncrement * config.circleIndex
            val totalAngle = circleAngle + config.startAngle
            
            val position = getPositionOnCircle(center, radius, totalAngle)
            
            drawDice(
                position = position,
                diceValue = config.diceValue,
                size = config.sizeDp * density,
                color = diceColor.copy(alpha = 0.08f),
                rotation = circleAngle * config.rotationSpeed
            )
        }
    }
}

private data class DiceConfig(
    val circleIndex: Int,
    val startAngle: Float,
    val diceValue: Int,
    val rotationSpeed: Float,
    val sizeDp: Float,
    val duration: Long
)

private fun DrawScope.drawDice(
    position: Offset,
    diceValue: Int,
    size: Float,
    color: Color,
    rotation: Float
) {
    translate(left = position.x, top = position.y) {
        rotate(degrees = rotation) {
            // Draw dice background (rounded square)
            drawRoundRect(
                color = color,
                topLeft = Offset(-size / 2, -size / 2),
                size = Size(size, size),
                cornerRadius = CornerRadius(size * 0.15f)
            )
            
            // Draw dots based on dice value
            val dotRadius = size * 0.08f
            val dotColor = color.copy(alpha = 0.5f)
            val quarter = size * 0.25f
            
            when (diceValue) {
                1 -> {
                    drawCircle(dotColor, dotRadius, Offset(0f, 0f))
                }
                2 -> {
                    drawCircle(dotColor, dotRadius, Offset(-quarter, -quarter))
                    drawCircle(dotColor, dotRadius, Offset(quarter, quarter))
                }
                3 -> {
                    drawCircle(dotColor, dotRadius, Offset(-quarter, -quarter))
                    drawCircle(dotColor, dotRadius, Offset(0f, 0f))
                    drawCircle(dotColor, dotRadius, Offset(quarter, quarter))
                }
                4 -> {
                    drawCircle(dotColor, dotRadius, Offset(-quarter, -quarter))
                    drawCircle(dotColor, dotRadius, Offset(quarter, -quarter))
                    drawCircle(dotColor, dotRadius, Offset(-quarter, quarter))
                    drawCircle(dotColor, dotRadius, Offset(quarter, quarter))
                }
                5 -> {
                    drawCircle(dotColor, dotRadius, Offset(-quarter, -quarter))
                    drawCircle(dotColor, dotRadius, Offset(quarter, -quarter))
                    drawCircle(dotColor, dotRadius, Offset(0f, 0f))
                    drawCircle(dotColor, dotRadius, Offset(-quarter, quarter))
                    drawCircle(dotColor, dotRadius, Offset(quarter, quarter))
                }
                6 -> {
                    drawCircle(dotColor, dotRadius, Offset(-quarter, -quarter))
                    drawCircle(dotColor, dotRadius, Offset(quarter, -quarter))
                    drawCircle(dotColor, dotRadius, Offset(-quarter, 0f))
                    drawCircle(dotColor, dotRadius, Offset(quarter, 0f))
                    drawCircle(dotColor, dotRadius, Offset(-quarter, quarter))
                    drawCircle(dotColor, dotRadius, Offset(quarter, quarter))
                }
            }
        }
    }
}

private fun getPositionOnCircle(center: Offset, radius: Float, angle: Float): Offset {
    val angleInRad = angle * (PI / 180).toFloat()
    val x = center.x + radius * cos(angleInRad)
    val y = center.y + radius * sin(angleInRad)
    return Offset(x, y)
}

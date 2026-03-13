package io.github.maximerollin.yams.feature.game.play.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.feature.game.play.ScoreSelectionOption

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ScoreSelectionMenu(
    expanded: Boolean,
    options: List<ScoreSelectionOption>,
    onDismissRequest: () -> Unit,
    onScoreSelected: (ScoreSelectionOption) -> Unit,
) {
    val scrollState = rememberScrollState()

    MaterialExpressiveTheme(
        colorScheme = MaterialTheme.colorScheme,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
    ) {
        val scrollbarColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
        val scrollbarTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.45f)

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = Modifier
                .heightIn(max = 320.dp)
                .drawWithContent {
                    drawContent()

                    if (scrollState.maxValue > 0 && scrollState.viewportSize > 0) {
                        val thickness = 4.dp.toPx()
                        val margin = 6.dp.toPx()
                        val minThumbHeight = 32.dp.toPx()
                        val viewportSize = scrollState.viewportSize.toFloat()
                        val contentSize = (scrollState.maxValue + scrollState.viewportSize).toFloat()
                        val thumbHeight = (size.height * (viewportSize / contentSize))
                            .coerceAtLeast(minThumbHeight)
                            .coerceAtMost(size.height - (margin * 2))
                        val availableTrackHeight = (size.height - (margin * 2) - thumbHeight).coerceAtLeast(0f)
                        val thumbOffsetY = if (scrollState.maxValue == 0) {
                            margin
                        } else {
                            margin + availableTrackHeight * (scrollState.value / scrollState.maxValue.toFloat())
                        }
                        val scrollbarX = size.width - thickness - margin

                        drawRoundRect(
                            color = scrollbarTrackColor,
                            topLeft = Offset(x = scrollbarX, y = margin),
                            size = Size(width = thickness, height = size.height - (margin * 2)),
                            cornerRadius = CornerRadius(thickness, thickness),
                        )
                        drawRoundRect(
                            color = scrollbarColor,
                            topLeft = Offset(x = scrollbarX, y = thumbOffsetY),
                            size = Size(width = thickness, height = thumbHeight),
                            cornerRadius = CornerRadius(thickness, thickness),
                        )
                    }
                },
            scrollState = scrollState,
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 14.dp,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
            ),
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    onClick = { onScoreSelected(option) },
                )
            }
        }
    }
}

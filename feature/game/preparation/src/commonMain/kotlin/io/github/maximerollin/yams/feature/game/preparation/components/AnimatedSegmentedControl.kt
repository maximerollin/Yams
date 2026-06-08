package io.github.maximerollin.yams.feature.game.preparation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors

internal data class SegmentedControlItem(
    val label: String,
    val icon: ImageVector? = null,
    val iconContentDescription: String? = null,
)

@Composable
internal fun AnimatedSegmentedControl(
    items: List<SegmentedControlItem>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 42.dp,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    indicatorColor: Color = YamsTheme.colors.gold.copy(alpha = 0.28f),
    indicatorHorizontalInset: Dp = 6.dp,
    indicatorVerticalInset: Dp = 4.dp,
    selectedTextColor: Color = YamsTheme.colors.gold,
    unselectedTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall.copy(textAlign = TextAlign.Center),
    selectedFontWeight: FontWeight = FontWeight.SemiBold,
    unselectedFontWeight: FontWeight = FontWeight.Medium,
    labelMaxLines: Int = 1,
    segmentHorizontalPadding: Dp = 6.dp,
) {
    if (items.isEmpty()) return

    val clampedSelectedIndex = selectedIndex.coerceIn(0, items.lastIndex)

    Surface(
        shape = MaterialTheme.shapes.large,
        color = containerColor,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
        ) {
            val segmentWidth = maxWidth / items.size
            val indicatorWidth = if (segmentWidth > indicatorHorizontalInset * 2) {
                segmentWidth - (indicatorHorizontalInset * 2)
            } else {
                segmentWidth
            }
            val indicatorOffset by animateDpAsState(
                targetValue = segmentWidth * clampedSelectedIndex,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing,
                ),
                label = "SegmentedControlIndicatorOffset",
            )

            Surface(
                shape = MaterialTheme.shapes.large,
                color = indicatorColor,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = indicatorVerticalInset)
                    .width(indicatorWidth)
                    .offset(x = indicatorOffset + indicatorHorizontalInset),
            ) {}

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = clampedSelectedIndex == index
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) selectedTextColor else unselectedTextColor,
                        animationSpec = tween(durationMillis = 220),
                        label = "SegmentedControlTextColor",
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onSelectedIndexChange(index) },
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 6.dp,
                                alignment = Alignment.CenterHorizontally,
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = segmentHorizontalPadding),
                        ) {
                            item.icon?.let { icon ->
                                Icon(
                                    imageVector = icon,
                                    contentDescription = item.iconContentDescription,
                                    tint = textColor,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                            Text(
                                text = item.label,
                                style = textStyle,
                                fontWeight = if (isSelected) {
                                    selectedFontWeight
                                } else {
                                    unselectedFontWeight
                                },
                                color = textColor,
                                maxLines = labelMaxLines,
                                overflow = TextOverflow.Ellipsis,
                                softWrap = labelMaxLines > 1,
                            )
                        }
                    }
                }
            }
        }
    }
}

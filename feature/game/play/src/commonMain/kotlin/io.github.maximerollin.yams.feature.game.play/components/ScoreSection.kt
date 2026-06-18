package io.github.maximerollin.yams.feature.game.play.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import yams.feature.game.play.generated.resources.Res
import yams.feature.game.play.generated.resources.play_points_suffix

@Composable
internal fun ScoreSection(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    trailingValue: String,
    modifier: Modifier = Modifier,
    isCompactUi: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val cornerRadius by animateDpAsState(
        targetValue = if (isCompactUi) 18.dp else 24.dp,
        label = "scoreSectionCornerRadius",
    )
    val contentPadding by animateDpAsState(
        targetValue = if (isCompactUi) 8.dp else 16.dp,
        label = "scoreSectionContentPadding",
    )
    val sectionSpacing by animateDpAsState(
        targetValue = if (isCompactUi) 6.dp else 14.dp,
        label = "scoreSectionSpacing",
    )
    val headerSpacing by animateDpAsState(
        targetValue = if (isCompactUi) 8.dp else 12.dp,
        label = "scoreSectionHeaderSpacing",
    )
    val rowSpacing by animateDpAsState(
        targetValue = if (isCompactUi) 5.dp else 10.dp,
        label = "scoreSectionRowSpacing",
    )
    val iconSize by animateDpAsState(
        targetValue = if (isCompactUi) 30.dp else 40.dp,
        label = "scoreSectionIconSize",
    )
    val iconImageSize by animateDpAsState(
        targetValue = if (isCompactUi) 16.dp else 20.dp,
        label = "scoreSectionIconImageSize",
    )

    Card(
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(sectionSpacing),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(headerSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SectionAccentIcon(
                            icon = icon,
                            accentColor = accentColor,
                            size = iconSize,
                            iconSize = iconImageSize,
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = title,
                                style = if (isCompactUi) {
                                    MaterialTheme.typography.titleSmall
                                } else {
                                    MaterialTheme.typography.titleMedium
                                },
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            AnimatedVisibility(visible = !isCompactUi) {
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
                ScorePill(
                    value = trailingValue,
                    suffix = stringResource(Res.string.play_points_suffix),
                    isCompactUi = isCompactUi,
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(rowSpacing),
                content = content,
            )
        }
    }
}

@Composable
private fun SectionAccentIcon(
    icon: ImageVector,
    accentColor: Color,
    size: Dp,
    iconSize: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size)
            .background(
                brush = Brush.linearGradient(
                    listOf(accentColor, accentColor.copy(alpha = 0.7f))
                ),
                shape = RoundedCornerShape(14.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(iconSize),
        )
    }
}

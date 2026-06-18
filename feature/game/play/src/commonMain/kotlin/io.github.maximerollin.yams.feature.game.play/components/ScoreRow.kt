package io.github.maximerollin.yams.feature.game.play.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.feature.game.play.ScoreRowUi
import io.github.maximerollin.yams.feature.game.play.ScoreSelectionOption

@Composable
internal fun ScoreRow(
    row: ScoreRowUi,
    value: Int?,
    isEditable: Boolean,
    onClick: (() -> Unit)? = null,
    isMenuExpanded: Boolean = false,
    menuOptions: List<ScoreSelectionOption> = emptyList(),
    onDismissMenu: () -> Unit = {},
    onMenuItemClick: (ScoreSelectionOption) -> Unit = {},
    modifier: Modifier = Modifier,
    isCompactUi: Boolean = false,
) {
    val rowSpacing by animateDpAsState(
        targetValue = if (isCompactUi) 8.dp else 12.dp,
        label = "scoreRowSpacing",
    )
    val badgeWidth by animateDpAsState(
        targetValue = if (isCompactUi) 30.dp else 40.dp,
        label = "scoreRowBadgeWidth",
    )
    val badgeVerticalPadding by animateDpAsState(
        targetValue = if (isCompactUi) 5.dp else 8.dp,
        label = "scoreRowBadgeVerticalPadding",
    )
    val scoreWidth by animateDpAsState(
        targetValue = if (isCompactUi) 56.dp else 68.dp,
        label = "scoreRowValueWidth",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(rowSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(rowSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            row.badge?.let {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.width(badgeWidth),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = badgeVerticalPadding),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = it,
                            style = if (isCompactUi) {
                                MaterialTheme.typography.labelMedium
                            } else {
                                MaterialTheme.typography.labelLarge
                            },
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = row.label,
                    style = if (isCompactUi) {
                        MaterialTheme.typography.bodyMedium
                    } else {
                        MaterialTheme.typography.bodyLarge
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                )
                AnimatedVisibility(visible = !isCompactUi) {
                    Text(
                        text = row.supportingText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        ScorePill(
            value = value?.toString() ?: if (isEditable) "..." else "--",
            suffix = null,
            emphasize = isEditable && value != null,
            onClick = if (isEditable && value == null) onClick else null,
            isMenuExpanded = isMenuExpanded,
            menuOptions = menuOptions,
            onDismissMenu = onDismissMenu,
            onMenuItemClick = onMenuItemClick,
            fixedWidth = scoreWidth,
            isCompactUi = isCompactUi,
        )
    }
}

@Composable
internal fun SummaryRow(
    label: String,
    value: String,
    supportingText: String,
    emphasize: Boolean = false,
    modifier: Modifier = Modifier,
    isCompactUi: Boolean = false,
) {
    val rowSpacing by animateDpAsState(
        targetValue = if (isCompactUi) 8.dp else 12.dp,
        label = "summaryRowSpacing",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(rowSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = label,
                style = if (emphasize) {
                    MaterialTheme.typography.titleSmall
                } else {
                    MaterialTheme.typography.bodyMedium
                },
                color = MaterialTheme.colorScheme.onSurface,
            )
            AnimatedVisibility(visible = !isCompactUi) {
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        ScorePill(
            value = value,
            suffix = null,
            emphasize = emphasize,
            isCompactUi = isCompactUi,
        )
    }
}

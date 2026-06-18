package io.github.maximerollin.yams.feature.game.play.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.feature.game.play.ScoreRowUi
import io.github.maximerollin.yams.feature.game.play.ScoreSelectionOption
import org.jetbrains.compose.resources.stringResource
import yams.feature.game.play.generated.resources.Res
import yams.feature.game.play.generated.resources.play_column_short

private val ScoreLabelColumnWidth = 168.dp
private val ScoreCellWidth = 72.dp
private val ScoreCellSpacing = 8.dp
private const val CompactLabelWeight = 1.15f

private fun useInlineTwoColumnLayout(columnCount: Int): Boolean = columnCount == 2

@Composable
internal fun ScoreColumnHeader(
    columnCount: Int,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    isCompactUi: Boolean = false,
) {
    val useInlineLayout = useInlineTwoColumnLayout(columnCount)
    val rowSpacing by animateDpAsState(
        targetValue = if (isCompactUi) 8.dp else 12.dp,
        label = "scoreColumnHeaderRowSpacing",
    )
    val cellSpacing by animateDpAsState(
        targetValue = if (isCompactUi) 6.dp else ScoreCellSpacing,
        label = "scoreColumnHeaderCellSpacing",
    )
    val labelWidth by animateDpAsState(
        targetValue = if (isCompactUi) 156.dp else ScoreLabelColumnWidth,
        label = "scoreColumnHeaderLabelWidth",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(rowSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (useInlineLayout) {
            Box(modifier = Modifier.weight(CompactLabelWeight))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(cellSpacing),
            ) {
                repeat(columnCount) { index ->
                    ColumnHeaderCell(
                        text = stringResource(Res.string.play_column_short, index + 1),
                        compact = true,
                        isCompactUi = isCompactUi,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        } else {
            Box(modifier = Modifier.width(labelWidth))
            Box(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(cellSpacing),
                ) {
                    repeat(columnCount) { index ->
                        ColumnHeaderCell(
                            text = stringResource(Res.string.play_column_short, index + 1),
                            isCompactUi = isCompactUi,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnHeaderCell(
    text: String,
    compact: Boolean = false,
    isCompactUi: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val cornerRadius by animateDpAsState(
        targetValue = if (isCompactUi) 12.dp else 14.dp,
        label = "columnHeaderCellCornerRadius",
    )
    val verticalPadding by animateDpAsState(
        targetValue = if (isCompactUi) 5.dp else 8.dp,
        label = "columnHeaderCellVerticalPadding",
    )
    val cellWidth by animateDpAsState(
        targetValue = if (isCompactUi) 64.dp else ScoreCellWidth,
        label = "columnHeaderCellWidth",
    )

    Surface(
        shape = RoundedCornerShape(cornerRadius),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
        ),
        modifier = if (compact) modifier else modifier.width(cellWidth),
    ) {
        Box(
            modifier = Modifier.padding(vertical = verticalPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = YamsTheme.colors.brown,
            )
        }
    }
}

@Composable
internal fun ScoreGridRow(
    row: ScoreRowUi,
    values: List<Int?>,
    isEditable: Boolean,
    scrollState: ScrollState,
    onCellClick: ((Int) -> Unit)? = null,
    expandedColumnIndex: Int? = null,
    menuOptions: List<ScoreSelectionOption> = emptyList(),
    onDismissMenu: () -> Unit = {},
    onMenuItemClick: (ScoreSelectionOption) -> Unit = {},
    modifier: Modifier = Modifier,
    isCompactUi: Boolean = false,
) {
    val useInlineLayout = useInlineTwoColumnLayout(values.size)
    val rowSpacing by animateDpAsState(
        targetValue = if (isCompactUi) 8.dp else 12.dp,
        label = "scoreGridRowSpacing",
    )
    val cellSpacing by animateDpAsState(
        targetValue = if (isCompactUi) 6.dp else ScoreCellSpacing,
        label = "scoreGridCellSpacing",
    )
    val labelWidth by animateDpAsState(
        targetValue = if (isCompactUi) 156.dp else ScoreLabelColumnWidth,
        label = "scoreGridLabelWidth",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(rowSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (useInlineLayout) {
            ScoreRowLabel(
                row = row,
                compact = true,
                isCompactUi = isCompactUi,
                modifier = Modifier.weight(CompactLabelWeight),
            )
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(cellSpacing),
            ) {
                values.forEachIndexed { columnIndex, value ->
                    ScoreCell(
                        text = value?.toString() ?: if (isEditable) "..." else "--",
                        isFilled = value != null,
                        isEditable = isEditable,
                        emphasize = isEditable && value != null,
                        compact = true,
                        isCompactUi = isCompactUi,
                        isMenuExpanded = expandedColumnIndex == columnIndex,
                        menuOptions = menuOptions,
                        onDismissMenu = onDismissMenu,
                        onMenuItemClick = onMenuItemClick,
                        onClick = if (isEditable && value == null) {
                            { onCellClick?.invoke(columnIndex) }
                        } else {
                            null
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        } else {
            ScoreRowLabel(
                row = row,
                isCompactUi = isCompactUi,
                modifier = Modifier.width(labelWidth),
            )
            Box(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(cellSpacing),
                ) {
                    values.forEachIndexed { columnIndex, value ->
                        ScoreCell(
                            text = value?.toString() ?: if (isEditable) "..." else "--",
                            isFilled = value != null,
                            isEditable = isEditable,
                            emphasize = isEditable && value != null,
                            isCompactUi = isCompactUi,
                            isMenuExpanded = expandedColumnIndex == columnIndex,
                            menuOptions = menuOptions,
                            onDismissMenu = onDismissMenu,
                            onMenuItemClick = onMenuItemClick,
                            onClick = if (isEditable && value == null) {
                                { onCellClick?.invoke(columnIndex) }
                            } else {
                                null
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun SummaryGridRow(
    label: String,
    supportingText: String,
    values: List<String>,
    scrollState: ScrollState,
    emphasize: Boolean = false,
    modifier: Modifier = Modifier,
    isCompactUi: Boolean = false,
) {
    val useInlineLayout = useInlineTwoColumnLayout(values.size)
    val rowSpacing by animateDpAsState(
        targetValue = if (isCompactUi) 8.dp else 12.dp,
        label = "summaryGridRowSpacing",
    )
    val cellSpacing by animateDpAsState(
        targetValue = if (isCompactUi) 6.dp else ScoreCellSpacing,
        label = "summaryGridCellSpacing",
    )
    val labelWidth by animateDpAsState(
        targetValue = if (isCompactUi) 156.dp else ScoreLabelColumnWidth,
        label = "summaryGridLabelWidth",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(rowSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (useInlineLayout) {
            Column(
                modifier = Modifier.weight(CompactLabelWeight),
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
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(cellSpacing),
            ) {
                values.forEach { value ->
                    ScoreCell(
                        text = value,
                        isFilled = true,
                        isEditable = false,
                        emphasize = emphasize,
                        compact = true,
                        isCompactUi = isCompactUi,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.width(labelWidth),
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
            Box(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(cellSpacing),
                ) {
                    values.forEach { value ->
                        ScoreCell(
                            text = value,
                            isFilled = true,
                            isEditable = false,
                            emphasize = emphasize,
                            isCompactUi = isCompactUi,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreRowLabel(
    row: ScoreRowUi,
    compact: Boolean = false,
    isCompactUi: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val labelSpacing by animateDpAsState(
        targetValue = if (isCompactUi) 8.dp else 12.dp,
        label = "scoreRowLabelSpacing",
    )
    val badgeWidth by animateDpAsState(
        targetValue = when {
            isCompactUi && compact -> 28.dp
            isCompactUi -> 30.dp
            compact -> 32.dp
            else -> 40.dp
        },
        label = "scoreRowLabelBadgeWidth",
    )
    val badgeVerticalPadding by animateDpAsState(
        targetValue = if (isCompactUi) 5.dp else 8.dp,
        label = "scoreRowLabelBadgeVerticalPadding",
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(labelSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        row.badge?.let {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
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
                        style = if (compact) {
                            MaterialTheme.typography.labelMedium
                        } else {
                            MaterialTheme.typography.labelLarge
                        },
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
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
}

@Composable
private fun ScoreCell(
    text: String,
    isFilled: Boolean,
    isEditable: Boolean,
    emphasize: Boolean,
    compact: Boolean = false,
    isMenuExpanded: Boolean = false,
    menuOptions: List<ScoreSelectionOption> = emptyList(),
    onDismissMenu: () -> Unit = {},
    onMenuItemClick: (ScoreSelectionOption) -> Unit = {},
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isCompactUi: Boolean = false,
) {
    val cornerRadius by animateDpAsState(
        targetValue = if (isCompactUi) 14.dp else 16.dp,
        label = "scoreCellCornerRadius",
    )
    val verticalPadding by animateDpAsState(
        targetValue = if (isCompactUi) 6.dp else 10.dp,
        label = "scoreCellVerticalPadding",
    )
    val cellWidth by animateDpAsState(
        targetValue = if (isCompactUi) 64.dp else ScoreCellWidth,
        label = "scoreCellWidth",
    )

    Box(
        modifier = if (compact) modifier else modifier.width(cellWidth),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(cornerRadius),
            color = when {
                emphasize -> YamsTheme.colors.gold.copy(alpha = 0.2f)
                isFilled -> MaterialTheme.colorScheme.background
                else -> MaterialTheme.colorScheme.surface
            },
            border = BorderStroke(
                width = 1.dp,
                color = when {
                    emphasize -> YamsTheme.colors.gold.copy(alpha = 0.35f)
                    isEditable && !isFilled -> YamsTheme.colors.gold.copy(alpha = 0.28f)
                    !isFilled -> MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = if (isCompactUi) 36.dp else Dp.Unspecified)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(onClick = onClick)
                    } else {
                        Modifier
                    }
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = verticalPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = text,
                    style = if (isCompactUi) {
                        MaterialTheme.typography.bodyMedium
                    } else {
                        MaterialTheme.typography.titleSmall
                    },
                    color = when {
                        emphasize -> YamsTheme.colors.brown
                        isEditable && !isFilled -> YamsTheme.colors.brown
                        !isFilled -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                )
            }
        }

        ScoreSelectionMenu(
            expanded = isMenuExpanded,
            options = menuOptions,
            onDismissRequest = onDismissMenu,
            onScoreSelected = onMenuItemClick,
        )
    }
}

package io.github.maximerollin.yams.feature.game.play.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.feature.game.play.ScoreRowUi
import io.github.maximerollin.yams.feature.game.play.ScoreSelectionOption

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
) {
    val useInlineLayout = useInlineTwoColumnLayout(columnCount)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (useInlineLayout) {
            Box(modifier = Modifier.weight(CompactLabelWeight))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(ScoreCellSpacing),
            ) {
                repeat(columnCount) { index ->
                    ColumnHeaderCell(
                        text = "C${index + 1}",
                        compact = true,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        } else {
            Box(modifier = Modifier.width(ScoreLabelColumnWidth))
            Box(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(ScoreCellSpacing),
                ) {
                    repeat(columnCount) { index ->
                        ColumnHeaderCell(text = "C${index + 1}")
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
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
        ),
        modifier = if (compact) modifier else modifier.width(ScoreCellWidth),
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
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
) {
    val useInlineLayout = useInlineTwoColumnLayout(values.size)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (useInlineLayout) {
            ScoreRowLabel(
                row = row,
                compact = true,
                modifier = Modifier.weight(CompactLabelWeight),
            )
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(ScoreCellSpacing),
            ) {
                values.forEachIndexed { columnIndex, value ->
                    ScoreCell(
                        text = value?.toString() ?: if (isEditable) "+" else "--",
                        isFilled = value != null,
                        isEditable = isEditable,
                        emphasize = isEditable && value != null,
                        compact = true,
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
                modifier = Modifier.width(ScoreLabelColumnWidth),
            )
            Box(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(ScoreCellSpacing),
                ) {
                    values.forEachIndexed { columnIndex, value ->
                        ScoreCell(
                            text = value?.toString() ?: if (isEditable) "+" else "--",
                            isFilled = value != null,
                            isEditable = isEditable,
                            emphasize = isEditable && value != null,
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
) {
    val useInlineLayout = useInlineTwoColumnLayout(values.size)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
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
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(ScoreCellSpacing),
            ) {
                values.forEach { value ->
                    ScoreCell(
                        text = value,
                        isFilled = true,
                        isEditable = false,
                        emphasize = emphasize,
                        compact = true,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.width(ScoreLabelColumnWidth),
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
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(ScoreCellSpacing),
                ) {
                    values.forEach { value ->
                        ScoreCell(
                            text = value,
                            isFilled = true,
                            isEditable = false,
                            emphasize = emphasize,
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
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        row.badge?.let {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                modifier = Modifier.width(if (compact) 32.dp else 40.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
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
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = row.supportingText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
) {
    Box(
        modifier = if (compact) modifier else modifier.width(ScoreCellWidth),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
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
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall,
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

package io.github.maximerollin.yams.feature.game.play.components

import androidx.compose.foundation.ScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.maximerollin.yams.core.designsystem.icon.Timeline
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.model.ScoreKey
import io.github.maximerollin.yams.data.game.model.ScoreCellRef
import io.github.maximerollin.yams.feature.game.play.ScoreRowUi
import io.github.maximerollin.yams.feature.game.play.ScoreSelectionOption
import io.github.maximerollin.yams.feature.game.play.ScoreSelectionRequest
import io.github.maximerollin.yams.feature.game.play.model.PlayerState
import io.github.maximerollin.yams.feature.game.play.valueFor
import io.github.maximerollin.yams.feature.game.play.valuesFor

@Composable
internal fun GamePlayCombinationSection(
    rows: List<ScoreRowUi>,
    selectedPlayer: PlayerState,
    columnCount: Int,
    isMultiColumn: Boolean,
    isEditable: Boolean,
    lowerTotals: List<Int>,
    scrollState: ScrollState,
    onScoreCellClick: (ScoreRowUi, Int) -> Unit,
    scoreSelectionRequest: ScoreSelectionRequest?,
    onDismissScoreSelection: () -> Unit,
    onSelectScore: (ScoreSelectionOption, ScoreCellRef) -> Unit,
    modifier: Modifier = Modifier,
) {
    ScoreSection(
        title = "Combinaisons",
        subtitle = if (isMultiColumn) {
            "Choisis librement la colonne à remplir pour chaque figure."
        } else {
            "Brelan, carré, full, suites, Yams et variantes fixes."
        },
        icon = YamsIcons.Timeline,
        accentColor = YamsTheme.colors.brown,
        trailingValue = if (isMultiColumn) {
            lowerTotals.sum().toString()
        } else {
            lowerTotals.firstOrNull()?.toString().orEmpty()
        },
        modifier = modifier,
    ) {
        if (isMultiColumn) {
            ScoreColumnHeader(
                columnCount = columnCount,
                scrollState = scrollState,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surface)
        }

        if (isMultiColumn) {
            rows.forEachIndexed { index, row ->
                val rowScoreSelection = scoreSelectionRequest?.takeIf { it.cell.key == row.key }
                val rowIsEditable = isEditable && row.isInteractive
                if (row.key == ScoreKey.EXTRA_FIVE_OF_A_KIND) {
                    SummaryGridRow(
                        label = row.label,
                        supportingText = row.supportingText,
                        values = selectedPlayer.valuesFor(row.key, columnCount).map { (it ?: 0).toString() },
                        scrollState = scrollState,
                        emphasize = true,
                    )
                } else {
                    ScoreGridRow(
                        row = row,
                        values = selectedPlayer.valuesFor(row.key, columnCount),
                        isEditable = rowIsEditable,
                        scrollState = scrollState,
                        expandedColumnIndex = rowScoreSelection?.cell?.columnIndex,
                        menuOptions = rowScoreSelection?.options.orEmpty(),
                        onDismissMenu = onDismissScoreSelection,
                        onMenuItemClick = { option ->
                            rowScoreSelection?.let { onSelectScore(option, it.cell) }
                        },
                        onCellClick = { columnIndex ->
                            onScoreCellClick(row, columnIndex)
                        },
                    )
                }
                if (index < rows.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surface)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surface)

            SummaryGridRow(
                label = "Total combinaisons",
                supportingText = "Somme des figures de chaque colonne",
                values = lowerTotals.map(Int::toString),
                scrollState = scrollState,
                emphasize = true,
            )
        } else {
            rows.forEachIndexed { index, row ->
                val rowScoreSelection = scoreSelectionRequest?.takeIf {
                    it.cell.key == row.key && it.cell.columnIndex == 0
                }
                val rowIsEditable = isEditable && row.isInteractive
                if (row.key == ScoreKey.EXTRA_FIVE_OF_A_KIND) {
                    SummaryRow(
                        label = row.label,
                        value = (selectedPlayer.valueFor(row.key) ?: 0).toString(),
                        supportingText = row.supportingText,
                        emphasize = true,
                    )
                } else {
                    ScoreRow(
                        row = row,
                        value = selectedPlayer.valueFor(row.key),
                        isEditable = rowIsEditable,
                        onClick = { onScoreCellClick(row, 0) },
                        isMenuExpanded = rowScoreSelection != null,
                        menuOptions = rowScoreSelection?.options.orEmpty(),
                        onDismissMenu = onDismissScoreSelection,
                        onMenuItemClick = { option ->
                            rowScoreSelection?.let { onSelectScore(option, it.cell) }
                        },
                    )
                }
                if (index < rows.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surface)
                }
            }
        }
    }
}

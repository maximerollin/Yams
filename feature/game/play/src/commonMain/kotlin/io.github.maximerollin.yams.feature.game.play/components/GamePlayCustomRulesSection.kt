package io.github.maximerollin.yams.feature.game.play.components

import androidx.compose.foundation.ScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.maximerollin.yams.core.designsystem.icon.Tune
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.data.game.model.ScoreCellRef
import io.github.maximerollin.yams.feature.game.play.ScoreRowUi
import io.github.maximerollin.yams.feature.game.play.ScoreSelectionOption
import io.github.maximerollin.yams.feature.game.play.ScoreSelectionRequest
import io.github.maximerollin.yams.feature.game.play.model.PlayerState
import io.github.maximerollin.yams.feature.game.play.valueFor
import io.github.maximerollin.yams.feature.game.play.valuesFor
import org.jetbrains.compose.resources.stringResource
import yams.feature.game.play.generated.resources.*

@Composable
internal fun GamePlayCustomRulesSection(
    rows: List<ScoreRowUi>,
    selectedPlayer: PlayerState,
    columnCount: Int,
    isMultiColumn: Boolean,
    isEditable: Boolean,
    customTotals: List<Int>,
    scrollState: ScrollState,
    onScoreCellClick: (ScoreRowUi, Int) -> Unit,
    scoreSelectionRequest: ScoreSelectionRequest?,
    onDismissScoreSelection: () -> Unit,
    onSelectScore: (ScoreSelectionOption, ScoreCellRef) -> Unit,
    isCompactUi: Boolean = false,
    modifier: Modifier = Modifier,
) {
    if (rows.isEmpty()) {
        return
    }

    ScoreSection(
        title = stringResource(Res.string.play_custom_title),
        subtitle = if (isMultiColumn) {
            stringResource(Res.string.play_custom_subtitle_multi)
        } else {
            stringResource(Res.string.play_custom_subtitle_single)
        },
        icon = YamsIcons.Tune,
        accentColor = MaterialTheme.colorScheme.tertiary,
        trailingValue = if (isMultiColumn) {
            customTotals.sum().toString()
        } else {
            customTotals.firstOrNull()?.toString().orEmpty()
        },
        isCompactUi = isCompactUi,
        modifier = modifier,
    ) {
        if (isMultiColumn) {
            ScoreColumnHeader(
                columnCount = columnCount,
                scrollState = scrollState,
                isCompactUi = isCompactUi,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surface)
        }

        if (isMultiColumn) {
            rows.forEachIndexed { index, row ->
                val rowScoreSelection = scoreSelectionRequest?.takeIf { it.cell.key == row.key }
                val rowIsEditable = isEditable && row.isInteractive
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
                    isCompactUi = isCompactUi,
                )
                if (index < rows.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surface)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surface)

            SummaryGridRow(
                label = stringResource(Res.string.play_custom_total),
                supportingText = stringResource(Res.string.play_custom_total_help),
                values = customTotals.map(Int::toString),
                scrollState = scrollState,
                emphasize = true,
                isCompactUi = isCompactUi,
            )
        } else {
            rows.forEachIndexed { index, row ->
                val rowScoreSelection = scoreSelectionRequest?.takeIf {
                    it.cell.key == row.key && it.cell.columnIndex == 0
                }
                val rowIsEditable = isEditable && row.isInteractive
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
                    isCompactUi = isCompactUi,
                )
                if (index < rows.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surface)
                }
            }
        }
    }
}

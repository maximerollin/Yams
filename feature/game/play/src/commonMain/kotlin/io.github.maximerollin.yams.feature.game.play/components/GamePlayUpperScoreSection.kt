package io.github.maximerollin.yams.feature.game.play.components

import androidx.compose.foundation.ScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.maximerollin.yams.core.designsystem.icon.Target
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.data.game.model.ScoreCellRef
import io.github.maximerollin.yams.feature.game.play.ScoreRowUi
import io.github.maximerollin.yams.feature.game.play.ScoreSelectionOption
import io.github.maximerollin.yams.feature.game.play.ScoreSelectionRequest
import io.github.maximerollin.yams.feature.game.play.bonusStatusText
import io.github.maximerollin.yams.feature.game.play.model.PlayerState
import io.github.maximerollin.yams.feature.game.play.valueFor
import io.github.maximerollin.yams.feature.game.play.valuesFor
import org.jetbrains.compose.resources.stringResource
import yams.feature.game.play.generated.resources.*

@Composable
internal fun GamePlayUpperScoreSection(
    settings: GameSettings,
    rows: List<ScoreRowUi>,
    selectedPlayer: PlayerState,
    columnCount: Int,
    isMultiColumn: Boolean,
    isEditable: Boolean,
    scrollState: ScrollState,
    upperSubtotals: List<Int>,
    upperBonuses: List<Int>,
    upperTotals: List<Int>,
    onScoreCellClick: (ScoreRowUi, Int) -> Unit,
    scoreSelectionRequest: ScoreSelectionRequest?,
    onDismissScoreSelection: () -> Unit,
    onSelectScore: (ScoreSelectionOption, ScoreCellRef) -> Unit,
    modifier: Modifier = Modifier,
) {
    ScoreSection(
        title = stringResource(Res.string.play_upper_title),
        subtitle = if (isMultiColumn) {
            stringResource(Res.string.play_upper_subtitle_multi)
        } else {
            stringResource(Res.string.play_upper_subtitle_single)
        },
        icon = YamsIcons.Target,
        accentColor = YamsTheme.colors.gold,
        trailingValue = if (isMultiColumn) {
            upperTotals.sum().toString()
        } else {
            upperTotals.firstOrNull()?.toString().orEmpty()
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
                if (index < rows.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surface)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surface)

            SummaryGridRow(
                label = stringResource(Res.string.play_subtotal),
                supportingText = stringResource(Res.string.play_subtotal_by_column),
                values = upperSubtotals.map(Int::toString),
                scrollState = scrollState,
            )
            SummaryGridRow(
                label = stringResource(Res.string.play_bonus_threshold),
                supportingText = stringResource(Res.string.play_bonus_threshold_by_column),
                values = List(columnCount) { settings.upperBonusThreshold.toString() },
                scrollState = scrollState,
            )
            SummaryGridRow(
                label = stringResource(Res.string.play_bonus_value),
                supportingText = stringResource(Res.string.play_bonus_value_awarded),
                values = List(columnCount) { settings.upperBonusValue.toString() },
                scrollState = scrollState,
            )
            SummaryGridRow(
                label = stringResource(Res.string.play_bonus_applied),
                supportingText = stringResource(Res.string.play_bonus_by_column),
                values = upperBonuses.map(Int::toString),
                scrollState = scrollState,
            )
            SummaryGridRow(
                label = stringResource(Res.string.play_upper_total),
                supportingText = stringResource(Res.string.play_upper_total_by_column),
                values = upperTotals.map(Int::toString),
                scrollState = scrollState,
                emphasize = true,
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
                )
                if (index < rows.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surface)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surface)

            SummaryRow(
                label = stringResource(Res.string.play_subtotal),
                value = upperSubtotals.firstOrNull()?.toString().orEmpty(),
                supportingText = stringResource(Res.string.play_upper_subtotal_single),
            )
            SummaryRow(
                label = stringResource(Res.string.play_bonus_threshold),
                value = settings.upperBonusThreshold.toString(),
                supportingText = stringResource(Res.string.play_bonus_starts_at),
            )
            SummaryRow(
                label = stringResource(Res.string.play_bonus_value),
                value = settings.upperBonusValue.toString(),
                supportingText = if (settings.isUpperBonusEnabled) {
                    stringResource(Res.string.play_bonus_value_enabled)
                } else {
                    stringResource(Res.string.play_bonus_disabled)
                },
            )
            SummaryRow(
                label = stringResource(Res.string.play_upper_total),
                value = upperTotals.firstOrNull()?.toString().orEmpty(),
                supportingText = if (settings.isUpperBonusEnabled) {
                    bonusStatusText(
                        subtotal = upperSubtotals.firstOrNull() ?: 0,
                        settings = settings,
                    )
                } else {
                    stringResource(Res.string.play_no_bonus_calculation)
                },
                emphasize = true,
            )
        }
    }
}

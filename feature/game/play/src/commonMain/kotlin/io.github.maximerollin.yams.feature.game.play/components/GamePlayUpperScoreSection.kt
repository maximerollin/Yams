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
        title = "Table mineure",
        subtitle = if (isMultiColumn) {
            "Chaque ligne peut être remplie dans n'importe quelle colonne."
        } else {
            "De 1 à 6, avec le récapitulatif du bonus du haut."
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
                label = "Sous-total",
                supportingText = "Calculé séparément pour chaque colonne",
                values = upperSubtotals.map(Int::toString),
                scrollState = scrollState,
            )
            SummaryGridRow(
                label = "Seuil bonus",
                supportingText = "À atteindre dans chaque colonne",
                values = List(columnCount) { settings.upperBonusThreshold.toString() },
                scrollState = scrollState,
            )
            SummaryGridRow(
                label = "Valeur bonus",
                supportingText = "Récompense gagnée quand le seuil est atteint",
                values = List(columnCount) { settings.upperBonusValue.toString() },
                scrollState = scrollState,
            )
            SummaryGridRow(
                label = "Bonus appliqué",
                supportingText = "Le bonus peut être gagné dans une colonne et pas dans une autre",
                values = upperBonuses.map(Int::toString),
                scrollState = scrollState,
            )
            SummaryGridRow(
                label = "Total table mineure",
                supportingText = "Sous-total et bonus inclus par colonne",
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
                label = "Sous-total",
                value = upperSubtotals.firstOrNull()?.toString().orEmpty(),
                supportingText = "Somme actuelle de la section du haut",
            )
            SummaryRow(
                label = "Seuil bonus",
                value = settings.upperBonusThreshold.toString(),
                supportingText = "Le bonus démarre à partir de ce total",
            )
            SummaryRow(
                label = "Valeur bonus",
                value = settings.upperBonusValue.toString(),
                supportingText = if (settings.isUpperBonusEnabled) {
                    "Valeur ajoutée une fois le seuil atteint"
                } else {
                    "Bonus désactivé dans cette partie"
                },
            )
            SummaryRow(
                label = "Total table mineure",
                value = upperTotals.firstOrNull()?.toString().orEmpty(),
                supportingText = if (settings.isUpperBonusEnabled) {
                    bonusStatusText(
                        subtotal = upperSubtotals.firstOrNull() ?: 0,
                        settings = settings,
                    )
                } else {
                    "Calcul sans bonus"
                },
                emphasize = true,
            )
        }
    }
}

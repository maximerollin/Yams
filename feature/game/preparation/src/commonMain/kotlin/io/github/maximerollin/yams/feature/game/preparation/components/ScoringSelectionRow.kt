package io.github.maximerollin.yams.feature.game.preparation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.model.GameSettings

@Composable
internal fun ScoringSelectionRow(
    label: String,
    optionOneLabel: String,
    optionOne: GameSettings.SettingsScoring,
    optionTwoLabel: String,
    optionTwo: GameSettings.SettingsScoring,
    optionThreeLabel: String? = null,
    optionThree: GameSettings.SettingsScoring? = null,
    selected: GameSettings.SettingsScoring?,
    onOptionSelected: (GameSettings.SettingsScoring) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scoringOptions = buildList {
        add(optionOneLabel to optionOne)
        add(optionTwoLabel to optionTwo)
        if (optionThreeLabel != null && optionThree != null) {
            add(optionThreeLabel to optionThree)
        }
    }
    val selectedIndex = scoringOptions.indexOfFirst { (_, scoring) -> scoring == selected }
        .let { index -> if (index >= 0) index else 0 }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        AnimatedSegmentedControl(
            items = scoringOptions.map { (label, _) ->
                SegmentedControlItem(label = label)
            },
            selectedIndex = selectedIndex,
            onSelectedIndexChange = { index ->
                onOptionSelected(scoringOptions[index].second)
            },
            modifier = Modifier.fillMaxWidth(),
            height = 48.dp,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            labelMaxLines = 2,
            segmentHorizontalPadding = 6.dp,
        )
    }
}

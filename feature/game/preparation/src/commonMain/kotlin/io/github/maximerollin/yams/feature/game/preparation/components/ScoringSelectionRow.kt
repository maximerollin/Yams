package io.github.maximerollin.yams.feature.game.preparation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            GameSettingsToggleButton(
                label = optionOneLabel,
                selected = selected == optionOne,
                onClick = { onOptionSelected(optionOne) },
                modifier = Modifier.weight(1f),
            )
            GameSettingsToggleButton(
                label = optionTwoLabel,
                selected = selected == optionTwo,
                onClick = { onOptionSelected(optionTwo) },
                modifier = Modifier.weight(1f),
            )
            if (optionThreeLabel != null && optionThree != null) {
                GameSettingsToggleButton(
                    label = optionThreeLabel,
                    selected = selected == optionThree,
                    onClick = { onOptionSelected(optionThree) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

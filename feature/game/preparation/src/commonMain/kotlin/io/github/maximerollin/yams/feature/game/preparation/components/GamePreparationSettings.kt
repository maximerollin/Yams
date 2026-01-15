package io.github.maximerollin.yams.feature.game.preparation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.icon.Tune
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.model.GameSettings

@Composable
public fun GamePreparationSettings(
    uiState: GameSettings,
    onToggleGameSettings: (GameSettings.RuleSet) -> Unit,
    modifier: Modifier = Modifier,
) {
    GamePreparationSection(
        title = "Choix des règles",
        icon = YamsIcons.Tune,
        modifier = modifier,
    ) {
        val shape = RoundedCornerShape(32.dp)

        Surface(
            shape = shape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GameSettingsToggleButton(
                    label = "Yahtzee",
                    selected = uiState.ruleSet === GameSettings.RuleSet.YAHTZEE,
                    onClick = { onToggleGameSettings(GameSettings.RuleSet.YAHTZEE) },
                    modifier = Modifier.weight(1f),
                )

                GameSettingsToggleButton(
                    label = "Yams",
                    selected = uiState.ruleSet === GameSettings.RuleSet.YAMS,
                    onClick = { onToggleGameSettings(GameSettings.RuleSet.YAMS) },
                    modifier = Modifier.weight(1f),
                )

                GameSettingsToggleButton(
                    label = "My mom's ❤\uFE0F",
                    selected = uiState.ruleSet === GameSettings.RuleSet.MOM,
                    onClick = { onToggleGameSettings(GameSettings.RuleSet.MOM) },
                    modifier = Modifier.weight(1f),
                )
            }
        }


    }
}

@Preview
@Composable
public fun GamePreparationSettingsPreview() {
    YamsTheme {
        GamePreparationSettings(
            uiState = GameSettings.YamsSettings(),
            onToggleGameSettings = {}
        )
    }
}
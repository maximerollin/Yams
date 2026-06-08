package io.github.maximerollin.yams.feature.game.preparation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.icon.Tune
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.model.GameSettings
import org.jetbrains.compose.resources.stringResource
import yams.feature.game.preparation.generated.resources.*

@Composable
@OptIn(ExperimentalMaterial3Api::class)
public fun GamePreparationSettings(
    uiState: GameSettings,
    onToggleGameSettings: (GameSettings.RuleSet) -> Unit,
    modifier: Modifier = Modifier,
    onAddCustomRule: () -> Unit = {},
    onUpdateGameSettings: (GameSettings) -> Unit = {},
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    fun openRuleDetails() {
        showBottomSheet = true
    }

    GamePreparationSection(
        title = stringResource(Res.string.prep_rules_choice),
        icon = YamsIcons.Tune,
        onAction = {
            if (showBottomSheet) {
                showBottomSheet = false
            } else {
                openRuleDetails()
            }
        },
        onActionEnabled = true,
        onActionState = showBottomSheet,
        actionLabelClosed = stringResource(Res.string.prep_action_details),
        actionLabelOpen = stringResource(Res.string.prep_action_close),
        modifier = modifier,
    ) {
        val ruleSetItems = listOf(
            SegmentedControlItem(label = stringResource(Res.string.prep_rule_yahtzee)),
            SegmentedControlItem(label = stringResource(Res.string.prep_rule_yams)),
            SegmentedControlItem(label = stringResource(Res.string.prep_rule_custom)),
        )
        val ruleSets = listOf(
            GameSettings.RuleSet.YAHTZEE,
            GameSettings.RuleSet.YAMS,
            GameSettings.RuleSet.CUSTOM,
        )
        val selectedRuleSetIndex = ruleSets.indexOf(uiState.ruleSet).coerceAtLeast(0)

        AnimatedSegmentedControl(
            items = ruleSetItems,
            selectedIndex = selectedRuleSetIndex,
            onSelectedIndexChange = { index ->
                onToggleGameSettings(ruleSets[index])
                openRuleDetails()
            },
            modifier = Modifier.fillMaxWidth(),
            height = 44.dp,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            GameRulesBottomSheetContent(
                settings = uiState,
                onClose = { showBottomSheet = false },
                onAddCustomRule = onAddCustomRule,
                onUpdateGameSettings = onUpdateGameSettings,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(16.dp),
            )
        }
    }
}

@Preview
@Composable
public fun GamePreparationSettingsPreview() {
    YamsTheme {
        GamePreparationSettings(
            uiState = GameSettings.YamsSettings(),
            onToggleGameSettings = {},
        )
    }
}

@Preview
@Composable
public fun GamePreparationSettingsYahtzeePreview() {
    YamsTheme {
        GamePreparationSettings(
            uiState = GameSettings.YahtzeeSettings(),
            onToggleGameSettings = {},
        )
    }
}

@Preview
@Composable
public fun GamePreparationSettingsCustomPreview() {
    YamsTheme {
        GamePreparationSettings(
            uiState = GameSettings.CustomSettings(
                ruleSet = GameSettings.RuleSet.CUSTOM,
                customGameSettings = listOf(
                    GameSettings.CustomGameSettings(
                        title = "Bonus maison",
                        scoring = GameSettings.SettingsScoring.FIXED_CUSTOM,
                        value = 15,
                        description = "Ajout maison.",
                    ),
                ),
            ),
            onToggleGameSettings = {},
            onUpdateGameSettings = {},
        )
    }
}

@Preview
@Composable
public fun GamePreparationSettingsCompactPreview() {
    YamsTheme {
        Box(modifier = Modifier.width(320.dp)) {
            GamePreparationSettings(
                uiState = GameSettings.YamsSettings(),
                onToggleGameSettings = {},
            )
        }
    }
}

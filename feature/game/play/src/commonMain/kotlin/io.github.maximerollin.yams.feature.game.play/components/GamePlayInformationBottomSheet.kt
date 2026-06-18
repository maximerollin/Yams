package io.github.maximerollin.yams.feature.game.play.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.data.preference.GamePlayUiDensity
import org.jetbrains.compose.resources.stringResource
import yams.feature.game.play.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GamePlayInformationBottomSheet(
    settings: GameSettings,
    gamePlayUiDensity: GamePlayUiDensity,
    onGamePlayUiDensityChange: (GamePlayUiDensity) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        GamePlayInformationContent(
            settings = settings,
            gamePlayUiDensity = gamePlayUiDensity,
            onGamePlayUiDensityChange = onGamePlayUiDensityChange,
            modifier = modifier
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        )
    }
}

@Composable
private fun GamePlayInformationContent(
    settings: GameSettings,
    gamePlayUiDensity: GamePlayUiDensity = GamePlayUiDensity.NORMAL,
    onGamePlayUiDensityChange: (GamePlayUiDensity) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val customRules = settings.customGameSettings.filter { it.isEnabled }
    val isCompactUi = gamePlayUiDensity == GamePlayUiDensity.COMPACT

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = stringResource(Res.string.play_info_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = stringResource(Res.string.play_info_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        GameInformationSection(title = stringResource(Res.string.play_info_display)) {
            GameInformationToggleRow(
                label = stringResource(Res.string.play_compact_display),
                supportingText = stringResource(Res.string.play_compact_display_help),
                checked = isCompactUi,
                onCheckedChange = { isChecked ->
                    onGamePlayUiDensityChange(
                        if (isChecked) {
                            GamePlayUiDensity.COMPACT
                        } else {
                            GamePlayUiDensity.NORMAL
                        },
                    )
                },
            )
        }

        GameInformationSection(title = stringResource(Res.string.play_info_format)) {
            GameInformationRow(
                label = stringResource(Res.string.play_info_game),
                value = ruleSetLabel(settings.ruleSet),
            )
            GameInformationRow(
                label = stringResource(Res.string.play_info_columns),
                value = if (settings.columnCount == 1) {
                    stringResource(Res.string.play_one_column)
                } else {
                    stringResource(Res.string.play_many_columns, settings.columnCount)
                },
                supportingText = if (settings.columnCount == 1) {
                    stringResource(Res.string.play_classic_format)
                } else {
                    stringResource(Res.string.play_multi_column_format)
                },
            )
            GameInformationRow(
                label = stringResource(Res.string.play_chance),
                value = ruleValue(
                    isEnabled = settings.isChanceEnabled,
                    scoring = settings.chanceValue,
                ),
            )
        }

        GameInformationSection(title = stringResource(Res.string.play_info_scores)) {
            GameInformationRow(
                label = stringResource(Res.string.play_upper_bonus),
                value = if (settings.isUpperBonusEnabled) {
                    stringResource(
                        Res.string.play_upper_bonus_value,
                        settings.upperBonusThreshold,
                        settings.upperBonusValue,
                    )
                } else {
                    stringResource(Res.string.play_disabled)
                },
            )
            GameInformationRow(
                label = stringResource(Res.string.play_three_of_kind),
                value = ruleValue(
                    isEnabled = settings.isThreeOfAKindEnabled,
                    scoring = settings.threeOfAKindScoring,
                    fixedValue = settings.threeOfAKindValue,
                ),
            )
            GameInformationRow(
                label = stringResource(Res.string.play_four_of_kind),
                value = ruleValue(
                    isEnabled = settings.isFourOfAKindEnabled,
                    scoring = settings.fourOfAKindScoring,
                    fixedValue = settings.fourOfAKindValue,
                ),
            )
            GameInformationRow(
                label = stringResource(Res.string.play_full),
                value = if (settings.isFullHouseEnabled) {
                    stringResource(Res.string.play_points_value, settings.fullHouseValue)
                } else {
                    stringResource(Res.string.play_disabled)
                },
            )
            GameInformationRow(
                label = stringResource(Res.string.play_small_straight),
                value = if (settings.isSmallStraightEnabled) {
                    stringResource(Res.string.play_points_value, settings.smallStraightValue ?: 0)
                } else {
                    stringResource(Res.string.play_disabled_feminine)
                },
            )
            GameInformationRow(
                label = stringResource(Res.string.play_large_straight),
                value = if (settings.isLargeStraightEnabled) {
                    stringResource(Res.string.play_points_value, settings.largeStraightValue ?: 0)
                } else {
                    stringResource(Res.string.play_disabled_feminine)
                },
            )
            GameInformationRow(
                label = stringResource(Res.string.play_yams),
                value = if (settings.isFiveOfAKindEnabled) {
                    stringResource(Res.string.play_points_value, settings.fiveOfAKindValue)
                } else {
                    stringResource(Res.string.play_disabled)
                },
            )
            GameInformationRow(
                label = stringResource(Res.string.play_joker),
                value = if (settings.jokerRule) {
                    stringResource(Res.string.play_enabled)
                } else {
                    stringResource(Res.string.play_disabled)
                },
            )
            GameInformationRow(
                label = stringResource(Res.string.play_yams_bonus_info),
                value = settings.extraFiveOfAKindValue?.let { extraFiveOfAKindValue ->
                    if (settings.isExtraFiveOfAKindEnabled) {
                        stringResource(
                            Res.string.play_auto_bonus_value,
                            extraFiveOfAKindValue,
                        )
                    } else {
                        stringResource(Res.string.play_disabled)
                    }
                } ?: stringResource(Res.string.play_disabled),
                supportingText = if (
                    settings.isExtraFiveOfAKindEnabled &&
                    settings.extraFiveOfAKindValue != null
                ) {
                    stringResource(Res.string.play_auto_bonus_help)
                } else {
                    null
                },
            )
        }

        GameInformationSection(title = stringResource(Res.string.play_custom_rules)) {
            if (settings.areCustomRulesEnabled && customRules.isNotEmpty()) {
                customRules.forEachIndexed { index, rule ->
                    GameInformationRow(
                        label = rule.title,
                        value = ruleValue(rule),
                        supportingText = rule.description,
                    )
                    if (index < customRules.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        )
                    }
                }
            } else {
                GameInformationRow(
                    label = stringResource(Res.string.play_state),
                    value = stringResource(Res.string.play_no_active_rule),
                    supportingText = stringResource(Res.string.play_standard_rules_only),
                )
            }
        }
    }
}

@Composable
private fun GameInformationSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            content()
        }
    }
}

@Composable
private fun GameInformationToggleRow(
    label: String,
    supportingText: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        )
    }
}

@Composable
private fun GameInformationRow(
    label: String,
    value: String,
    supportingText: String? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End,
            )
        }

        if (!supportingText.isNullOrBlank()) {
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ruleValue(rule: GameSettings.CustomGameSettings): String =
    scoringLabel(scoring = rule.scoring, fixedValue = rule.value)

@Composable
private fun ruleValue(
    isEnabled: Boolean,
    scoring: GameSettings.SettingsScoring?,
    fixedValue: Int? = null,
): String {
    if (!isEnabled || scoring == null) {
        return stringResource(Res.string.play_disabled)
    }

    return scoringLabel(scoring = scoring, fixedValue = fixedValue)
}

@Composable
private fun scoringLabel(
    scoring: GameSettings.SettingsScoring,
    fixedValue: Int? = null,
): String {
    return when (scoring) {
        GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE -> stringResource(Res.string.play_sum_5_dice)
        GameSettings.SettingsScoring.SUM_MATCHING_THREE -> stringResource(
            Res.string.play_sum_3_identical,
        )
        GameSettings.SettingsScoring.SUM_MATCHING_FOUR -> stringResource(
            Res.string.play_sum_4_identical,
        )
        GameSettings.SettingsScoring.FIXED,
        GameSettings.SettingsScoring.FIXED_CUSTOM -> stringResource(
            Res.string.play_points_value,
            fixedValue ?: 0,
        )
    }
}

@Composable
private fun ruleSetLabel(ruleSet: GameSettings.RuleSet): String = when (ruleSet) {
    GameSettings.RuleSet.YAHTZEE -> stringResource(Res.string.play_rule_yahtzee)
    GameSettings.RuleSet.YAMS -> stringResource(Res.string.play_yams)
    GameSettings.RuleSet.CUSTOM -> stringResource(Res.string.play_rule_custom)
}

@Preview
@Composable
private fun GamePlayInformationContentPreview() {
    YamsTheme {
        GamePlayInformationContent(
            settings = GameSettings.CustomSettings(
                columnCount = 2,
                customGameSettings = listOf(
                    GameSettings.CustomGameSettings(
                        title = "Double paire",
                        scoring = GameSettings.SettingsScoring.FIXED_CUSTOM,
                        value = 15,
                        description = "Deux paires différentes rapportent 15 pts",
                    ),
                    GameSettings.CustomGameSettings(
                        title = "Tour du roi",
                        scoring = GameSettings.SettingsScoring.FIXED_CUSTOM,
                        value = 35,
                        description = "Cinq dés supérieurs ou égaux à 3 rapportent 35 pts",
                    ),
                ),
            ),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        )
    }
}

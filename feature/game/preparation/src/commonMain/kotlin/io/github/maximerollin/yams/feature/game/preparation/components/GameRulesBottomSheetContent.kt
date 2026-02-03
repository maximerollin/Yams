package io.github.maximerollin.yams.feature.game.preparation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.component.AppInput
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimarySmallButton
import io.github.maximerollin.yams.core.designsystem.icon.Close
import io.github.maximerollin.yams.core.designsystem.icon.Delete
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.core.model.GameSettings.Companion.getName

@Composable
internal fun GameRulesBottomSheetContent(
    settings: GameSettings,
    onClose: () -> Unit,
    onAddCustomRule: () -> Unit,
    onUpdateGameSettings: (GameSettings) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val currentSettings = toCustomSettings(settings)
    var newRuleTitle by remember { mutableStateOf("") }
    var newRuleValue by remember { mutableStateOf("") }
    var newRuleDescription by remember { mutableStateOf("") }
    var newRuleScoring by remember {
        mutableStateOf(GameSettings.SettingsScoring.FIXED_CUSTOM)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = YamsIcons.Close,
            contentDescription = "Fermer",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.End)
                .padding(horizontal = 16.dp)
                .size(24.dp)
                .clickable(onClick = onClose),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier.verticalScroll(scrollState),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = "Détails des règles",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "Ajustez les scores pour personnaliser votre partie.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        text = settings.ruleSet.getName(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }

            SettingsSection(
                title = "Combinaisons",
                subtitle = "Valeurs fixes pour les figures principales",
            ) {
                RuleToggleRow(
                    label = "Chance",
                    checked = currentSettings.isChanceEnabled,
                    onCheckedChange = { enabled ->
                        onUpdateGameSettings(currentSettings.copy(isChanceEnabled = enabled))
                    },
                )
                if (currentSettings.isChanceEnabled) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    ) {
                        Text(
                            text = "Somme des 5 dés",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }
                }
                RuleToggleRow(
                    label = "Brelan",
                    checked = currentSettings.isThreeOfAKindEnabled,
                    onCheckedChange = { enabled ->
                        onUpdateGameSettings(currentSettings.copy(isThreeOfAKindEnabled = enabled))
                    },
                )
                if (currentSettings.isThreeOfAKindEnabled) {
                    ScoringSelectionRow(
                        label = "Mode de score",
                        optionOneLabel = "Somme 3",
                        optionOne = GameSettings.SettingsScoring.SUM_MATCHING_THREE,
                        optionTwoLabel = "Somme 5",
                        optionTwo = GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE,
                        optionThreeLabel = "Valeur",
                        optionThree = GameSettings.SettingsScoring.FIXED_CUSTOM,
                        selected = currentSettings.threeOfAKindScoring,
                        onOptionSelected = { scoring ->
                            onUpdateGameSettings(
                                currentSettings.copy(
                                    threeOfAKindScoring = scoring,
                                    threeOfAKindValue = if (scoring == GameSettings.SettingsScoring.FIXED_CUSTOM) {
                                        currentSettings.threeOfAKindValue
                                    } else {
                                        null
                                    },
                                )
                            )
                        },
                    )
                    if (currentSettings.threeOfAKindScoring ==
                        GameSettings.SettingsScoring.FIXED_CUSTOM
                    ) {
                        RuleValueInputRow(
                            label = "Valeur brelan",
                            value = currentSettings.threeOfAKindValue,
                            onValueChange = { value ->
                                onUpdateGameSettings(
                                    currentSettings.copy(
                                        threeOfAKindValue = value,
                                        threeOfAKindScoring = GameSettings.SettingsScoring.FIXED_CUSTOM,
                                    )
                                )
                            },
                        )
                    }
                }
                RuleToggleRow(
                    label = "Carré",
                    checked = currentSettings.isFourOfAKindEnabled,
                    onCheckedChange = { enabled ->
                        onUpdateGameSettings(currentSettings.copy(isFourOfAKindEnabled = enabled))
                    },
                )
                if (currentSettings.isFourOfAKindEnabled) {
                    ScoringSelectionRow(
                        label = "Mode de score",
                        optionOneLabel = "Somme 4",
                        optionOne = GameSettings.SettingsScoring.SUM_MATCHING_FOUR,
                        optionTwoLabel = "Somme 5",
                        optionTwo = GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE,
                        optionThreeLabel = "Valeur",
                        optionThree = GameSettings.SettingsScoring.FIXED_CUSTOM,
                        selected = currentSettings.fourOfAKindScoring,
                        onOptionSelected = { scoring ->
                            onUpdateGameSettings(
                                currentSettings.copy(
                                    fourOfAKindScoring = scoring,
                                    fourOfAKindValue = if (scoring == GameSettings.SettingsScoring.FIXED_CUSTOM) {
                                        currentSettings.fourOfAKindValue
                                    } else {
                                        null
                                    },
                                )
                            )
                        },
                    )
                    if (currentSettings.fourOfAKindScoring ==
                        GameSettings.SettingsScoring.FIXED_CUSTOM
                    ) {
                        RuleValueInputRow(
                            label = "Valeur carré",
                            value = currentSettings.fourOfAKindValue,
                            onValueChange = { value ->
                                onUpdateGameSettings(
                                    currentSettings.copy(
                                        fourOfAKindValue = value,
                                        fourOfAKindScoring = GameSettings.SettingsScoring.FIXED_CUSTOM,
                                    )
                                )
                            },
                        )
                    }
                }
                RuleToggleRow(
                    label = "Full",
                    checked = currentSettings.isFullHouseEnabled,
                    onCheckedChange = { enabled ->
                        onUpdateGameSettings(currentSettings.copy(isFullHouseEnabled = enabled))
                    },
                )
                if (currentSettings.isFullHouseEnabled) {
                    RuleValueInputRow(
                        label = "Valeur full",
                        value = currentSettings.fullHouseValue,
                        isNullable = false,
                        onValueChange = { value ->
                            val resolvedValue = value ?: currentSettings.fullHouseValue
                            onUpdateGameSettings(currentSettings.copy(fullHouseValue = resolvedValue))
                        },
                    )
                }
                RuleToggleRow(
                    label = "Yams",
                    checked = currentSettings.isFiveOfAKindEnabled,
                    onCheckedChange = { enabled ->
                        onUpdateGameSettings(currentSettings.copy(isFiveOfAKindEnabled = enabled))
                    },
                )
                if (currentSettings.isFiveOfAKindEnabled) {
                    RuleValueInputRow(
                        label = "Valeur Yams",
                        value = currentSettings.fiveOfAKindValue,
                        isNullable = false,
                        onValueChange = { value ->
                            val resolvedValue = value ?: currentSettings.fiveOfAKindValue
                            onUpdateGameSettings(currentSettings.copy(fiveOfAKindValue = resolvedValue))
                        },
                    )
                }
                RuleToggleRow(
                    label = "Yams supplémentaire",
                    checked = currentSettings.isExtraFiveOfAKindEnabled,
                    onCheckedChange = { enabled ->
                        onUpdateGameSettings(currentSettings.copy(isExtraFiveOfAKindEnabled = enabled))
                    },
                )
                if (currentSettings.isExtraFiveOfAKindEnabled) {
                    RuleValueInputRow(
                        label = "Valeur Yams supplémentaire",
                        value = currentSettings.extraFiveOfAKindValue,
                        onValueChange = { value ->
                            onUpdateGameSettings(currentSettings.copy(extraFiveOfAKindValue = value))
                        },
                    )
                }
            }

            SettingsSection(
                title = "Suites",
                subtitle = "Scores liés aux suites consécutives",
            ) {
                RuleToggleRow(
                    label = "Petite suite (4 dés qui se suivent)",
                    checked = currentSettings.isSmallStraightEnabled,
                    onCheckedChange = { enabled ->
                        onUpdateGameSettings(currentSettings.copy(isSmallStraightEnabled = enabled))
                    },
                )
                if (currentSettings.isSmallStraightEnabled) {
                    RuleValueInputRow(
                        label = "Valeur petite suite",
                        value = currentSettings.smallStraightValue,
                        onValueChange = { value ->
                            onUpdateGameSettings(currentSettings.copy(smallStraightValue = value))
                        },
                    )
                }
                RuleToggleRow(
                    label = "Grande suite (5 dés qui se suivent)",
                    checked = currentSettings.isLargeStraightEnabled,
                    onCheckedChange = { enabled ->
                        onUpdateGameSettings(currentSettings.copy(isLargeStraightEnabled = enabled))
                    },
                )
                if (currentSettings.isLargeStraightEnabled) {
                    RuleValueInputRow(
                        label = "Valeur grande suite",
                        value = currentSettings.largeStraightValue,
                        onValueChange = { value ->
                            onUpdateGameSettings(currentSettings.copy(largeStraightValue = value))
                        },
                    )
                }
            }

            SettingsSection(
                title = "Bonus supérieur",
                subtitle = "Conditions et valeur du bonus",
            ) {
                RuleToggleRow(
                    label = "Activer le bonus supérieur",
                    checked = currentSettings.isUpperBonusEnabled,
                    onCheckedChange = { enabled ->
                        onUpdateGameSettings(currentSettings.copy(isUpperBonusEnabled = enabled))
                    },
                )
                if (currentSettings.isUpperBonusEnabled) {
                    RuleValueInputRow(
                        label = "Seuil",
                        value = currentSettings.upperBonusThreshold,
                        isNullable = false,
                        onValueChange = { value ->
                            val resolvedValue = value ?: currentSettings.upperBonusThreshold
                            onUpdateGameSettings(
                                currentSettings.copy(upperBonusThreshold = resolvedValue)
                            )
                        },
                    )
                    RuleValueInputRow(
                        label = "Valeur",
                        value = currentSettings.upperBonusValue,
                        isNullable = false,
                        onValueChange = { value ->
                            val resolvedValue = value ?: currentSettings.upperBonusValue
                            onUpdateGameSettings(
                                currentSettings.copy(upperBonusValue = resolvedValue)
                            )
                        },
                    )
                }
            }

            SettingsSection(
                title = "Règles personnalisées",
                subtitle = "Ajoutez vos propres scores spéciaux",
            ) {
                RuleToggleRow(
                    label = "Activer les règles personnalisées",
                    checked = currentSettings.areCustomRulesEnabled,
                    onCheckedChange = { enabled ->
                        onUpdateGameSettings(currentSettings.copy(areCustomRulesEnabled = enabled))
                    },
                )

                if (currentSettings.areCustomRulesEnabled) {
                    val canAddRule = newRuleTitle.isNotBlank()

                    if (currentSettings.customGameSettings.isNotEmpty()) {
                        currentSettings.customGameSettings.forEachIndexed { index, customRule ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RuleToggleRow(
                                    label = customRule.title,
                                    checked = customRule.isEnabled,
                                    onCheckedChange = { enabled ->
                                        val updatedCustomRules =
                                            currentSettings.customGameSettings.mapIndexed { currentIndex, rule ->
                                                if (currentIndex == index) {
                                                    rule.copy(isEnabled = enabled)
                                                } else {
                                                    rule
                                                }
                                            }
                                        onUpdateGameSettings(
                                            currentSettings.copy(customGameSettings = updatedCustomRules)
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                                Icon(
                                    imageVector = YamsIcons.Delete,
                                    contentDescription = "Supprimer la règle",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable {
                                            val updatedCustomRules =
                                                currentSettings.customGameSettings.filterIndexed { currentIndex, _ ->
                                                    currentIndex != index
                                                }
                                            onUpdateGameSettings(
                                                currentSettings.copy(customGameSettings = updatedCustomRules)
                                            )
                                        },
                                )
                            }
                            if (customRule.isEnabled) {
                                ScoringSelectionRow(
                                    label = "Mode de score",
                                    optionOneLabel = "Somme 5",
                                    optionOne = GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE,
                                    optionTwoLabel = "Valeur",
                                    optionTwo = GameSettings.SettingsScoring.FIXED_CUSTOM,
                                    selected = customRule.scoring,
                                    onOptionSelected = { scoring ->
                                        val updatedCustomRules =
                                            currentSettings.customGameSettings.mapIndexed { currentIndex, rule ->
                                                if (currentIndex == index) {
                                                    rule.copy(
                                                        scoring = scoring,
                                                        value = if (scoring == GameSettings.SettingsScoring.FIXED_CUSTOM) {
                                                            rule.value
                                                        } else {
                                                            null
                                                        },
                                                    )
                                                } else {
                                                    rule
                                                }
                                            }
                                        onUpdateGameSettings(
                                            currentSettings.copy(customGameSettings = updatedCustomRules)
                                        )
                                    },
                                )
                                if (customRule.scoring == GameSettings.SettingsScoring.FIXED_CUSTOM) {
                                    RuleValueInputRow(
                                        label = "Valeur",
                                        value = customRule.value,
                                        onValueChange = { value ->
                                            val updatedCustomRules =
                                                currentSettings.customGameSettings.mapIndexed { currentIndex, rule ->
                                                    if (currentIndex == index) {
                                                        rule.copy(value = value)
                                                    } else {
                                                        rule
                                                    }
                                                }
                                            onUpdateGameSettings(
                                                currentSettings.copy(customGameSettings = updatedCustomRules)
                                            )
                                        },
                                    )
                                }
                            }
                            val customRuleDescription = customRule.description
                            if (!customRuleDescription.isNullOrBlank()) {
                                Text(
                                    text = customRuleDescription,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            if (index != currentSettings.customGameSettings.lastIndex) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.surface,
                                    thickness = 1.dp,
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Aucune règle personnalisée pour le moment.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.surface,
                        thickness = 1.dp,
                    )

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                        ) {
                            Text(
                                text = "Nouvelle règle",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            AppInput(
                                value = newRuleTitle,
                                onValueChange = { newRuleTitle = it },
                                placeholder = {
                                    Text(
                                        text = "Nom de la règle",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                },
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (newRuleScoring == GameSettings.SettingsScoring.FIXED_CUSTOM) {
                                    AppInput(
                                        value = newRuleValue,
                                        onValueChange = {
                                            newRuleValue = it.filter { char -> char.isDigit() }
                                        },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number
                                        ),
                                        modifier = Modifier.weight(1f),
                                        placeholder = {
                                            Text(
                                                text = "Valeur (optionnelle)",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        },
                                    )
                                }
                                YamsPrimarySmallButton(
                                    onClick = {
                                        val parsedValue = newRuleValue.toIntOrNull()
                                        val updatedCustomRules =
                                            currentSettings.customGameSettings +
                                                    GameSettings.CustomGameSettings(
                                                        title = newRuleTitle.trim(),
                                                        scoring = newRuleScoring,
                                                        value = if (newRuleScoring ==
                                                            GameSettings.SettingsScoring.FIXED_CUSTOM
                                                        ) {
                                                            parsedValue
                                                        } else {
                                                            null
                                                        },
                                                        description = newRuleDescription.trim()
                                                            .ifBlank { null },
                                                        isEnabled = true,
                                                    )
                                        onUpdateGameSettings(
                                            currentSettings.copy(customGameSettings = updatedCustomRules)
                                        )
                                        onAddCustomRule()
                                        newRuleTitle = ""
                                        newRuleValue = ""
                                        newRuleDescription = ""
                                        newRuleScoring = GameSettings.SettingsScoring.FIXED_CUSTOM
                                    },
                                    text = "Ajouter",
                                    enabled = canAddRule,
                                )
                            }
                            ScoringSelectionRow(
                                label = "Mode de score",
                                optionOneLabel = "Somme 5",
                                optionOne = GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE,
                                optionTwoLabel = "Valeur",
                                optionTwo = GameSettings.SettingsScoring.FIXED_CUSTOM,
                                selected = newRuleScoring,
                                onOptionSelected = { scoring ->
                                    newRuleScoring = scoring
                                    if (scoring != GameSettings.SettingsScoring.FIXED_CUSTOM) {
                                        newRuleValue = ""
                                    }
                                },
                            )
                            AppInput(
                                value = newRuleDescription,
                                onValueChange = { newRuleDescription = it },
                                placeholder = {
                                    Text(
                                        text = "Description (optionnelle)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun toCustomSettings(settings: GameSettings): GameSettings.CustomSettings =
    when (settings) {
        is GameSettings.CustomSettings -> settings.copy(ruleSet = GameSettings.RuleSet.CUSTOM)
        is GameSettings.YamsSettings -> GameSettings.CustomSettings(
            ruleSet = GameSettings.RuleSet.CUSTOM,
            columnCount = settings.columnCount,
            chanceValue = settings.chanceValue,
            isChanceEnabled = settings.isChanceEnabled,
            threeOfAKindScoring = settings.threeOfAKindScoring,
            threeOfAKindValue = settings.threeOfAKindValue,
            fourOfAKindScoring = settings.fourOfAKindScoring,
            fourOfAKindValue = settings.fourOfAKindValue,
            isThreeOfAKindEnabled = settings.isThreeOfAKindEnabled,
            isFourOfAKindEnabled = settings.isFourOfAKindEnabled,
            fullHouseValue = settings.fullHouseValue,
            isFullHouseEnabled = settings.isFullHouseEnabled,
            smallStraightValue = settings.smallStraightValue,
            largeStraightValue = settings.largeStraightValue,
            isSmallStraightEnabled = settings.isSmallStraightEnabled,
            isLargeStraightEnabled = settings.isLargeStraightEnabled,
            fiveOfAKindValue = settings.fiveOfAKindValue,
            isFiveOfAKindEnabled = settings.isFiveOfAKindEnabled,
            jokerRule = settings.jokerRule,
            extraFiveOfAKindValue = settings.extraFiveOfAKindValue,
            isExtraFiveOfAKindEnabled = settings.isExtraFiveOfAKindEnabled,
            upperBonusThreshold = settings.upperBonusThreshold,
            upperBonusValue = settings.upperBonusValue,
            isUpperBonusEnabled = settings.isUpperBonusEnabled,
            areCustomRulesEnabled = settings.areCustomRulesEnabled,
            customGameSettings = settings.customGameSettings,
        )

        is GameSettings.YahtzeeSettings -> GameSettings.CustomSettings(
            ruleSet = GameSettings.RuleSet.CUSTOM,
            columnCount = settings.columnCount,
            chanceValue = settings.chanceValue,
            isChanceEnabled = settings.isChanceEnabled,
            threeOfAKindScoring = settings.threeOfAKindScoring,
            threeOfAKindValue = settings.threeOfAKindValue,
            fourOfAKindScoring = settings.fourOfAKindScoring,
            fourOfAKindValue = settings.fourOfAKindValue,
            isThreeOfAKindEnabled = settings.isThreeOfAKindEnabled,
            isFourOfAKindEnabled = settings.isFourOfAKindEnabled,
            fullHouseValue = settings.fullHouseValue,
            isFullHouseEnabled = settings.isFullHouseEnabled,
            smallStraightValue = settings.smallStraightValue,
            largeStraightValue = settings.largeStraightValue,
            isSmallStraightEnabled = settings.isSmallStraightEnabled,
            isLargeStraightEnabled = settings.isLargeStraightEnabled,
            fiveOfAKindValue = settings.fiveOfAKindValue,
            isFiveOfAKindEnabled = settings.isFiveOfAKindEnabled,
            jokerRule = settings.jokerRule,
            extraFiveOfAKindValue = settings.extraFiveOfAKindValue,
            isExtraFiveOfAKindEnabled = settings.isExtraFiveOfAKindEnabled,
            upperBonusThreshold = settings.upperBonusThreshold,
            upperBonusValue = settings.upperBonusValue,
            isUpperBonusEnabled = settings.isUpperBonusEnabled,
            areCustomRulesEnabled = settings.areCustomRulesEnabled,
            customGameSettings = settings.customGameSettings,
        )
    }

@Preview
@Composable
public fun GameRulesBottomSheetContentPreview() {
    YamsTheme {
        val settings = GameSettings.YamsSettings(
            customGameSettings = listOf(
                GameSettings.CustomGameSettings(
                    title = "Turbo",
                    scoring = GameSettings.SettingsScoring.FIXED_CUSTOM,
                    value = 40,
                    description = "Bonus rapide si 4 dés se suivent.",
                ),
            ),
        )
        Box(modifier = Modifier.padding(16.dp)) {
            GameRulesBottomSheetContent(
                settings = settings,
                onClose = {},
                onAddCustomRule = {},
                onUpdateGameSettings = {},
            )
        }
    }
}

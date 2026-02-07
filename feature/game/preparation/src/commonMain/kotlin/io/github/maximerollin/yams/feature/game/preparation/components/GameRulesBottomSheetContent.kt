package io.github.maximerollin.yams.feature.game.preparation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import io.github.maximerollin.yams.core.designsystem.component.YamsTextButton
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
    var selectedSection by remember { mutableStateOf(RuleEditorSection.COMBINATIONS) }
    var isNewRuleFormExpanded by remember { mutableStateOf(false) }
    var newRuleTitle by remember { mutableStateOf("") }
    var newRuleValue by remember { mutableStateOf("") }
    var newRuleDescription by remember { mutableStateOf("") }
    var newRuleScoring by remember {
        mutableStateOf(GameSettings.SettingsScoring.FIXED_CUSTOM)
    }
    val customRulesCount = currentSettings.customGameSettings.size
    val canAddRule = newRuleTitle.isNotBlank()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(onClick = onClose),
                ) {
                    Icon(
                        imageVector = YamsIcons.Close,
                        contentDescription = "Fermer",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Règles de la partie",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f),
                )
                RuleMetaChip(text = settings.ruleSet.getName())
            }
            Text(
                text = "Modifiez une catégorie à la fois pour garder une vue claire.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        RuleSectionTabs(
            selectedSection = selectedSection,
            onSectionSelected = { section ->
                selectedSection = section
                if (
                    section == RuleEditorSection.CUSTOM &&
                    currentSettings.customGameSettings.isEmpty()
                ) {
                    isNewRuleFormExpanded = true
                }
            },
        )

        SettingsSection(
            title = selectedSection.title,
            subtitle = selectedSection.subtitle,
        ) {
            AnimatedContent(
                targetState = selectedSection,
                transitionSpec = {
                    val forward = targetState.ordinal >= initialState.ordinal
                    (
                        fadeIn(
                            animationSpec = tween(
                                durationMillis = 220,
                                easing = LinearOutSlowInEasing,
                            )
                        ) + slideInVertically(
                            animationSpec = tween(
                                durationMillis = 240,
                                easing = FastOutSlowInEasing,
                            ),
                            initialOffsetY = { if (forward) it / 5 else -it / 5 },
                        )
                        ).togetherWith(
                            fadeOut(
                                animationSpec = tween(
                                    durationMillis = 140,
                                    easing = FastOutSlowInEasing,
                                )
                            ) + slideOutVertically(
                                animationSpec = tween(
                                    durationMillis = 180,
                                    easing = FastOutSlowInEasing,
                                ),
                                targetOffsetY = { if (forward) -it / 8 else it / 8 },
                            )
                        ).using(
                            SizeTransform(clip = false)
                        )
                },
                label = "RuleSectionContent",
            ) { section ->
                when (section) {
                    RuleEditorSection.COMBINATIONS -> {
                        CombinationRulesContent(
                            currentSettings = currentSettings,
                            onUpdateGameSettings = onUpdateGameSettings,
                        )
                    }

                    RuleEditorSection.STRAIGHTS -> {
                        StraightsRulesContent(
                            currentSettings = currentSettings,
                            onUpdateGameSettings = onUpdateGameSettings,
                        )
                    }

                    RuleEditorSection.BONUS -> {
                        BonusRulesContent(
                            currentSettings = currentSettings,
                            onUpdateGameSettings = onUpdateGameSettings,
                        )
                    }

                    RuleEditorSection.CUSTOM -> {
                        CustomRulesContent(
                            currentSettings = currentSettings,
                            isNewRuleFormExpanded = isNewRuleFormExpanded,
                            onToggleNewRuleForm = {
                                isNewRuleFormExpanded = !isNewRuleFormExpanded
                            },
                            newRuleTitle = newRuleTitle,
                            onNewRuleTitleChange = { newRuleTitle = it },
                            newRuleValue = newRuleValue,
                            onNewRuleValueChange = { value ->
                                newRuleValue = value.filter { char -> char.isDigit() }
                            },
                            newRuleDescription = newRuleDescription,
                            onNewRuleDescriptionChange = { newRuleDescription = it },
                            newRuleScoring = newRuleScoring,
                            onNewRuleScoringChange = { scoring ->
                                newRuleScoring = scoring
                                if (scoring != GameSettings.SettingsScoring.FIXED_CUSTOM) {
                                    newRuleValue = ""
                                }
                            },
                            customRulesCount = customRulesCount,
                            canAddRule = canAddRule,
                            onUpdateGameSettings = onUpdateGameSettings,
                            onCustomRulesEnabledChange = { enabled ->
                                onUpdateGameSettings(
                                    currentSettings.copy(areCustomRulesEnabled = enabled)
                                )
                                if (enabled && customRulesCount == 0) {
                                    isNewRuleFormExpanded = true
                                }
                            },
                            onAddRule = {
                                val parsedValue = newRuleValue.toIntOrNull()
                                val updatedCustomRules =
                                    currentSettings.customGameSettings +
                                            GameSettings.CustomGameSettings(
                                                title = newRuleTitle.trim(),
                                                scoring = newRuleScoring,
                                                value = if (
                                                    newRuleScoring == GameSettings.SettingsScoring.FIXED_CUSTOM
                                                ) {
                                                    parsedValue
                                                } else {
                                                    null
                                                },
                                                description = newRuleDescription.trim().ifBlank { null },
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
                                isNewRuleFormExpanded = false
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RuleSectionTabs(
    selectedSection: RuleEditorSection,
    onSectionSelected: (RuleEditorSection) -> Unit,
) {
    val sections = RuleEditorSection.values()
    AnimatedSegmentedControl(
        items = sections.map { section ->
            SegmentedControlItem(label = section.tabLabel)
        },
        selectedIndex = sections.indexOf(selectedSection).coerceAtLeast(0),
        onSelectedIndexChange = { index -> onSectionSelected(sections[index]) },
        modifier = Modifier.fillMaxWidth(),
        height = 42.dp,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
    )

    AnimatedContent(
        targetState = selectedSection.hint,
        transitionSpec = {
            fadeIn(animationSpec = tween(180))
                .togetherWith(fadeOut(animationSpec = tween(120)))
        },
        label = "RuleSectionHint",
    ) { hint ->
        Text(
            text = hint,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CombinationRulesContent(
    currentSettings: GameSettings.CustomSettings,
    onUpdateGameSettings: (GameSettings) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        RuleCard(
            label = "Chance",
            checked = currentSettings.isChanceEnabled,
            onCheckedChange = { enabled ->
                onUpdateGameSettings(currentSettings.copy(isChanceEnabled = enabled))
            },
        ) {
            RuleMetaChip(text = "Somme des 5 dés")
        }

        RuleCard(
            label = "Brelan",
            checked = currentSettings.isThreeOfAKindEnabled,
            onCheckedChange = { enabled ->
                onUpdateGameSettings(currentSettings.copy(isThreeOfAKindEnabled = enabled))
            },
        ) {
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
                            threeOfAKindValue = if (
                                scoring == GameSettings.SettingsScoring.FIXED_CUSTOM
                            ) {
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

        RuleCard(
            label = "Carré",
            checked = currentSettings.isFourOfAKindEnabled,
            onCheckedChange = { enabled ->
                onUpdateGameSettings(currentSettings.copy(isFourOfAKindEnabled = enabled))
            },
        ) {
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
                            fourOfAKindValue = if (
                                scoring == GameSettings.SettingsScoring.FIXED_CUSTOM
                            ) {
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

        RuleCard(
            label = "Full",
            checked = currentSettings.isFullHouseEnabled,
            onCheckedChange = { enabled ->
                onUpdateGameSettings(currentSettings.copy(isFullHouseEnabled = enabled))
            },
        ) {
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

        RuleCard(
            label = "Yams",
            checked = currentSettings.isFiveOfAKindEnabled,
            onCheckedChange = { enabled ->
                onUpdateGameSettings(currentSettings.copy(isFiveOfAKindEnabled = enabled))
            },
        ) {
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

        RuleCard(
            label = "Yams supplémentaire",
            checked = currentSettings.isExtraFiveOfAKindEnabled,
            onCheckedChange = { enabled ->
                onUpdateGameSettings(currentSettings.copy(isExtraFiveOfAKindEnabled = enabled))
            },
        ) {
            RuleValueInputRow(
                label = "Valeur Yams supplémentaire",
                value = currentSettings.extraFiveOfAKindValue,
                onValueChange = { value ->
                    onUpdateGameSettings(currentSettings.copy(extraFiveOfAKindValue = value))
                },
            )
        }
    }
}

@Composable
private fun StraightsRulesContent(
    currentSettings: GameSettings.CustomSettings,
    onUpdateGameSettings: (GameSettings) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        RuleCard(
            label = "Petite suite",
            checked = currentSettings.isSmallStraightEnabled,
            onCheckedChange = { enabled ->
                onUpdateGameSettings(currentSettings.copy(isSmallStraightEnabled = enabled))
            },
        ) {
            RuleMetaChip(text = "4 dés qui se suivent")
            RuleValueInputRow(
                label = "Valeur petite suite",
                value = currentSettings.smallStraightValue,
                onValueChange = { value ->
                    onUpdateGameSettings(currentSettings.copy(smallStraightValue = value))
                },
            )
        }

        RuleCard(
            label = "Grande suite",
            checked = currentSettings.isLargeStraightEnabled,
            onCheckedChange = { enabled ->
                onUpdateGameSettings(currentSettings.copy(isLargeStraightEnabled = enabled))
            },
        ) {
            RuleMetaChip(text = "5 dés qui se suivent")
            RuleValueInputRow(
                label = "Valeur grande suite",
                value = currentSettings.largeStraightValue,
                onValueChange = { value ->
                    onUpdateGameSettings(currentSettings.copy(largeStraightValue = value))
                },
            )
        }
    }
}

@Composable
private fun BonusRulesContent(
    currentSettings: GameSettings.CustomSettings,
    onUpdateGameSettings: (GameSettings) -> Unit,
) {
    RuleCard(
        label = "Activer le bonus supérieur",
        checked = currentSettings.isUpperBonusEnabled,
        onCheckedChange = { enabled ->
            onUpdateGameSettings(currentSettings.copy(isUpperBonusEnabled = enabled))
        },
    ) {
        RuleValueInputRow(
            label = "Seuil",
            value = currentSettings.upperBonusThreshold,
            isNullable = false,
            onValueChange = { value ->
                val resolvedValue = value ?: currentSettings.upperBonusThreshold
                onUpdateGameSettings(currentSettings.copy(upperBonusThreshold = resolvedValue))
            },
        )
        RuleValueInputRow(
            label = "Valeur",
            value = currentSettings.upperBonusValue,
            isNullable = false,
            onValueChange = { value ->
                val resolvedValue = value ?: currentSettings.upperBonusValue
                onUpdateGameSettings(currentSettings.copy(upperBonusValue = resolvedValue))
            },
        )
    }
}

@Composable
private fun CustomRulesContent(
    currentSettings: GameSettings.CustomSettings,
    isNewRuleFormExpanded: Boolean,
    onToggleNewRuleForm: () -> Unit,
    newRuleTitle: String,
    onNewRuleTitleChange: (String) -> Unit,
    newRuleValue: String,
    onNewRuleValueChange: (String) -> Unit,
    newRuleDescription: String,
    onNewRuleDescriptionChange: (String) -> Unit,
    newRuleScoring: GameSettings.SettingsScoring,
    onNewRuleScoringChange: (GameSettings.SettingsScoring) -> Unit,
    customRulesCount: Int,
    canAddRule: Boolean,
    onUpdateGameSettings: (GameSettings) -> Unit,
    onCustomRulesEnabledChange: (Boolean) -> Unit,
    onAddRule: () -> Unit,
) {
    RuleCard(
        label = "Activer les règles personnalisées",
        checked = currentSettings.areCustomRulesEnabled,
        onCheckedChange = onCustomRulesEnabledChange,
    ) {
        val enabledCustomRules = currentSettings.customGameSettings.count { it.isEnabled }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RuleMetaChip(text = "$enabledCustomRules active(s) / $customRulesCount")
            YamsTextButton(onClick = onToggleNewRuleForm) {
                Text(if (isNewRuleFormExpanded) "Masquer le formulaire" else "Ajouter une règle")
            }
        }

        AnimatedVisibility(visible = isNewRuleFormExpanded) {
            NewCustomRuleForm(
                newRuleTitle = newRuleTitle,
                onNewRuleTitleChange = onNewRuleTitleChange,
                newRuleValue = newRuleValue,
                onNewRuleValueChange = onNewRuleValueChange,
                newRuleDescription = newRuleDescription,
                onNewRuleDescriptionChange = onNewRuleDescriptionChange,
                newRuleScoring = newRuleScoring,
                onNewRuleScoringChange = onNewRuleScoringChange,
                canAddRule = canAddRule,
                onAddRule = onAddRule,
            )
        }

        Text(
            text = "Règles existantes",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (currentSettings.customGameSettings.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                currentSettings.customGameSettings.forEachIndexed { index, customRule ->
                    CustomRuleCard(
                        customRule = customRule,
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
                        onDelete = {
                            val updatedCustomRules =
                                currentSettings.customGameSettings.filterIndexed { currentIndex, _ ->
                                    currentIndex != index
                                }
                            onUpdateGameSettings(
                                currentSettings.copy(customGameSettings = updatedCustomRules)
                            )
                        },
                        onScoringSelected = { scoring ->
                            val updatedCustomRules =
                                currentSettings.customGameSettings.mapIndexed { currentIndex, rule ->
                                    if (currentIndex == index) {
                                        rule.copy(
                                            scoring = scoring,
                                            value = if (
                                                scoring == GameSettings.SettingsScoring.FIXED_CUSTOM
                                            ) {
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
        } else {
            Text(
                text = "Aucune règle personnalisée pour le moment.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CustomRuleCard(
    customRule: GameSettings.CustomGameSettings,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onScoringSelected: (GameSettings.SettingsScoring) -> Unit,
    onValueChange: (Int?) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RuleToggleRow(
                    label = customRule.title,
                    checked = customRule.isEnabled,
                    onCheckedChange = onCheckedChange,
                    modifier = Modifier.weight(1f),
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.75f),
                    modifier = Modifier.clickable(onClick = onDelete),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            imageVector = YamsIcons.Delete,
                            contentDescription = "Supprimer la règle",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }

            AnimatedRuleDetails(visible = customRule.isEnabled) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    ScoringSelectionRow(
                        label = "Mode de score",
                        optionOneLabel = "Somme 5",
                        optionOne = GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE,
                        optionTwoLabel = "Valeur",
                        optionTwo = GameSettings.SettingsScoring.FIXED_CUSTOM,
                        selected = customRule.scoring,
                        onOptionSelected = onScoringSelected,
                    )
                    if (customRule.scoring == GameSettings.SettingsScoring.FIXED_CUSTOM) {
                        RuleValueInputRow(
                            label = "Valeur",
                            value = customRule.value,
                            onValueChange = onValueChange,
                        )
                    }
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
        }
    }
}

@Composable
private fun NewCustomRuleForm(
    newRuleTitle: String,
    onNewRuleTitleChange: (String) -> Unit,
    newRuleValue: String,
    onNewRuleValueChange: (String) -> Unit,
    newRuleDescription: String,
    onNewRuleDescriptionChange: (String) -> Unit,
    newRuleScoring: GameSettings.SettingsScoring,
    onNewRuleScoringChange: (GameSettings.SettingsScoring) -> Unit,
    canAddRule: Boolean,
    onAddRule: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Text(
                text = "Créer une nouvelle règle",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Nom obligatoire, description facultative.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AppInput(
                value = newRuleTitle,
                onValueChange = onNewRuleTitleChange,
                placeholder = {
                    Text(
                        text = "Nom de la règle",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
            ScoringSelectionRow(
                label = "Mode de score",
                optionOneLabel = "Somme 5",
                optionOne = GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE,
                optionTwoLabel = "Valeur",
                optionTwo = GameSettings.SettingsScoring.FIXED_CUSTOM,
                selected = newRuleScoring,
                onOptionSelected = onNewRuleScoringChange,
            )
            if (newRuleScoring == GameSettings.SettingsScoring.FIXED_CUSTOM) {
                AppInput(
                    value = newRuleValue,
                    onValueChange = onNewRuleValueChange,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                    placeholder = {
                        Text(
                            text = "Valeur (optionnelle)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                )
            }
            AppInput(
                value = newRuleDescription,
                onValueChange = onNewRuleDescriptionChange,
                placeholder = {
                    Text(
                        text = "Description (optionnelle)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
            YamsPrimarySmallButton(
                onClick = onAddRule,
                text = "Ajouter la règle",
                enabled = canAddRule,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun RuleCard(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    details: @Composable ColumnScope.() -> Unit = {},
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            RuleToggleRow(
                label = label,
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
            AnimatedRuleDetails(visible = checked) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    content = details,
                )
            }
        }
    }
}

@Composable
private fun AnimatedRuleDetails(
    visible: Boolean,
    content: @Composable () -> Unit,
) {
    AnimatedContent(
        targetState = visible,
        transitionSpec = {
            val expanding = targetState
            val enterDuration = if (expanding) 170 else 90
            val exitDuration = if (expanding) 120 else 200
            val sizeDuration = if (expanding) 220 else 300

            fadeIn(
                animationSpec = tween(
                    durationMillis = enterDuration,
                    easing = LinearOutSlowInEasing,
                )
            ).togetherWith(
                fadeOut(
                    animationSpec = tween(
                        durationMillis = exitDuration,
                        easing = FastOutSlowInEasing,
                    )
                )
            ).using(
                SizeTransform(
                    clip = false,
                    sizeAnimationSpec = { _, _ ->
                        tween(
                            durationMillis = sizeDuration,
                            easing = FastOutSlowInEasing,
                        )
                    },
                )
            )
        },
        label = "RuleDetails",
    ) { isVisible ->
        if (isVisible) {
            content()
        } else {
            Spacer(modifier = Modifier.height(0.dp))
        }
    }
}

private enum class RuleEditorSection(
    val tabLabel: String,
    val title: String,
    val subtitle: String,
    val hint: String,
) {
    COMBINATIONS(
        tabLabel = "Combos",
        title = "Combinaisons",
        subtitle = "Figures principales du tableau",
        hint = "Activez les combinaisons utiles, puis choisissez leur mode de score.",
    ),
    STRAIGHTS(
        tabLabel = "Suites",
        title = "Suites",
        subtitle = "Petite et grande suite",
        hint = "Réglez uniquement les suites à jouer dans votre variante.",
    ),
    BONUS(
        tabLabel = "Bonus",
        title = "Bonus supérieur",
        subtitle = "Seuil et valeur du bonus",
        hint = "Définissez le seuil à atteindre et la valeur accordée.",
    ),
    CUSTOM(
        tabLabel = "Perso",
        title = "Règles personnalisées",
        subtitle = "Ajoutez vos scores spéciaux",
        hint = "Gardez vos règles maison dans cette section dédiée.",
    ),
}

@Composable
private fun RuleMetaChip(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
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

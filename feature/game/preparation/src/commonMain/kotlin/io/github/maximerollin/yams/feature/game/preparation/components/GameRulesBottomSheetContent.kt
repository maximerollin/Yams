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
import org.jetbrains.compose.resources.stringResource
import yams.feature.game.preparation.generated.resources.*

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
                        contentDescription = stringResource(Res.string.prep_rules_close_cd),
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
                    text = stringResource(Res.string.prep_rules_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f),
                )
                RuleMetaChip(text = ruleSetLabel(settings.ruleSet))
            }
            Text(
                text = stringResource(Res.string.prep_rules_subtitle),
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
            title = selectedSection.title(),
            subtitle = selectedSection.subtitle(),
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
    val sections = RuleEditorSection.entries.toTypedArray()
    AnimatedSegmentedControl(
        items = sections.map { section ->
            SegmentedControlItem(label = section.tabLabel())
        },
        selectedIndex = sections.indexOf(selectedSection).coerceAtLeast(0),
        onSelectedIndexChange = { index -> onSectionSelected(sections[index]) },
        modifier = Modifier.fillMaxWidth(),
        height = 42.dp,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
    )

    AnimatedContent(
        targetState = selectedSection,
        transitionSpec = {
            fadeIn(animationSpec = tween(180))
                .togetherWith(fadeOut(animationSpec = tween(120)))
        },
        label = "RuleSectionHint",
    ) { section ->
        Text(
            text = section.hint(),
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
            label = stringResource(Res.string.prep_rule_chance),
            checked = currentSettings.isChanceEnabled,
            onCheckedChange = { enabled ->
                onUpdateGameSettings(currentSettings.copy(isChanceEnabled = enabled))
            },
        ) {
            RuleMetaChip(text = stringResource(Res.string.prep_sum_5_dice))
        }

        RuleCard(
            label = stringResource(Res.string.prep_three_of_kind),
            checked = currentSettings.isThreeOfAKindEnabled,
            onCheckedChange = { enabled ->
                onUpdateGameSettings(currentSettings.copy(isThreeOfAKindEnabled = enabled))
            },
        ) {
            ScoringSelectionRow(
                label = stringResource(Res.string.prep_score_mode),
                optionOneLabel = stringResource(Res.string.prep_sum_3_dice),
                optionOne = GameSettings.SettingsScoring.SUM_MATCHING_THREE,
                optionTwoLabel = stringResource(Res.string.prep_sum_5_dice),
                optionTwo = GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE,
                optionThreeLabel = stringResource(Res.string.prep_fixed_value),
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
                    label = stringResource(Res.string.prep_three_of_kind_value),
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
            label = stringResource(Res.string.prep_four_of_kind),
            checked = currentSettings.isFourOfAKindEnabled,
            onCheckedChange = { enabled ->
                onUpdateGameSettings(currentSettings.copy(isFourOfAKindEnabled = enabled))
            },
        ) {
            ScoringSelectionRow(
                label = stringResource(Res.string.prep_score_mode),
                optionOneLabel = stringResource(Res.string.prep_sum_4_dice),
                optionOne = GameSettings.SettingsScoring.SUM_MATCHING_FOUR,
                optionTwoLabel = stringResource(Res.string.prep_sum_5_dice),
                optionTwo = GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE,
                optionThreeLabel = stringResource(Res.string.prep_fixed_value),
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
                    label = stringResource(Res.string.prep_four_of_kind_value),
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
            label = stringResource(Res.string.prep_full),
            checked = currentSettings.isFullHouseEnabled,
            onCheckedChange = { enabled ->
                onUpdateGameSettings(currentSettings.copy(isFullHouseEnabled = enabled))
            },
        ) {
            RuleValueInputRow(
                label = stringResource(Res.string.prep_full_value),
                value = currentSettings.fullHouseValue,
                isNullable = false,
                onValueChange = { value ->
                    val resolvedValue = value ?: currentSettings.fullHouseValue
                    onUpdateGameSettings(currentSettings.copy(fullHouseValue = resolvedValue))
                },
            )
        }

        RuleCard(
            label = stringResource(Res.string.prep_rule_yams),
            checked = currentSettings.isFiveOfAKindEnabled,
            onCheckedChange = { enabled ->
                onUpdateGameSettings(currentSettings.copy(isFiveOfAKindEnabled = enabled))
            },
        ) {
            RuleValueInputRow(
                label = stringResource(Res.string.prep_yams_value),
                value = currentSettings.fiveOfAKindValue,
                isNullable = false,
                onValueChange = { value ->
                    val resolvedValue = value ?: currentSettings.fiveOfAKindValue
                    onUpdateGameSettings(currentSettings.copy(fiveOfAKindValue = resolvedValue))
                },
            )
        }

        RuleCard(
            label = stringResource(Res.string.prep_extra_yams),
            checked = currentSettings.isExtraFiveOfAKindEnabled,
            onCheckedChange = { enabled ->
                onUpdateGameSettings(currentSettings.copy(isExtraFiveOfAKindEnabled = enabled))
            },
        ) {
            RuleValueInputRow(
                label = stringResource(Res.string.prep_extra_yams_value),
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
            label = stringResource(Res.string.prep_small_straight),
            checked = currentSettings.isSmallStraightEnabled,
            onCheckedChange = { enabled ->
                onUpdateGameSettings(currentSettings.copy(isSmallStraightEnabled = enabled))
            },
        ) {
            RuleMetaChip(text = stringResource(Res.string.prep_four_sequence))
            RuleValueInputRow(
                label = stringResource(Res.string.prep_small_straight_value),
                value = currentSettings.smallStraightValue,
                onValueChange = { value ->
                    onUpdateGameSettings(currentSettings.copy(smallStraightValue = value))
                },
            )
        }

        RuleCard(
            label = stringResource(Res.string.prep_large_straight),
            checked = currentSettings.isLargeStraightEnabled,
            onCheckedChange = { enabled ->
                onUpdateGameSettings(currentSettings.copy(isLargeStraightEnabled = enabled))
            },
        ) {
            RuleMetaChip(text = stringResource(Res.string.prep_five_sequence))
            RuleValueInputRow(
                label = stringResource(Res.string.prep_large_straight_value),
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
        label = stringResource(Res.string.prep_enable_upper_bonus),
        checked = currentSettings.isUpperBonusEnabled,
        onCheckedChange = { enabled ->
            onUpdateGameSettings(currentSettings.copy(isUpperBonusEnabled = enabled))
        },
    ) {
        RuleValueInputRow(
            label = stringResource(Res.string.prep_threshold),
            value = currentSettings.upperBonusThreshold,
            isNullable = false,
            onValueChange = { value ->
                val resolvedValue = value ?: currentSettings.upperBonusThreshold
                onUpdateGameSettings(currentSettings.copy(upperBonusThreshold = resolvedValue))
            },
        )
        RuleValueInputRow(
            label = stringResource(Res.string.prep_value),
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
        label = stringResource(Res.string.prep_enable_custom_rules),
        checked = currentSettings.areCustomRulesEnabled,
        onCheckedChange = onCustomRulesEnabledChange,
    ) {
        val enabledCustomRules = currentSettings.customGameSettings.count { it.isEnabled }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RuleMetaChip(
                text = stringResource(
                    Res.string.prep_custom_rules_count,
                    enabledCustomRules,
                    customRulesCount,
                ),
            )
            YamsTextButton(onClick = onToggleNewRuleForm) {
                Text(
                    if (isNewRuleFormExpanded) {
                        stringResource(Res.string.prep_hide_form)
                    } else {
                        stringResource(Res.string.prep_add_rule)
                    },
                )
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
            text = stringResource(Res.string.prep_existing_rules),
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
                text = stringResource(Res.string.prep_no_custom_rules),
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
                            contentDescription = stringResource(Res.string.prep_delete_rule_cd),
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
                        label = stringResource(Res.string.prep_score_mode),
                        optionOneLabel = stringResource(Res.string.prep_sum_5_dice),
                        optionOne = GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE,
                        optionTwoLabel = stringResource(Res.string.prep_fixed_value),
                        optionTwo = GameSettings.SettingsScoring.FIXED_CUSTOM,
                        selected = customRule.scoring,
                        onOptionSelected = onScoringSelected,
                    )
                    if (customRule.scoring == GameSettings.SettingsScoring.FIXED_CUSTOM) {
                        RuleValueInputRow(
                            label = stringResource(Res.string.prep_value),
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
                text = stringResource(Res.string.prep_create_rule),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = stringResource(Res.string.prep_create_rule_help),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AppInput(
                value = newRuleTitle,
                onValueChange = onNewRuleTitleChange,
                placeholder = {
                    Text(
                        text = stringResource(Res.string.prep_rule_name_placeholder),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
            ScoringSelectionRow(
                label = stringResource(Res.string.prep_score_mode),
                optionOneLabel = stringResource(Res.string.prep_sum_5_dice),
                optionOne = GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE,
                optionTwoLabel = stringResource(Res.string.prep_fixed_value),
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
                            text = stringResource(Res.string.prep_value_optional),
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
                        text = stringResource(Res.string.prep_description_optional),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
            YamsPrimarySmallButton(
                onClick = onAddRule,
                text = stringResource(Res.string.prep_add_rule_button),
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

private enum class RuleEditorSection {
    COMBINATIONS,
    STRAIGHTS,
    BONUS,
    CUSTOM,
}

@Composable
private fun RuleEditorSection.tabLabel(): String = when (this) {
    RuleEditorSection.COMBINATIONS -> stringResource(Res.string.prep_tab_combos)
    RuleEditorSection.STRAIGHTS -> stringResource(Res.string.prep_tab_straights)
    RuleEditorSection.BONUS -> stringResource(Res.string.prep_tab_bonus)
    RuleEditorSection.CUSTOM -> stringResource(Res.string.prep_tab_custom)
}

@Composable
private fun RuleEditorSection.title(): String = when (this) {
    RuleEditorSection.COMBINATIONS -> stringResource(Res.string.prep_section_combinations)
    RuleEditorSection.STRAIGHTS -> stringResource(Res.string.prep_section_straights)
    RuleEditorSection.BONUS -> stringResource(Res.string.prep_section_bonus)
    RuleEditorSection.CUSTOM -> stringResource(Res.string.prep_section_custom)
}

@Composable
private fun RuleEditorSection.subtitle(): String = when (this) {
    RuleEditorSection.COMBINATIONS -> stringResource(
        Res.string.prep_section_combinations_subtitle,
    )

    RuleEditorSection.STRAIGHTS -> stringResource(Res.string.prep_section_straights_subtitle)
    RuleEditorSection.BONUS -> stringResource(Res.string.prep_section_bonus_subtitle)
    RuleEditorSection.CUSTOM -> stringResource(Res.string.prep_section_custom_subtitle)
}

@Composable
private fun RuleEditorSection.hint(): String = when (this) {
    RuleEditorSection.COMBINATIONS -> stringResource(Res.string.prep_section_combinations_hint)
    RuleEditorSection.STRAIGHTS -> stringResource(Res.string.prep_section_straights_hint)
    RuleEditorSection.BONUS -> stringResource(Res.string.prep_section_bonus_hint)
    RuleEditorSection.CUSTOM -> stringResource(Res.string.prep_section_custom_hint)
}

@Composable
private fun ruleSetLabel(ruleSet: GameSettings.RuleSet): String = when (ruleSet) {
    GameSettings.RuleSet.YAHTZEE -> stringResource(Res.string.prep_rule_yahtzee)
    GameSettings.RuleSet.YAMS -> stringResource(Res.string.prep_rule_yams)
    GameSettings.RuleSet.CUSTOM -> stringResource(Res.string.prep_rule_custom)
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

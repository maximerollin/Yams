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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.core.model.GameSettings.Companion.getName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GamePlayInformationBottomSheet(
    settings: GameSettings,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        GamePlayInformationContent(
            settings = settings,
            modifier = modifier
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        )
    }
}

@Composable
private fun GamePlayInformationContent(
    settings: GameSettings,
    modifier: Modifier = Modifier,
) {
    val customRules = settings.customGameSettings.filter { it.isEnabled }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Informations de partie",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Rappel rapide des règles actives pour cette feuille.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        GameInformationSection(title = "Format") {
            GameInformationRow(
                label = "Jeu",
                value = settings.ruleSet.getName(),
            )
            GameInformationRow(
                label = "Colonnes",
                value = if (settings.columnCount == 1) {
                    "1 colonne"
                } else {
                    "${settings.columnCount} colonnes"
                },
                supportingText = if (settings.columnCount == 1) {
                    "Format classique sur une seule grille."
                } else {
                    "Chaque ligne peut être remplie dans n'importe quelle colonne."
                },
            )
            GameInformationRow(
                label = "Chance",
                value = ruleValue(
                    isEnabled = settings.isChanceEnabled,
                    scoring = settings.chanceValue,
                ),
            )
        }

        GameInformationSection(title = "Scores") {
            GameInformationRow(
                label = "Bonus haut",
                value = if (settings.isUpperBonusEnabled) {
                    "${settings.upperBonusThreshold} pts -> +${settings.upperBonusValue}"
                } else {
                    "Désactivé"
                },
            )
            GameInformationRow(
                label = "Brelan",
                value = ruleValue(
                    isEnabled = settings.isThreeOfAKindEnabled,
                    scoring = settings.threeOfAKindScoring,
                    fixedValue = settings.threeOfAKindValue,
                ),
            )
            GameInformationRow(
                label = "Carré",
                value = ruleValue(
                    isEnabled = settings.isFourOfAKindEnabled,
                    scoring = settings.fourOfAKindScoring,
                    fixedValue = settings.fourOfAKindValue,
                ),
            )
            GameInformationRow(
                label = "Full",
                value = if (settings.isFullHouseEnabled) {
                    "${settings.fullHouseValue} pts"
                } else {
                    "Désactivé"
                },
            )
            GameInformationRow(
                label = "Petite suite",
                value = if (settings.isSmallStraightEnabled) {
                    "${settings.smallStraightValue ?: 0} pts"
                } else {
                    "Désactivée"
                },
            )
            GameInformationRow(
                label = "Grande suite",
                value = if (settings.isLargeStraightEnabled) {
                    "${settings.largeStraightValue ?: 0} pts"
                } else {
                    "Désactivée"
                },
            )
            GameInformationRow(
                label = "Yams",
                value = if (settings.isFiveOfAKindEnabled) {
                    "${settings.fiveOfAKindValue} pts"
                } else {
                    "Désactivé"
                },
            )
            GameInformationRow(
                label = "Joker",
                value = if (settings.jokerRule) "Activé" else "Désactivé",
            )
            GameInformationRow(
                label = "Bonus Yams",
                value = if (
                    settings.isExtraFiveOfAKindEnabled &&
                    settings.extraFiveOfAKindValue != null
                ) {
                    "Auto +${settings.extraFiveOfAKindValue}"
                } else {
                    "Désactivé"
                },
                supportingText = if (
                    settings.isExtraFiveOfAKindEnabled &&
                    settings.extraFiveOfAKindValue != null
                ) {
                    "Ajouté automatiquement quand un nouveau Yam est joué après la case Yams."
                } else {
                    null
                },
            )
        }

        GameInformationSection(title = "Règles custom") {
            if (settings.areCustomRulesEnabled && customRules.isNotEmpty()) {
                customRules.forEachIndexed { index, rule ->
                    GameInformationRow(
                        label = rule.title,
                        value = scoringLabel(
                            scoring = rule.scoring,
                            fixedValue = rule.value,
                        ),
                        supportingText = rule.description,
                    )
                    if (index < customRules.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    }
                }
            } else {
                GameInformationRow(
                    label = "État",
                    value = "Aucune règle active",
                    supportingText = "Cette partie utilise uniquement les règles standards.",
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

private fun ruleValue(
    isEnabled: Boolean,
    scoring: GameSettings.SettingsScoring?,
    fixedValue: Int? = null,
): String {
    if (!isEnabled || scoring == null) {
        return "Désactivé"
    }

    return scoringLabel(scoring = scoring, fixedValue = fixedValue)
}

private fun scoringLabel(
    scoring: GameSettings.SettingsScoring,
    fixedValue: Int? = null,
): String {
    return when (scoring) {
        GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE -> "Somme des 5 dés"
        GameSettings.SettingsScoring.SUM_MATCHING_THREE -> "Somme des 3 dés"
        GameSettings.SettingsScoring.SUM_MATCHING_FOUR -> "Somme des 4 dés"
        GameSettings.SettingsScoring.FIXED,
        GameSettings.SettingsScoring.FIXED_CUSTOM -> "${fixedValue ?: 0} pts"
    }
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

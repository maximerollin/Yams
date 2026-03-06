package io.github.maximerollin.yams.feature.game.play

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.component.AppTopBar
import io.github.maximerollin.yams.core.designsystem.icon.ChevronLeft
import io.github.maximerollin.yams.core.designsystem.icon.ChevronRight
import io.github.maximerollin.yams.core.designsystem.icon.Crown
import io.github.maximerollin.yams.core.designsystem.icon.PersonRaisedHand
import io.github.maximerollin.yams.core.designsystem.icon.Target
import io.github.maximerollin.yams.core.designsystem.icon.Timeline
import io.github.maximerollin.yams.core.designsystem.icon.Tune
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.mocks.UserMocks
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.core.model.User
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun GamePlayRoute(
    gameId: GameId,
    onNavigateHome: () -> Unit,
    onNavigateToResults: (gameId: GameId) -> Unit,
    viewModel: GamePlayViewModel = koinViewModel { parametersOf(gameId) }
) {
    GamePlayScreen()
}

@Composable
private fun GamePlayScreen(
    uiState: GamePlayMockUiState = gamePlayMockUiState(),
    initialSelectedPlayerIndex: Int = uiState.currentTurnPlayerIndex,
    modifier: Modifier = Modifier,
) {
    var selectedPlayerIndex by rememberSaveable(
        uiState.players.size,
        initialSelectedPlayerIndex,
    ) {
        mutableStateOf(initialSelectedPlayerIndex.coerceIn(0, uiState.players.lastIndex))
    }

    val selectedPlayer = uiState.players[selectedPlayerIndex]
    val currentTurnPlayer = uiState.players[uiState.currentTurnPlayerIndex]
    val isEditable = selectedPlayerIndex == uiState.currentTurnPlayerIndex
    val upperRows = buildUpperScoreRows()
    val lowerRows = buildMainScoreRows(uiState.settings)
    val customRows = buildCustomScoreRows(uiState.settings)
    val upperSubtotal = selectedPlayer.sumOf(upperRows)
    val upperTotal = upperSubtotal + upperBonus(upperSubtotal, uiState.settings)
    val lowerTotal = selectedPlayer.sumOf(lowerRows)
    val customTotal = selectedPlayer.sumOf(customRows)
    val overallTotal = upperTotal + lowerTotal + customTotal

    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            AppTopBar(
                isDividerVisible = false,
                center = {
                    Text(
                        text = "Feuille de score",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleMedium,
                        color = YamsTheme.colors.brown,
                        textAlign = TextAlign.Center,
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ScoreSheetOverviewCard(
                selectedPlayer = selectedPlayer,
                currentTurnPlayer = currentTurnPlayer,
                isEditable = isEditable,
                overallTotal = overallTotal,
                selectedIndex = selectedPlayerIndex,
                playerCount = uiState.players.size,
                onPreviousPlayer = {
                    selectedPlayerIndex =
                        if (selectedPlayerIndex == 0) uiState.players.lastIndex else selectedPlayerIndex - 1
                },
                onNextPlayer = {
                    selectedPlayerIndex = (selectedPlayerIndex + 1) % uiState.players.size
                },
            )

            ScoreSection(
                title = "Table mineure",
                subtitle = "De 1 à 6, avec le récapitulatif du bonus du haut.",
                icon = YamsIcons.Target,
                accentColor = YamsTheme.colors.gold,
                trailingValue = upperTotal.toString(),
            ) {
                upperRows.forEachIndexed { index, row ->
                    ScoreRow(
                        row = row,
                        value = selectedPlayer.scores[row.key],
                        isEditable = isEditable,
                    )
                    if (index < upperRows.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surface)
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.surface)

                SummaryRow(
                    label = "Sous-total",
                    value = upperSubtotal.toString(),
                    supportingText = "Somme actuelle de la section du haut",
                )
                SummaryRow(
                    label = "Seuil bonus",
                    value = uiState.settings.upperBonusThreshold.toString(),
                    supportingText = "Le bonus démarre à partir de ce total",
                )
                SummaryRow(
                    label = "Valeur bonus",
                    value = uiState.settings.upperBonusValue.toString(),
                    supportingText = if (uiState.settings.isUpperBonusEnabled) {
                        "Valeur ajoutée une fois le seuil atteint"
                    } else {
                        "Bonus désactivé dans cette partie"
                    },
                )
                SummaryRow(
                    label = "Total table mineure",
                    value = upperTotal.toString(),
                    supportingText = if (uiState.settings.isUpperBonusEnabled) {
                        bonusStatusText(
                            subtotal = upperSubtotal,
                            settings = uiState.settings,
                        )
                    } else {
                        "Calcul sans bonus"
                    },
                    emphasize = true,
                )
            }

            ScoreSection(
                title = "Combinaisons",
                subtitle = "Brelan, carré, full, suites, Yams et variantes fixes.",
                icon = YamsIcons.Timeline,
                accentColor = YamsTheme.colors.brown,
                trailingValue = lowerTotal.toString(),
            ) {
                lowerRows.forEachIndexed { index, row ->
                    ScoreRow(
                        row = row,
                        value = selectedPlayer.scores[row.key],
                        isEditable = isEditable,
                    )
                    if (index < lowerRows.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surface)
                    }
                }
            }

            if (customRows.isNotEmpty()) {
                ScoreSection(
                    title = "Règles custom",
                    subtitle = "Affichées seulement si la partie utilise des variantes maison.",
                    icon = YamsIcons.Tune,
                    accentColor = MaterialTheme.colorScheme.tertiary,
                    trailingValue = customTotal.toString(),
                ) {
                    customRows.forEachIndexed { index, row ->
                        ScoreRow(
                            row = row,
                            value = selectedPlayer.scores[row.key],
                            isEditable = isEditable,
                        )
                        if (index < customRows.lastIndex) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.surface)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreSheetOverviewCard(
    selectedPlayer: GamePlayPlayerSheet,
    currentTurnPlayer: GamePlayPlayerSheet,
    isEditable: Boolean,
    overallTotal: Int,
    selectedIndex: Int,
    playerCount: Int,
    onPreviousPlayer: () -> Unit,
    onNextPlayer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TurnStatusPill(
                    icon = YamsIcons.PersonRaisedHand,
                    label = "Tour en cours",
                    value = currentTurnPlayer.user.name,
                    modifier = Modifier.weight(1f),
                )
                ScorePill(
                    value = "$overallTotal",
                    suffix = "pts",
                    emphasize = true,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PlayerPagerButton(
                    onClick = onPreviousPlayer,
                    icon = YamsIcons.ChevronLeft,
                    contentDescription = "Voir la feuille précédente",
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isEditable) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isEditable) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        }
                    ),
                    modifier = Modifier.weight(1f),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        PlayerInitialBadge(
                            user = selectedPlayer.user,
                            highlight = isEditable,
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = selectedPlayer.user.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            if (isEditable) {
                                Icon(
                                    imageVector = YamsIcons.Crown,
                                    contentDescription = "Joueur actif",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                        Text(
                            text = if (isEditable) {
                                "Feuille active"
                            } else {
                                "Lecture seule"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isEditable) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                        Text(
                            text = "Joueur ${selectedIndex + 1}/$playerCount",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                PlayerPagerButton(
                    onClick = onNextPlayer,
                    icon = YamsIcons.ChevronRight,
                    contentDescription = "Voir la feuille suivante",
                )
            }

            Text(
                text = if (isEditable) {
                    "Tu peux utiliser cette feuille pour saisir les points du joueur en cours."
                } else {
                    "La feuille de ${selectedPlayer.user.name} reste visible, mais elle n'est pas modifiable pendant le tour de ${currentTurnPlayer.user.name}."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TurnStatusPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = YamsTheme.colors.gold.copy(alpha = 0.18f),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = YamsTheme.colors.brown,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = YamsTheme.colors.brown,
                fontWeight = FontWeight.SemiBold,
            )
        }
        }
    }
}

@Composable
private fun PlayerPagerButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = modifier.size(44.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
        )
    }
}

@Composable
private fun PlayerInitialBadge(
    user: User,
    highlight: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                if (highlight) {
                    YamsTheme.colors.gold.copy(alpha = 0.22f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = user.name.take(1).uppercase(),
            style = MaterialTheme.typography.titleMedium,
            color = if (highlight) {
                YamsTheme.colors.brown
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ScoreSection(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    trailingValue: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SectionAccentIcon(
                            icon = icon,
                            accentColor = accentColor,
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
                ScorePill(
                    value = trailingValue,
                    suffix = "pts",
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = content,
            )
        }
    }
}

@Composable
private fun SectionAccentIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(
                brush = Brush.linearGradient(
                    listOf(accentColor, accentColor.copy(alpha = 0.7f))
                ),
                shape = RoundedCornerShape(14.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun ScoreRow(
    row: ScoreRowUi,
    value: Int?,
    isEditable: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            row.badge?.let {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.width(40.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = row.label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = row.supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        ScorePill(
            value = value?.toString() ?: if (isEditable) "..." else "--",
            suffix = null,
            emphasize = isEditable && value != null,
            muted = !isEditable && value == null,
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    supportingText: String,
    emphasize: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = label,
                style = if (emphasize) {
                    MaterialTheme.typography.titleSmall
                } else {
                    MaterialTheme.typography.bodyMedium
                },
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        ScorePill(
            value = value,
            suffix = null,
            emphasize = emphasize,
        )
    }
}

@Composable
private fun ScorePill(
    value: String,
    suffix: String?,
    emphasize: Boolean = false,
    muted: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = when {
            emphasize -> YamsTheme.colors.gold.copy(alpha = 0.2f)
            muted -> MaterialTheme.colorScheme.surface
            else -> MaterialTheme.colorScheme.background
        },
        border = BorderStroke(
            width = 1.dp,
            color = when {
                emphasize -> YamsTheme.colors.gold.copy(alpha = 0.35f)
                muted -> MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
            }
        ),
        modifier = modifier.widthIn(min = 68.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = when {
                    emphasize -> YamsTheme.colors.brown
                    muted -> MaterialTheme.colorScheme.onSurfaceVariant
                    else -> MaterialTheme.colorScheme.onSurface
                },
            )
            if (suffix != null) {
                Text(
                    text = suffix,
                    style = MaterialTheme.typography.labelMedium,
                    color = when {
                        emphasize -> YamsTheme.colors.brown
                        muted -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}

private fun buildUpperScoreRows(): List<ScoreRowUi> = listOf(
    ScoreRowUi(key = ScoreKey.ONES, badge = "1", label = "As", supportingText = "Somme des 1"),
    ScoreRowUi(key = ScoreKey.TWOS, badge = "2", label = "Deux", supportingText = "Somme des 2"),
    ScoreRowUi(key = ScoreKey.THREES, badge = "3", label = "Trois", supportingText = "Somme des 3"),
    ScoreRowUi(key = ScoreKey.FOURS, badge = "4", label = "Quatre", supportingText = "Somme des 4"),
    ScoreRowUi(key = ScoreKey.FIVES, badge = "5", label = "Cinq", supportingText = "Somme des 5"),
    ScoreRowUi(key = ScoreKey.SIXES, badge = "6", label = "Six", supportingText = "Somme des 6"),
)

private fun buildMainScoreRows(settings: GameSettings): List<ScoreRowUi> = buildList {
    if (settings.isThreeOfAKindEnabled) {
        add(
            ScoreRowUi(
                key = ScoreKey.THREE_OF_A_KIND,
                label = "Brelan",
                supportingText = scoringDescription(
                    scoring = settings.threeOfAKindScoring,
                    fixedValue = settings.threeOfAKindValue,
                ),
            )
        )
    }
    if (settings.isFourOfAKindEnabled) {
        add(
            ScoreRowUi(
                key = ScoreKey.FOUR_OF_A_KIND,
                label = "Carré",
                supportingText = scoringDescription(
                    scoring = settings.fourOfAKindScoring,
                    fixedValue = settings.fourOfAKindValue,
                ),
            )
        )
    }
    if (settings.isFullHouseEnabled) {
        add(
            ScoreRowUi(
                key = ScoreKey.FULL_HOUSE,
                label = "Full",
                supportingText = "${settings.fullHouseValue} pts fixes",
            )
        )
    }
    if (settings.isSmallStraightEnabled) {
        add(
            ScoreRowUi(
                key = ScoreKey.SMALL_STRAIGHT,
                label = "Petite suite",
                supportingText = "${settings.smallStraightValue ?: 0} pts fixes",
            )
        )
    }
    if (settings.isLargeStraightEnabled) {
        add(
            ScoreRowUi(
                key = ScoreKey.LARGE_STRAIGHT,
                label = "Grande suite",
                supportingText = "${settings.largeStraightValue ?: 0} pts fixes",
            )
        )
    }
    if (settings.isFiveOfAKindEnabled) {
        add(
            ScoreRowUi(
                key = ScoreKey.FIVE_OF_A_KIND,
                label = "Yams",
                supportingText = "${settings.fiveOfAKindValue} pts fixes",
            )
        )
    }
    if (settings.isExtraFiveOfAKindEnabled && settings.extraFiveOfAKindValue != null) {
        add(
            ScoreRowUi(
                key = ScoreKey.EXTRA_FIVE_OF_A_KIND,
                label = "Yams bonus",
                supportingText = "${settings.extraFiveOfAKindValue} pts supplémentaires",
            )
        )
    }
    if (settings.isChanceEnabled) {
        add(
            ScoreRowUi(
                key = ScoreKey.CHANCE,
                label = "Chance",
                supportingText = scoringDescription(
                    scoring = settings.chanceValue,
                    fixedValue = null,
                ),
            )
        )
    }
}

private fun buildCustomScoreRows(settings: GameSettings): List<ScoreRowUi> {
    if (!settings.areCustomRulesEnabled) return emptyList()
    return settings.customGameSettings
        .filter { it.isEnabled }
        .map { rule ->
            ScoreRowUi(
                key = ScoreKey.custom(rule.title),
                label = rule.title,
                supportingText = rule.description ?: scoringDescription(
                    scoring = rule.scoring,
                    fixedValue = rule.value,
                ),
            )
        }
}

private fun scoringDescription(
    scoring: GameSettings.SettingsScoring?,
    fixedValue: Int?,
): String = when (scoring) {
    GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE -> "Somme des 5 dés"
    GameSettings.SettingsScoring.SUM_MATCHING_THREE -> "Somme des 3 dés identiques"
    GameSettings.SettingsScoring.SUM_MATCHING_FOUR -> "Somme des 4 dés identiques"
    GameSettings.SettingsScoring.FIXED,
    GameSettings.SettingsScoring.FIXED_CUSTOM -> "${fixedValue ?: 0} pts fixes"
    null -> "Score défini par la règle"
}

private fun upperBonus(subtotal: Int, settings: GameSettings): Int {
    if (!settings.isUpperBonusEnabled) return 0
    return if (subtotal >= settings.upperBonusThreshold) settings.upperBonusValue else 0
}

private fun bonusStatusText(
    subtotal: Int,
    settings: GameSettings,
): String {
    val bonus = upperBonus(subtotal, settings)
    if (bonus > 0) return "Bonus appliqué: +$bonus pts"
    val remaining = (settings.upperBonusThreshold - subtotal).coerceAtLeast(0)
    return "Encore $remaining pts pour débloquer le bonus"
}

private fun GamePlayPlayerSheet.sumOf(rows: List<ScoreRowUi>): Int =
    rows.sumOf { scores[it.key] ?: 0 }

private fun gamePlayMockUiState(): GamePlayMockUiState {
    val settings = GameSettings.CustomSettings(
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
    )

    return GamePlayMockUiState(
        settings = settings,
        currentTurnPlayerIndex = 1,
        players = listOf(
            GamePlayPlayerSheet(
                user = UserMocks.generate("Maxime"),
                scores = mapOf(
                    ScoreKey.ONES to 2,
                    ScoreKey.TWOS to 6,
                    ScoreKey.THREES to 9,
                    ScoreKey.FOURS to null,
                    ScoreKey.FIVES to 10,
                    ScoreKey.SIXES to 12,
                    ScoreKey.THREE_OF_A_KIND to 18,
                    ScoreKey.FOUR_OF_A_KIND to null,
                    ScoreKey.FULL_HOUSE to 25,
                    ScoreKey.SMALL_STRAIGHT to 30,
                    ScoreKey.LARGE_STRAIGHT to null,
                    ScoreKey.FIVE_OF_A_KIND to null,
                    ScoreKey.EXTRA_FIVE_OF_A_KIND to null,
                    ScoreKey.CHANCE to 21,
                    ScoreKey.custom("Double paire") to 15,
                    ScoreKey.custom("Tour du roi") to null,
                ),
            ),
            GamePlayPlayerSheet(
                user = UserMocks.generate("Lina"),
                scores = mapOf(
                    ScoreKey.ONES to 3,
                    ScoreKey.TWOS to 6,
                    ScoreKey.THREES to 9,
                    ScoreKey.FOURS to 16,
                    ScoreKey.FIVES to 20,
                    ScoreKey.SIXES to 24,
                    ScoreKey.THREE_OF_A_KIND to 19,
                    ScoreKey.FOUR_OF_A_KIND to 24,
                    ScoreKey.FULL_HOUSE to null,
                    ScoreKey.SMALL_STRAIGHT to 30,
                    ScoreKey.LARGE_STRAIGHT to 40,
                    ScoreKey.FIVE_OF_A_KIND to 50,
                    ScoreKey.EXTRA_FIVE_OF_A_KIND to null,
                    ScoreKey.CHANCE to null,
                    ScoreKey.custom("Double paire") to null,
                    ScoreKey.custom("Tour du roi") to 35,
                ),
            ),
            GamePlayPlayerSheet(
                user = UserMocks.generate("Noa"),
                scores = mapOf(
                    ScoreKey.ONES to null,
                    ScoreKey.TWOS to 2,
                    ScoreKey.THREES to 9,
                    ScoreKey.FOURS to 8,
                    ScoreKey.FIVES to 15,
                    ScoreKey.SIXES to null,
                    ScoreKey.THREE_OF_A_KIND to null,
                    ScoreKey.FOUR_OF_A_KIND to null,
                    ScoreKey.FULL_HOUSE to 25,
                    ScoreKey.SMALL_STRAIGHT to null,
                    ScoreKey.LARGE_STRAIGHT to null,
                    ScoreKey.FIVE_OF_A_KIND to null,
                    ScoreKey.EXTRA_FIVE_OF_A_KIND to null,
                    ScoreKey.CHANCE to 23,
                    ScoreKey.custom("Double paire") to 15,
                    ScoreKey.custom("Tour du roi") to null,
                ),
            ),
        ),
    )
}

private data class GamePlayMockUiState(
    val settings: GameSettings,
    val players: List<GamePlayPlayerSheet>,
    val currentTurnPlayerIndex: Int,
)

private data class GamePlayPlayerSheet(
    val user: User,
    val scores: Map<String, Int?>,
)

private data class ScoreRowUi(
    val key: String,
    val label: String,
    val supportingText: String,
    val badge: String? = null,
)

private object ScoreKey {
    const val ONES: String = "ones"
    const val TWOS: String = "twos"
    const val THREES: String = "threes"
    const val FOURS: String = "fours"
    const val FIVES: String = "fives"
    const val SIXES: String = "sixes"
    const val THREE_OF_A_KIND: String = "three_of_a_kind"
    const val FOUR_OF_A_KIND: String = "four_of_a_kind"
    const val FULL_HOUSE: String = "full_house"
    const val SMALL_STRAIGHT: String = "small_straight"
    const val LARGE_STRAIGHT: String = "large_straight"
    const val FIVE_OF_A_KIND: String = "five_of_a_kind"
    const val EXTRA_FIVE_OF_A_KIND: String = "extra_five_of_a_kind"
    const val CHANCE: String = "chance"

    fun custom(title: String): String = "custom_${title.lowercase().replace(" ", "_")}"
}

@Preview
@Composable
private fun GamePlayScreenPreview() {
    YamsTheme {
        GamePlayScreen()
    }
}

@Preview
@Composable
private fun GamePlayScreenReadOnlyPreview() {
    YamsTheme {
        GamePlayScreen(initialSelectedPlayerIndex = 0)
    }
}

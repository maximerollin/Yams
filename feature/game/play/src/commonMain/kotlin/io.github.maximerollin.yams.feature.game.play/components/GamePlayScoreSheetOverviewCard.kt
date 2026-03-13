package io.github.maximerollin.yams.feature.game.play.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.icon.ChevronLeft
import io.github.maximerollin.yams.core.designsystem.icon.ChevronRight
import io.github.maximerollin.yams.core.designsystem.icon.Crown
import io.github.maximerollin.yams.core.designsystem.icon.PersonRaisedHand
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.model.User
import io.github.maximerollin.yams.feature.game.play.model.GamePlayColumnSummary
import io.github.maximerollin.yams.feature.game.play.model.PlayerState

@Composable
internal fun GamePlayScoreSheetOverviewCard(
    selectedPlayer: PlayerState,
    currentTurnPlayer: PlayerState,
    columnSummaries: List<GamePlayColumnSummary>,
    showColumnSummaries: Boolean,
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
                AnimatedContent(
                    targetState = currentTurnPlayer.player.user,
                    modifier = Modifier.weight(1f),
                    transitionSpec = {
                        (slideInVertically { fullHeight -> fullHeight / 2 } + fadeIn())
                            .togetherWith(slideOutVertically { fullHeight -> -fullHeight / 2 } + fadeOut())
                    },
                    label = "currentTurnPlayer",
                ) { currentUser ->
                    TurnStatusPill(
                        icon = YamsIcons.PersonRaisedHand,
                        label = "Tour en cours",
                        value = currentUser.name,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                ScorePill(
                    value = overallTotal.toString(),
                    suffix = "pts",
                    emphasize = true,
                )
            }

            if (showColumnSummaries) {
                ColumnSummaryRow(columnSummaries = columnSummaries)
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

                AnimatedContent(
                    targetState = selectedPlayer,
                    modifier = Modifier.weight(1f),
                    transitionSpec = {
                        (slideInVertically { fullHeight -> fullHeight / 3 } + fadeIn())
                            .togetherWith(slideOutVertically { fullHeight -> -fullHeight / 3 } + fadeOut())
                    },
                    contentKey = { playerState -> playerState.player.userId },
                    label = "selectedPlayerSheet",
                ) { animatedSelectedPlayer ->
                    val isAnimatedSelectedPlayerActive =
                        animatedSelectedPlayer.player.userId == currentTurnPlayer.player.userId

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isAnimatedSelectedPlayerActive) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isAnimatedSelectedPlayerActive) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                            } else {
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            }
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            PlayerInitialBadge(
                                user = animatedSelectedPlayer.player.user,
                                highlight = isAnimatedSelectedPlayerActive,
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = animatedSelectedPlayer.player.user.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                if (isAnimatedSelectedPlayerActive) {
                                    Icon(
                                        imageVector = YamsIcons.Crown,
                                        contentDescription = "Joueur actif",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            }
                            Text(
                                text = if (isAnimatedSelectedPlayerActive) {
                                    "Feuille active"
                                } else {
                                    "Lecture seule"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isAnimatedSelectedPlayerActive) {
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
                }

                PlayerPagerButton(
                    onClick = onNextPlayer,
                    icon = YamsIcons.ChevronRight,
                    contentDescription = "Voir la feuille suivante",
                )
            }

            Text(
                text = if (isEditable) {
                    if (showColumnSummaries) {
                        "Tu peux remplir n'importe quelle case vide, dans la colonne que tu veux, sans ordre imposé."
                    } else {
                        "Tu peux utiliser cette feuille pour saisir les points du joueur en cours."
                    }
                } else {
                    "La feuille de ${selectedPlayer.player.user.name} reste visible, mais elle n'est pas modifiable pendant le tour de ${currentTurnPlayer.player.user.name}."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ColumnSummaryRow(
    columnSummaries: List<GamePlayColumnSummary>,
    modifier: Modifier = Modifier,
) {
    val useInlineLayout = columnSummaries.size == 2
    val scrollState = rememberScrollState()

    Row(
        modifier = if (useInlineLayout) modifier.fillMaxWidth() else modifier.horizontalScroll(
            scrollState
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        columnSummaries.forEach { summary ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
                ),
                modifier = if (useInlineLayout) Modifier.weight(1f) else Modifier,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "Col. ${summary.columnIndex + 1}",
                        style = MaterialTheme.typography.labelLarge,
                        color = YamsTheme.colors.brown,
                    )
                    Text(
                        text = "${summary.filledCells}/${summary.totalCells} cases",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "${summary.totalScore} pts",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun TurnStatusPill(
    icon: ImageVector,
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
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.FilledTonalIconButton(
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

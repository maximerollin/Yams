package io.github.maximerollin.yams.feature.game.play.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.maximerollin.yams.core.designsystem.icon.ChevronLeft
import io.github.maximerollin.yams.core.designsystem.icon.ChevronRight
import io.github.maximerollin.yams.core.designsystem.icon.Crown
import io.github.maximerollin.yams.core.designsystem.icon.PersonRaisedHand
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.model.User
import io.github.maximerollin.yams.core.ui.utils.appAvatarFor
import io.github.maximerollin.yams.feature.game.play.model.GamePlayColumnSummary
import io.github.maximerollin.yams.feature.game.play.model.PlayerState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import yams.feature.game.play.generated.resources.*

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
                        (slideInHorizontally(
                            animationSpec = tween(280),
                            initialOffsetX = { fullWidth -> fullWidth },
                        ) + fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.94f))
                            .togetherWith(
                                slideOutHorizontally(
                                    animationSpec = tween(220),
                                    targetOffsetX = { fullWidth -> -fullWidth },
                                ) + fadeOut(animationSpec = tween(180)) + scaleOut(targetScale = 0.94f)
                            )
                    },
                    contentKey = { user -> user.id },
                    label = "currentTurnPlayer",
                ) { currentUser ->
                    TurnStatusPill(
                        icon = YamsIcons.PersonRaisedHand,
                        label = stringResource(Res.string.play_turn_current),
                        user = currentUser,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                ScorePill(
                    value = overallTotal.toString(),
                    suffix = stringResource(Res.string.play_points_suffix),
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
                    contentDescription = stringResource(Res.string.play_previous_sheet_cd),
                )

                AnimatedContent(
                    targetState = selectedPlayer,
                    modifier = Modifier.weight(1f),
                    transitionSpec = {
                        (slideInVertically(
                            animationSpec = tween(260),
                            initialOffsetY = { fullHeight -> fullHeight },
                        ) + fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.96f))
                            .togetherWith(
                                slideOutVertically(
                                    animationSpec = tween(220),
                                    targetOffsetY = { fullHeight -> -fullHeight },
                                ) + fadeOut(animationSpec = tween(180)) + scaleOut(targetScale = 0.96f)
                            )
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
                                        contentDescription = stringResource(Res.string.play_active_player_cd),
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            }
                            Text(
                                text = if (isAnimatedSelectedPlayerActive) {
                                    stringResource(Res.string.play_active_sheet)
                                } else {
                                    stringResource(Res.string.play_read_only)
                                },
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isAnimatedSelectedPlayerActive) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                            Text(
                                text = stringResource(
                                    Res.string.play_player_position,
                                    selectedIndex + 1,
                                    playerCount,
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                PlayerPagerButton(
                    onClick = onNextPlayer,
                    icon = YamsIcons.ChevronRight,
                    contentDescription = stringResource(Res.string.play_next_sheet_cd),
                )
            }

            Text(
                text = if (isEditable) {
                    if (showColumnSummaries) {
                        stringResource(Res.string.play_editable_multi_help)
                    } else {
                        stringResource(Res.string.play_editable_single_help)
                    }
                } else {
                    stringResource(
                        Res.string.play_read_only_help,
                        selectedPlayer.player.user.name,
                        currentTurnPlayer.player.user.name,
                    )
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
                        text = stringResource(
                            Res.string.play_column_label,
                            summary.columnIndex + 1,
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = YamsTheme.colors.brown,
                    )
                    Text(
                        text = stringResource(
                            Res.string.play_filled_cells,
                            summary.filledCells,
                            summary.totalCells,
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = stringResource(Res.string.play_points_value, summary.totalScore),
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
    user: User,
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
            PlayerAvatar(user = user, size = 42.dp)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = YamsTheme.colors.brown,
                    )
                }
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = YamsTheme.colors.brown,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun PlayerAvatar(
    user: User,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        val fallbackAvatar = painterResource(appAvatarFor(user.name))
        AsyncImage(
            model = user.avatar,
            contentDescription = stringResource(Res.string.play_current_player_avatar_cd, user.name),
            placeholder = fallbackAvatar,
            error = fallbackAvatar,
            fallback = fallbackAvatar,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .padding(2.dp)
                .clip(CircleShape),
        )
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

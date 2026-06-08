@file:OptIn(kotlin.time.ExperimentalTime::class)

package io.github.maximerollin.yams.feature.user.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.maximerollin.yams.core.designsystem.icon.ChevronRight
import io.github.maximerollin.yams.core.designsystem.icon.History
import io.github.maximerollin.yams.core.designsystem.icon.Trophy
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.model.User
import io.github.vinceglb.filekit.PlatformFile
import org.jetbrains.compose.resources.stringResource
import yams.feature.user.common.generated.resources.Res
import yams.feature.user.common.generated.resources.common_avatar_cd
import yams.feature.user.common.generated.resources.common_average_score
import yams.feature.user.common.generated.resources.common_game_players_many
import yams.feature.user.common.generated.resources.common_game_players_one
import yams.feature.user.common.generated.resources.common_games
import yams.feature.user.common.generated.resources.common_record
import yams.feature.user.common.generated.resources.common_record_score
import yams.feature.user.common.generated.resources.common_score_points
import yams.feature.user.common.generated.resources.common_stats_subtitle
import yams.feature.user.common.generated.resources.common_stats_title
import yams.feature.user.common.generated.resources.common_victories
import yams.feature.user.common.generated.resources.common_winner_score
import yams.feature.user.common.generated.resources.common_yams
import yams.feature.user.common.generated.resources.common_yams_per_game
import kotlin.time.Instant

@Composable
public fun LoadingState(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CircularProgressIndicator()
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
public fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = YamsIcons.History,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = YamsTheme.colors.brown,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
public fun UserAvatar(
    user: User,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
) {
    Avatar(
        name = user.name,
        avatar = user.avatar,
        modifier = modifier,
        size = size,
    )
}

@Composable
public fun Avatar(
    name: String,
    avatar: PlatformFile?,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        if (avatar != null) {
            AsyncImage(
                model = avatar,
                contentDescription = stringResource(Res.string.common_avatar_cd, name),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .clip(CircleShape),
            )
        } else {
            Text(
                text = name.initial(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
public fun HomeStatsCard(
    stats: HomeStatsUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.common_stats_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = stringResource(Res.string.common_stats_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f),
                    )
                }
                Icon(
                    imageVector = YamsIcons.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatTile(
                        label = stringResource(Res.string.common_games),
                        value = stats.gamesPlayed.toString(),
                        modifier = Modifier.weight(1f),
                    )
                    StatTile(
                        label = stringResource(Res.string.common_yams),
                        value = stats.totalYams.toString(),
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatTile(
                        label = stringResource(Res.string.common_yams_per_game),
                        value = stats.averageYamsPerGame.formatOneDecimal(),
                        modifier = Modifier.weight(1f),
                    )
                    StatTile(
                        label = stringResource(Res.string.common_record),
                        value = recordLabel(stats),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
public fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = YamsTheme.colors.brown,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (supportingText != null) {
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
public fun GameHistoryCard(
    game: GameSummaryUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WinnerAvatar(
                winner = game.winner,
                modifier = Modifier.size(66.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(
                                if (game.totalPlayers > 1) {
                                    Res.string.common_game_players_many
                                } else {
                                    Res.string.common_game_players_one
                                },
                                game.finishedAt.toDateLabel(),
                                game.totalPlayers,
                            ),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Icon(
                        imageVector = YamsIcons.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy((-8).dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    game.players.take(7).forEach { player ->
                        Avatar(
                            name = player.name,
                            avatar = player.avatar,
                            size = 30.dp,
                        )
                    }
                    val remainingCount = game.players.size - 7
                    if (remainingCount > 0) {
                        RemainingPlayersBadge(remainingCount = remainingCount)
                    }
                }

                val winner = game.winner
                if (winner != null) {
                    Text(
                        text = stringResource(
                            Res.string.common_winner_score,
                            winner.name,
                            winner.score,
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = YamsTheme.colors.brown,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun WinnerAvatar(
    winner: PlayerSummaryUiState?,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Avatar(
            name = winner?.name ?: "?",
            avatar = winner?.avatar,
            modifier = Modifier.fillMaxSize(),
        )
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(26.dp),
            shape = CircleShape,
            color = YamsTheme.colors.firstPlace,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = YamsIcons.Trophy,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = YamsTheme.colors.onFirstPlace,
                )
            }
        }
    }
}

@Composable
private fun RemainingPlayersBadge(
    remainingCount: Int,
) {
    Surface(
        modifier = Modifier.size(30.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "+$remainingCount",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
public fun PlayerAvatarRow(
    players: List<PlayerSummaryUiState>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy((-8).dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        players.take(8).forEach { player ->
            Avatar(
                name = player.name,
                avatar = player.avatar,
                size = 32.dp,
            )
        }
        val remainingCount = players.size - 8
        if (remainingCount > 0) {
            RemainingPlayersBadge(remainingCount)
        }
    }
}

@Composable
public fun CompactPlayerScore(
    player: PlayerSummaryUiState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(
            name = player.name,
            avatar = player.avatar,
            size = 28.dp,
        )
        Text(
            text = player.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stringResource(Res.string.common_score_points, player.score),
            style = MaterialTheme.typography.labelMedium,
            color = YamsTheme.colors.brown,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

@Composable
public fun ProfileStatRow(
    stats: UserStatsUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                label = stringResource(Res.string.common_victories),
                value = stats.victories.toString(),
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = stringResource(Res.string.common_games),
                value = stats.gamesPlayed.toString(),
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                label = stringResource(Res.string.common_yams),
                value = stats.totalYams.toString(),
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = stringResource(Res.string.common_average_score),
                value = stats.averageScore.formatOneDecimal(),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private fun String.initial(): String =
    trim().firstOrNull()?.uppercase() ?: "?"

@Composable
private fun recordLabel(stats: HomeStatsUiState): String {
    if (stats.highestScore <= 0) return "-"

    val playerName = stats.highestScorePlayerName
    return if (playerName != null) {
        stringResource(Res.string.common_record_score, playerName, stats.highestScore)
    } else {
        stringResource(Res.string.common_score_points, stats.highestScore)
    }
}

private fun Instant.toDateLabel(): String =
    toString().take(10)

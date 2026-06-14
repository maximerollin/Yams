package io.github.maximerollin.yams.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.maximerollin.yams.core.designsystem.component.AppTopBar
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimaryButton
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimarySmallButton
import io.github.maximerollin.yams.core.designsystem.icon.Delete
import io.github.maximerollin.yams.core.designsystem.icon.RocketLaunch
import io.github.maximerollin.yams.core.designsystem.icon.Timer
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.preview.YamsStoreScreenshotPreviews
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.designsystem.util.IconInfo
import io.github.maximerollin.yams.core.mocks.UserMocks
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.feature.user.common.EmptyState
import io.github.maximerollin.yams.feature.user.common.Avatar
import io.github.maximerollin.yams.feature.user.common.GameSummaryUiState
import io.github.maximerollin.yams.feature.user.common.gameHistoryItems
import io.github.maximerollin.yams.feature.user.common.HomeStatsUiState
import io.github.maximerollin.yams.feature.user.common.HomeStatsCard
import io.github.maximerollin.yams.feature.user.common.LoadingState
import io.github.maximerollin.yams.feature.user.common.PlayerSummaryUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import yams.feature.home.generated.resources.*
import yams.core.ui.generated.resources.app_icon
import yams.core.ui.generated.resources.Res as CoreUiRes

@Composable
internal fun HomeRoute(
    onNavigateToGameCreation: () -> Unit,
    onNavigateToGamePlay: (GameId) -> Unit,
    onNavigateToGameResult: (GameId) -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onNavigateToGameCreation = onNavigateToGameCreation,
        onNavigateToGamePlay = onNavigateToGamePlay,
        onNavigateToGameResult = onNavigateToGameResult,
        onNavigateToUsers = onNavigateToUsers,
        onNavigateToPaywall = onNavigateToPaywall,
        onAbandonGame = viewModel::abandonGame,
        modifier = modifier,
    )
}

@Composable
internal fun HomeScreen(
    uiState: HomeUiState,
    onNavigateToGameCreation: () -> Unit,
    onNavigateToGamePlay: (GameId) -> Unit,
    onNavigateToGameResult: (GameId) -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToPaywall: () -> Unit,
    onAbandonGame: (GameId) -> Unit,
    modifier: Modifier = Modifier,
) {
    var pendingAbandonGameId by remember { mutableStateOf<GameId?>(null) }
    val activeGame = (uiState as? HomeUiState.Success)?.activeGame

    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            AppTopBar(
                modifier = Modifier.statusBarsPadding(),
                isDividerVisible = false,
                center = {
                    HomeBrandTitle()
                },
            )
        },
        bottomBar = {
            HomeBottomBar(
                activeGame = activeGame,
                onNavigateToGameCreation = onNavigateToGameCreation,
                onNavigateToGamePlay = onNavigateToGamePlay,
                onAbandonGame = { pendingAbandonGameId = it },
            )
        },
    ) { innerPadding ->
        when (uiState) {
            HomeUiState.Loading -> LoadingState(
                message = stringResource(yams.feature.home.generated.resources.Res.string.home_loading),
                modifier = Modifier.padding(innerPadding),
            )

            is HomeUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        top = 16.dp,
                        end = 20.dp,
                        bottom = 20.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        HomeStatsCard(
                            stats = uiState.stats,
                            onClick = onNavigateToUsers,
                        )
                    }

                    item {
                        Text(
                            text = stringResource(yams.feature.home.generated.resources.Res.string.home_recent_games_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = YamsTheme.colors.brown,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    if (uiState.recentGames.isEmpty()) {
                        item {
                            EmptyState(
                                title = stringResource(yams.feature.home.generated.resources.Res.string.home_no_finished_games_title),
                                message = stringResource(yams.feature.home.generated.resources.Res.string.home_no_finished_games_message),
                            )
                        }
                    } else {
                        gameHistoryItems(
                            games = uiState.recentGames,
                            isPremium = uiState.isPremium,
                            onGameClick = onNavigateToGameResult,
                            onUnlockClick = onNavigateToPaywall,
                        )
                    }
                }
            }
        }
    }

    val gameIdToAbandon = pendingAbandonGameId
    if (gameIdToAbandon != null) {
        AbandonGameDialog(
            onDismiss = { pendingAbandonGameId = null },
            onConfirm = {
                pendingAbandonGameId = null
                onAbandonGame(gameIdToAbandon)
            },
        )
    }
}

@Composable
private fun HomeBrandTitle() {
    val appName = stringResource(yams.feature.home.generated.resources.Res.string.home_app_name)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(CoreUiRes.drawable.app_icon),
            contentDescription = stringResource(
                yams.feature.home.generated.resources.Res.string.home_logo_cd,
                appName,
            ),
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape),
        )
        Text(
            text = appName,
            modifier = Modifier.padding(start = 10.dp),
            style = MaterialTheme.typography.titleMedium,
            color = YamsTheme.colors.brown,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun HomeBottomBar(
    activeGame: ActiveGameUiState?,
    onNavigateToGameCreation: () -> Unit,
    onNavigateToGamePlay: (GameId) -> Unit,
    onAbandonGame: (GameId) -> Unit,
) {
    Surface(
        modifier = Modifier.zIndex(1f),
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (activeGame != null) {
                ActiveGameResumeCard(
                    activeGame = activeGame,
                    onResumeGame = onNavigateToGamePlay,
                    onAbandonGame = onAbandonGame,
                )
            }

            YamsPrimaryButton(
                onClick = onNavigateToGameCreation,
                text = stringResource(yams.feature.home.generated.resources.Res.string.home_new_game),
                icon = IconInfo(
                    vector = YamsIcons.RocketLaunch,
                    contentDescription = stringResource(
                        yams.feature.home.generated.resources.Res.string.home_create_game_cd,
                    ),
                ),
            )
        }
    }
}

@Composable
private fun ActiveGameResumeCard(
    activeGame: ActiveGameUiState,
    onResumeGame: (GameId) -> Unit,
    onAbandonGame: (GameId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = YamsIcons.Timer,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = YamsTheme.colors.brown,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.home_active_game_title),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stringResource(
                            if (activeGame.playerCount > 1) {
                                Res.string.home_active_game_players_many
                            } else {
                                Res.string.home_active_game_players_one
                            },
                            activeGame.playerCount,
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (activeGame.players.isNotEmpty()) {
                        ActiveGamePlayerRow(
                            players = activeGame.players,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = { onAbandonGame(activeGame.gameId) },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 36.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.home_abandon_game),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                YamsPrimarySmallButton(
                    onClick = { onResumeGame(activeGame.gameId) },
                    modifier = Modifier.weight(1f),
                    text = stringResource(Res.string.home_resume_game),
                )
            }
        }
    }
}

@Composable
private fun ActiveGamePlayerRow(
    players: List<ActiveGamePlayerUiState>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy((-8).dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            players.take(4).forEach { player ->
                Avatar(
                    name = player.name,
                    avatar = player.avatar,
                    size = 28.dp,
                )
            }
            val remainingCount = players.size - 4
            if (remainingCount > 0) {
                ActiveGameRemainingPlayersBadge(remainingCount = remainingCount)
            }
        }

        Text(
            text = players.joinToString(", ") { it.name },
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.84f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ActiveGameRemainingPlayersBadge(
    remainingCount: Int,
) {
    Surface(
        modifier = Modifier.size(28.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "+$remainingCount",
                style = MaterialTheme.typography.labelSmall,
                color = YamsTheme.colors.brown,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun AbandonGameDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(Res.string.home_abandon_title))
        },
        text = {
            Text(text = stringResource(Res.string.home_abandon_message))
        },
        icon = {
            Icon(
                imageVector = YamsIcons.Delete,
                contentDescription = null,
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(Res.string.home_abandon_cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(Res.string.home_abandon_confirm))
            }
        },
    )
}

@OptIn(ExperimentalTime::class)
@YamsStoreScreenshotPreviews
@Composable
private fun HomeScreenPreview() {
    HomeStoreScreenshotContent()
}

@OptIn(ExperimentalTime::class)
@Composable
public fun HomeStoreScreenshotContent() {
    YamsTheme {
        HomeScreen(
            uiState = previewHomeUiState(),
            onNavigateToGameCreation = {},
            onNavigateToGamePlay = {},
            onNavigateToGameResult = {},
            onNavigateToUsers = {},
            onNavigateToPaywall = {},
            onAbandonGame = {},
        )
    }
}

@OptIn(ExperimentalTime::class)
private fun previewHomeUiState(): HomeUiState.Success {
    val users = UserMocks.users
    val players = users.take(4).mapIndexed { index, user ->
        PlayerSummaryUiState(
            userId = user.id,
            name = user.name,
            avatar = user.avatar,
            rank = index + 1,
            score = listOf(286, 241, 218, 193)[index],
            yams = listOf(3, 2, 1, 0)[index],
            isWinner = index == 0,
        )
    }

    return HomeUiState.Success(
        stats = HomeStatsUiState(
            gamesPlayed = 18,
            totalYams = 37,
            averageYamsPerGame = 2.1f,
            highestScore = 286,
            highestScorePlayerName = users.first().name,
        ),
        recentGames = listOf(
            GameSummaryUiState(
                gameId = GameId("preview-finished-1"),
                gameNumber = 18,
                finishedAt = Clock.System.now(),
                photo = null,
                players = players,
                topPlayers = players.take(3),
                totalPlayers = players.size,
                totalYams = players.sumOf { it.yams },
                highestScore = players.maxOf { it.score },
            ),
            GameSummaryUiState(
                gameId = GameId("preview-finished-2"),
                gameNumber = 17,
                finishedAt = Clock.System.now(),
                photo = null,
                players = players.drop(1) + players.first(),
                topPlayers = players.drop(1).take(3),
                totalPlayers = players.size,
                totalYams = 5,
                highestScore = 251,
            ),
        ),
        activeGame = ActiveGameUiState(
            gameId = GameId("preview-active"),
            playerCount = users.size,
            players = users.map {
                ActiveGamePlayerUiState(
                    name = it.name,
                    avatar = it.avatar,
                )
            },
        ),
        isPremium = true,
    )
}

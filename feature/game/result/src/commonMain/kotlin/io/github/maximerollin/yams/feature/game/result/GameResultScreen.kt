package io.github.maximerollin.yams.feature.game.result

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import io.github.maximerollin.yams.core.designsystem.component.AppTopBar
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimaryButton
import io.github.maximerollin.yams.core.designsystem.component.YamsSecondaryButton
import io.github.maximerollin.yams.core.designsystem.icon.ChevronLeft
import io.github.maximerollin.yams.core.designsystem.icon.Trophy
import io.github.maximerollin.yams.core.designsystem.icon.Undo
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.preview.YamsStoreScreenshotPreviews
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.model.Game
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.core.mocks.UserMocks
import io.github.maximerollin.yams.core.ui.utils.appAvatarFor
import io.github.maximerollin.yams.data.game.model.Player
import io.github.maximerollin.yams.feature.game.result.model.GameResultPlayerUiState
import io.github.maximerollin.yams.feature.game.result.model.GameResultUiState
import io.github.vinceglb.confettikit.compose.ConfettiKit
import io.github.vinceglb.confettikit.core.Angle
import io.github.vinceglb.confettikit.core.Party
import io.github.vinceglb.confettikit.core.Position
import io.github.vinceglb.confettikit.core.Spread
import io.github.vinceglb.confettikit.core.emitter.Emitter
import kotlin.math.round
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import yams.feature.game.result.generated.resources.Res
import yams.feature.game.result.generated.resources.result_avatar_cd
import yams.feature.game.result.generated.resources.result_average_per_turn
import yams.feature.game.result.generated.resources.result_back_cd
import yams.feature.game.result.generated.resources.result_duration
import yams.feature.game.result.generated.resources.result_duration_hours_minutes
import yams.feature.game.result.generated.resources.result_duration_in_progress
import yams.feature.game.result.generated.resources.result_duration_less_than_minute
import yams.feature.game.result.generated.resources.result_duration_minutes
import yams.feature.game.result.generated.resources.result_empty
import yams.feature.game.result.generated.resources.result_finish_game
import yams.feature.game.result.generated.resources.result_game_finished
import yams.feature.game.result.generated.resources.result_loading
import yams.feature.game.result.generated.resources.result_podium
import yams.feature.game.result.generated.resources.result_points
import yams.feature.game.result.generated.resources.result_points_per_turn
import yams.feature.game.result.generated.resources.result_rank_first
import yams.feature.game.result.generated.resources.result_rank_other
import yams.feature.game.result.generated.resources.result_ranking
import yams.feature.game.result.generated.resources.result_shared_win
import yams.feature.game.result.generated.resources.result_title
import yams.feature.game.result.generated.resources.result_undo_last_move
import yams.feature.game.result.generated.resources.result_victories
import yams.feature.game.result.generated.resources.result_win_for_one
import yams.feature.game.result.generated.resources.result_win_for_two
import yams.feature.game.result.generated.resources.result_winner
import yams.feature.game.result.generated.resources.result_yam_count_many
import yams.feature.game.result.generated.resources.result_yam_count_one
import yams.feature.game.result.generated.resources.result_yams

private val GoldMedalColor = Color(0xFFD4AF37)
private val SilverMedalColor = Color(0xFF9EA3AA)
private val BronzeMedalColor = Color(0xFFB87333)

@Composable
internal fun GameResultRoute(
    gameId: GameId,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToGame: (GameId) -> Unit,
    onNavigateToUserProfile: (UserId) -> Unit,
    viewModel: GameResultViewModel = koinViewModel { parametersOf(gameId) },
) {
    val uiState by viewModel.gameResultUiState.collectAsStateWithLifecycle()
    val navigationTarget by viewModel.navigationTarget.collectAsStateWithLifecycle()
    val isActionInProgress by viewModel.isActionInProgress.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.checkInAppReview()
    }

    LaunchedEffect(navigationTarget) {
        when (navigationTarget) {
            GameResultNavigationTarget.GAME_PLAY -> {
                onNavigateToGame(gameId)
                viewModel.onNavigationHandled()
            }

            GameResultNavigationTarget.HOME -> {
                onNavigateHome()
                viewModel.onNavigationHandled()
            }

            null -> Unit
        }
    }

    GameResultScreen(
        uiState = uiState,
        isActionInProgress = isActionInProgress,
        onNavigateBack = onNavigateBack,
        onNavigateToUserProfile = onNavigateToUserProfile,
        onUndoLastMove = viewModel::onUndoLastMove,
        onFinishGame = viewModel::onFinishGame,
    )
}

@Composable
private fun GameResultScreen(
    uiState: GameResultUiState?,
    isActionInProgress: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToUserProfile: (UserId) -> Unit,
    onUndoLastMove: () -> Unit,
    onFinishGame: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val areActionsEnabled = uiState?.game is Game.GameInProgress && !isActionInProgress

    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            GameResultTopBar(onNavigateBack = onNavigateBack)
        },
        bottomBar = {
            if (uiState?.game is Game.GameInProgress) {
                GameResultBottomBar(
                    isEnabled = areActionsEnabled,
                    onUndoLastMove = onUndoLastMove,
                    onFinishGame = onFinishGame,
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            val resultState = uiState

            when {
                resultState == null -> {
                    GameResultMessageState(message = stringResource(Res.string.result_loading))
                }

                resultState.playerResults.isEmpty() -> {
                    GameResultMessageState(message = stringResource(Res.string.result_empty))
                }

                else -> {
                    val scrollState = rememberScrollState()
                    val winners = resultState.winnerResults

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        GameResultWinnerHeader(
                            winners = winners,
                            fallbackWinner = resultState.playerResults.first(),
                            onNavigateToUserProfile = onNavigateToUserProfile,
                        )

                        GameResultPodiumCard(
                            playerResults = resultState.playerResults,
                            onNavigateToUserProfile = onNavigateToUserProfile,
                        )

                        GameResultStatsCard(uiState = resultState)

                        Text(
                            text = stringResource(Res.string.result_ranking),
                            style = MaterialTheme.typography.titleMedium,
                            color = YamsTheme.colors.brown,
                            fontWeight = FontWeight.SemiBold,
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            resultState.playerResults.forEach { playerResult ->
                                GameResultPlayerCard(playerResult = playerResult)
                            }
                        }
                    }

                    GameResultConfetti(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
private fun GameResultTopBar(
    onNavigateBack: () -> Unit,
) {
    AppTopBar(
        modifier = Modifier.statusBarsPadding(),
        isDividerVisible = false,
        start = {
            io.github.maximerollin.yams.core.designsystem.component.AppIconButton(
                icon = YamsIcons.ChevronLeft,
                contentDescription = stringResource(Res.string.result_back_cd),
                onClick = onNavigateBack,
            )
        },
        center = {
            Text(
                text = stringResource(Res.string.result_title),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                color = YamsTheme.colors.brown,
                textAlign = TextAlign.Center,
            )
        },
    )
}

@Composable
private fun GameResultWinnerHeader(
    winners: List<GameResultPlayerUiState>,
    fallbackWinner: GameResultPlayerUiState,
    onNavigateToUserProfile: (UserId) -> Unit,
) {
    val displayedWinners = winners.ifEmpty { listOf(fallbackWinner) }
    val totalTurns = displayedWinners.sumOf(GameResultPlayerUiState::numberOfTurns)
    val averageScorePerTurn = if (totalTurns > 0) {
        displayedWinners.sumOf(GameResultPlayerUiState::score).toFloat() / totalTurns
    } else {
        0f
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                if (displayedWinners.size == 1) 0.dp else (-10).dp,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            displayedWinners.forEach { winner ->
                WinnerAvatarBadge(
                    playerResult = winner,
                    size = if (displayedWinners.size == 1) 108.dp else 82.dp,
                    onClick = { onNavigateToUserProfile(winner.player.userId) },
                )
            }
        }

        Text(
            text = winnerTitle(winners),
            style = MaterialTheme.typography.headlineSmall,
            color = YamsTheme.colors.brown,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            GameResultSummaryPill(
                label = stringResource(Res.string.result_victories),
                value = displayedWinners.sumOf(GameResultPlayerUiState::totalVictoryCount).toString(),
                modifier = Modifier.weight(1f),
            )
            GameResultSummaryPill(
                label = stringResource(Res.string.result_points_per_turn),
                value = averageScorePerTurn.format(1),
                modifier = Modifier.weight(1f),
            )
            GameResultSummaryPill(
                label = stringResource(Res.string.result_yams),
                value = displayedWinners.sumOf(GameResultPlayerUiState::numberOfFiveOfAKind).toString(),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun WinnerAvatarBadge(
    playerResult: GameResultPlayerUiState,
    size: Dp,
    onClick: () -> Unit,
) {
    Box(modifier = Modifier.size(size)) {
        PlayerAvatar(
            playerResult = playerResult,
            modifier = Modifier.fillMaxSize(),
            onClick = onClick,
        )
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(if (size > 90.dp) 34.dp else 30.dp),
            shape = CircleShape,
            color = YamsTheme.colors.firstPlace,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = YamsIcons.Trophy,
                    contentDescription = null,
                    modifier = Modifier.size(if (size > 90.dp) 20.dp else 17.dp),
                    tint = YamsTheme.colors.onFirstPlace,
                )
            }
        }
    }
}

@Composable
private fun GameResultPodiumCard(
    playerResults: List<GameResultPlayerUiState>,
    onNavigateToUserProfile: (UserId) -> Unit,
) {
    val podiumPlayers = playerResults.filter { it.rank <= 3 }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.result_podium),
                style = MaterialTheme.typography.titleMedium,
                color = YamsTheme.colors.brown,
                fontWeight = FontWeight.SemiBold,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                podiumPlayers.forEach { playerResult ->
                    PodiumStep(
                        playerResult = playerResult,
                        modifier = Modifier.weight(1f),
                        onClickAvatar = {
                            onNavigateToUserProfile(playerResult.player.userId)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun PodiumStep(
    playerResult: GameResultPlayerUiState,
    modifier: Modifier = Modifier,
    onClickAvatar: () -> Unit,
) {
    val color = medalColor(playerResult.rank)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PlayerAvatar(
            playerResult = playerResult,
            modifier = Modifier.size(52.dp),
            onClick = onClickAvatar,
        )
        Text(
            text = playerResult.player.name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(podiumStepHeight(playerResult.rank)),
            shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
            color = color.copy(alpha = 0.20f),
            border = BorderStroke(1.5.dp, color),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = medalEmoji(playerResult.rank),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(Res.string.result_points, playerResult.score),
                    style = MaterialTheme.typography.labelMedium,
                    color = YamsTheme.colors.brown,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun podiumStepHeight(rank: Int): Dp = when (rank) {
    1 -> 108.dp
    2 -> 92.dp
    3 -> 80.dp
    else -> 48.dp
}

@Composable
private fun RankingMedal(
    rank: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(44.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = medalEmoji(rank),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun GameResultStatsCard(
    uiState: GameResultUiState,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            GameResultSummaryPill(
                label = stringResource(Res.string.result_duration),
                value = durationLabel(uiState.game),
                modifier = Modifier.weight(1f),
            )
            GameResultSummaryPill(
                label = stringResource(Res.string.result_points_per_turn),
                value = uiState.averageScorePerTurn.format(1),
                modifier = Modifier.weight(1f),
            )
            GameResultSummaryPill(
                label = stringResource(Res.string.result_yams),
                value = uiState.totalYamCount.toString(),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PlayerAvatar(
    playerResult: GameResultPlayerUiState,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val avatarContentDescription = stringResource(Res.string.result_avatar_cd, playerResult.player.name)
    val avatarModifier = modifier
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primaryContainer)
        .let { baseModifier ->
            if (onClick != null) {
                baseModifier.clickable(
                    onClickLabel = avatarContentDescription,
                    onClick = onClick,
                )
            } else {
                baseModifier
            }
        }

    Box(
        modifier = avatarModifier,
        contentAlignment = Alignment.Center,
    ) {
        val avatar = playerResult.player.avatar
        val fallbackAvatar = painterResource(appAvatarFor(playerResult.player.name))
        AsyncImage(
            model = avatar,
            contentDescription = avatarContentDescription,
            placeholder = fallbackAvatar,
            error = fallbackAvatar,
            fallback = fallbackAvatar,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp)
                .clip(CircleShape),
        )
    }
}

@Composable
private fun GameResultSummaryPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = YamsTheme.colors.brown,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun GameResultPlayerCard(
    playerResult: GameResultPlayerUiState,
) {
    val shape = RoundedCornerShape(24.dp)
    val containerColor = if (playerResult.isWinner) {
        YamsTheme.colors.gold.copy(alpha = 0.14f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Surface(
        modifier = Modifier.then(
            if (playerResult.rank <= 3) {
                Modifier.border(
                    border = BorderStroke(1.5.dp, medalColor(playerResult.rank)),
                    shape = shape,
                )
            } else {
                Modifier
            }
        ),
        shape = shape,
        color = containerColor,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RankingMedal(rank = playerResult.rank)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = playerResult.player.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                )
                if (playerResult.isWinner) {
                    Text(
                        text = stringResource(Res.string.result_winner),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    GameResultMetaChip(
                        label = yamCountLabel(playerResult.numberOfFiveOfAKind),
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = stringResource(Res.string.result_points, playerResult.score),
                    style = MaterialTheme.typography.titleMedium,
                    color = YamsTheme.colors.brown,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = stringResource(
                        Res.string.result_average_per_turn,
                        playerResult.averageScorePerTurn.format(1),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun GameResultMetaChip(
    label: String,
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun GameResultBottomBar(
    isEnabled: Boolean,
    onUndoLastMove: () -> Unit,
    onFinishGame: () -> Unit,
) {
    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            YamsSecondaryButton(
                onClick = onUndoLastMove,
                modifier = Modifier.fillMaxWidth(),
                enabled = isEnabled,
            ) {
                Icon(
                    imageVector = YamsIcons.Undo,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = stringResource(Res.string.result_undo_last_move))
            }

            YamsPrimaryButton(
                onClick = onFinishGame,
                enabled = isEnabled,
                text = stringResource(Res.string.result_finish_game),
            )
        }
    }
}

@Composable
private fun GameResultConfetti(
    modifier: Modifier = Modifier,
) {
    var isVisible by remember { mutableStateOf(true) }
    val parties = remember { gameResultConfettiParties() }

    if (!isVisible) return

    ConfettiKit(
        modifier = modifier,
        parties = parties,
        onParticleSystemEnded = { _, activeSystems ->
            if (activeSystems == 0) {
                isVisible = false
            }
        },
    )
}

private fun gameResultConfettiParties(): List<Party> {
    val colors = listOf(0xd4af37, 0x50b788, 0x4d96ff, 0xff6b6b, 0xffc857)
    val rain = Party(
        speed = 0f,
        maxSpeed = 16f,
        damping = 0.92f,
        angle = Angle.BOTTOM,
        spread = Spread.ROUND,
        colors = colors,
        emitter = Emitter(duration = 3.seconds).perSecond(85),
        position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0)),
    )

    return listOf(
        Party(
            speed = 0f,
            maxSpeed = 32f,
            damping = 0.9f,
            spread = Spread.ROUND,
            colors = colors,
            emitter = Emitter(duration = 160.milliseconds).max(130),
            position = Position.Relative(0.5, 0.25),
        ),
        rain.copy(delay = 180),
    )
}

@Composable
private fun GameResultMessageState(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun winnerTitle(winners: List<GameResultPlayerUiState>): String = when (winners.size) {
    0 -> stringResource(Res.string.result_game_finished)
    1 -> stringResource(Res.string.result_win_for_one, winners.first().player.name)
    2 -> stringResource(
        Res.string.result_win_for_two,
        winners[0].player.name,
        winners[1].player.name,
    )
    else -> stringResource(Res.string.result_shared_win)
}

@Composable
private fun yamCountLabel(count: Int): String =
    stringResource(
        if (count == 1) Res.string.result_yam_count_one else Res.string.result_yam_count_many,
        count,
    )

@OptIn(ExperimentalTime::class)
@Composable
private fun durationLabel(game: Game): String = when (game) {
    is Game.GameFinished -> (game.finishedAt - game.startedAt).toLocalizedDurationLabel()
    is Game.GameInProgress -> stringResource(Res.string.result_duration_in_progress)
}

@Composable
private fun Duration.toLocalizedDurationLabel(): String {
    val totalMinutes = inWholeMinutes
    if (totalMinutes <= 0) return stringResource(Res.string.result_duration_less_than_minute)

    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) {
        stringResource(Res.string.result_duration_hours_minutes, hours, minutes)
    } else {
        stringResource(Res.string.result_duration_minutes, minutes)
    }
}

@Composable
private fun rankLabel(rank: Int): String =
    if (rank == 1) {
        stringResource(Res.string.result_rank_first)
    } else {
        stringResource(Res.string.result_rank_other, rank)
    }

@Composable
private fun medalEmoji(rank: Int): String = when (rank) {
    1 -> "🥇"
    2 -> "🥈"
    3 -> "🥉"
    else -> rankLabel(rank)
}

private fun medalColor(rank: Int): Color = when (rank) {
    1 -> GoldMedalColor
    2 -> SilverMedalColor
    3 -> BronzeMedalColor
    else -> Color.Transparent
}

private fun Float.format(decimals: Int): String {
    val factor = when (decimals) {
        0 -> 1f
        1 -> 10f
        2 -> 100f
        else -> 10f
    }
    return (round(this * factor) / factor).toString()
}

private fun String.initial(): String =
    trim().firstOrNull()?.uppercase() ?: "?"

@OptIn(ExperimentalTime::class)
@YamsStoreScreenshotPreviews
@Composable
private fun GameResultScreenPreview() {
    GameResultStoreScreenshotContent()
}

@OptIn(ExperimentalTime::class)
@Composable
public fun GameResultStoreScreenshotContent() {
    val gameId = GameId("preview-game")
    val users = UserMocks.users
    val game = Game.GameInProgress(
        id = gameId,
        settings = GameSettings.YamsSettings(),
        startedAt = Clock.System.now(),
    )

    val uiState = GameResultUiState(
        game = game,
        playerResults = listOf(
            GameResultPlayerUiState(
                player = Player(
                    userId = users[0].id,
                    name = users[0].name,
                    avatar = users[0].avatar,
                    gameId = gameId,
                    userIndex = 0,
                ),
                rank = 1,
                score = 268,
                numberOfTurns = 13,
                isWinner = true,
                numberOfFiveOfAKind = 2,
                totalVictoryCount = 8,
            ),
            GameResultPlayerUiState(
                player = Player(
                    userId = users[1].id,
                    name = users[1].name,
                    avatar = users[1].avatar,
                    gameId = gameId,
                    userIndex = 1,
                ),
                rank = 1,
                score = 268,
                numberOfTurns = 13,
                isWinner = true,
                numberOfFiveOfAKind = 1,
                totalVictoryCount = 4,
            ),
            GameResultPlayerUiState(
                player = Player(
                    userId = users[2].id,
                    name = users[2].name,
                    avatar = users[2].avatar,
                    gameId = gameId,
                    userIndex = 2,
                ),
                rank = 3,
                score = 219,
                numberOfTurns = 13,
                isWinner = false,
                numberOfFiveOfAKind = 0,
                totalVictoryCount = 2,
            ),
        ),
    )

    YamsTheme {
        GameResultScreen(
            uiState = uiState,
            isActionInProgress = false,
            onNavigateBack = {},
            onNavigateToUserProfile = {},
            onUndoLastMove = {},
            onFinishGame = {},
        )
    }
}

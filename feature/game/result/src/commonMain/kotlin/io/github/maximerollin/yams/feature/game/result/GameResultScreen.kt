package io.github.maximerollin.yams.feature.game.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.maximerollin.yams.core.designsystem.component.AppTopBar
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimaryButton
import io.github.maximerollin.yams.core.designsystem.component.YamsSecondaryButton
import io.github.maximerollin.yams.core.designsystem.icon.Trophy
import io.github.maximerollin.yams.core.designsystem.icon.Undo
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.model.Game
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.data.game.model.Player
import io.github.maximerollin.yams.feature.game.result.model.GameResultPlayerUiState
import io.github.maximerollin.yams.feature.game.result.model.GameResultUiState
import kotlin.math.round
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun GameResultRoute(
    gameId: GameId,
    onNavigateHome: () -> Unit,
    onNavigateToGame: (GameId) -> Unit,
    viewModel: GameResultViewModel = koinViewModel { parametersOf(gameId) },
) {
    val uiState by viewModel.gameResultUiState.collectAsStateWithLifecycle()
    val navigationTarget by viewModel.navigationTarget.collectAsStateWithLifecycle()
    val isActionInProgress by viewModel.isActionInProgress.collectAsStateWithLifecycle()

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
        onUndoLastMove = viewModel::onUndoLastMove,
        onFinishGame = viewModel::onFinishGame,
    )
}

@Composable
private fun GameResultScreen(
    uiState: GameResultUiState?,
    isActionInProgress: Boolean,
    onUndoLastMove: () -> Unit,
    onFinishGame: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val areActionsEnabled = uiState?.game is Game.GameInProgress && !isActionInProgress

    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            GameResultTopBar()
        },
        bottomBar = {
            if (uiState != null) {
                GameResultBottomBar(
                    isEnabled = areActionsEnabled,
                    onUndoLastMove = onUndoLastMove,
                    onFinishGame = onFinishGame,
                )
            }
        },
    ) { innerPadding ->
        if (uiState == null) {
            GameResultMessageState(
                message = "Chargement des résultats...",
                modifier = Modifier.padding(innerPadding),
            )
            return@Scaffold
        }

        if (uiState.playerResults.isEmpty()) {
            GameResultMessageState(
                message = "Aucun résultat à afficher pour le moment.",
                modifier = Modifier.padding(innerPadding),
            )
            return@Scaffold
        }

        val scrollState = rememberScrollState()
        val winners = uiState.winnerResults

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GameResultHeroCard(
                winners = winners,
                totalPlayers = uiState.playerResults.size,
                totalYamCount = uiState.totalYamCount,
                isOfficial = uiState.game is Game.GameFinished,
            )

            Text(
                text = "Classement",
                style = MaterialTheme.typography.titleMedium,
                color = YamsTheme.colors.brown,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                uiState.playerResults.forEach { playerResult ->
                    GameResultPlayerCard(playerResult = playerResult)
                }
            }
        }
    }
}

@Composable
private fun GameResultTopBar() {
    AppTopBar(
        modifier = Modifier.statusBarsPadding(),
        isDividerVisible = false,
        center = {
            Text(
                text = "Résultats",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                color = YamsTheme.colors.brown,
                textAlign = TextAlign.Center,
            )
        },
    )
}

@Composable
private fun GameResultHeroCard(
    winners: List<GameResultPlayerUiState>,
    totalPlayers: Int,
    totalYamCount: Int,
    isOfficial: Boolean,
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(22.dp),
                color = YamsTheme.colors.gold.copy(alpha = 0.18f),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = YamsIcons.Trophy,
                        contentDescription = null,
                        tint = YamsTheme.colors.brown,
                        modifier = Modifier.size(30.dp),
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = if (isOfficial) "Résultat officiel" else "Résultat provisoire",
                    style = MaterialTheme.typography.labelMedium,
                    color = YamsTheme.colors.brown,
                )
                Text(
                    text = winnerHeadline(winners),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = if (isOfficial) {
                        "La partie est terminée et le classement est désormais enregistré."
                    } else {
                        "Le classement est prêt. Tu peux encore annuler le dernier coup avant de terminer officiellement la partie."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                GameResultSummaryPill(
                    label = "Joueurs",
                    value = totalPlayers.toString(),
                    modifier = Modifier.weight(1f),
                )
                GameResultSummaryPill(
                    label = "Yams",
                    value = totalYamCount.toString(),
                    modifier = Modifier.weight(1f),
                )
            }
        }
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
    val (badgeColor, badgeContentColor) = rankColors(playerResult.rank)
    val containerColor = if (playerResult.isWinner) {
        YamsTheme.colors.gold.copy(alpha = 0.14f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(16.dp),
                color = badgeColor,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = rankLabel(playerResult.rank),
                        style = MaterialTheme.typography.labelLarge,
                        color = badgeContentColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

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
                Text(
                    text = if (playerResult.isWinner) "Vainqueur" else "Classement confirmé",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    GameResultMetaChip(
                        label = "${playerResult.numberOfTurns} tours",
                    )
                    GameResultMetaChip(
                        label = "${playerResult.numberOfFiveOfAKind} yam(s)",
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = "${playerResult.score} pts",
                    style = MaterialTheme.typography.titleMedium,
                    color = YamsTheme.colors.brown,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "${playerResult.averageScorePerTurn.format(1)} / tour",
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
                androidx.compose.material3.Icon(
                    imageVector = YamsIcons.Undo,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "Annuler le dernier coup")
            }

            YamsPrimaryButton(
                onClick = onFinishGame,
                enabled = isEnabled,
                text = "Terminer la partie",
            )
        }
    }
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

private fun winnerHeadline(winners: List<GameResultPlayerUiState>): String = when (winners.size) {
    0 -> "Aucun vainqueur"
    1 -> "${winners.first().player.name} prend la tête"
    2 -> "${winners[0].player.name} et ${winners[1].player.name} sont à égalité"
    else -> "Égalité entre ${winners.size} joueurs"
}

private fun rankLabel(rank: Int): String = if (rank == 1) "1er" else "${rank}e"

@Composable
private fun rankColors(rank: Int): Pair<Color, Color> =
    when (rank) {
        1 -> YamsTheme.colors.firstPlace to YamsTheme.colors.onFirstPlace
        2 -> YamsTheme.colors.secondPlace to YamsTheme.colors.onSecondPlace
        3 -> YamsTheme.colors.thirdPlace to YamsTheme.colors.onThirdPlace
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
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

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun GameResultScreenPreview() {
    val gameId = GameId("preview-game")
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
                    userId = UserId("alice"),
                    name = "Alice",
                    avatar = null,
                    gameId = gameId,
                    userIndex = 0,
                ),
                rank = 1,
                score = 268,
                numberOfTurns = 13,
                isWinner = true,
                numberOfFiveOfAKind = 2,
            ),
            GameResultPlayerUiState(
                player = Player(
                    userId = UserId("bob"),
                    name = "Bob",
                    avatar = null,
                    gameId = gameId,
                    userIndex = 1,
                ),
                rank = 2,
                score = 243,
                numberOfTurns = 13,
                isWinner = false,
                numberOfFiveOfAKind = 1,
            ),
            GameResultPlayerUiState(
                player = Player(
                    userId = UserId("claire"),
                    name = "Claire",
                    avatar = null,
                    gameId = gameId,
                    userIndex = 2,
                ),
                rank = 3,
                score = 219,
                numberOfTurns = 13,
                isWinner = false,
                numberOfFiveOfAKind = 0,
            ),
        ),
    )

    YamsTheme {
        GameResultScreen(
            uiState = uiState,
            isActionInProgress = false,
            onUndoLastMove = {},
            onFinishGame = {},
        )
    }
}

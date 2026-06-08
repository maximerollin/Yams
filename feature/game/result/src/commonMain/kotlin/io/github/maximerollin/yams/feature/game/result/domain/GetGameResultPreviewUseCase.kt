package io.github.maximerollin.yams.feature.game.result.domain

import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.data.game.model.GamePlayState
import io.github.maximerollin.yams.data.game.model.GameResult
import io.github.maximerollin.yams.data.game.model.PlayerResult
import io.github.maximerollin.yams.data.game.model.ScoreEntry
import io.github.maximerollin.yams.feature.game.result.model.GameResultPlayerUiState
import io.github.maximerollin.yams.feature.game.result.model.GameResultUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

internal class GetGameResultPreviewUseCase(
    private val gameRepository: GameRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    operator fun invoke(gameId: GameId): Flow<GameResultUiState?> =
        combine(
            gameRepository.getGamePlayState(gameId),
            gameRepository.getGameResult(gameId),
            gameRepository.getFinishedGamesResults(),
        ) { playState, gameResult, finishedResults ->
            val victoryCounts = finishedResults.victoryCounts()
            gameResult
                ?.takeIf { it.playersResults.isNotEmpty() }
                ?.toGameResultUiState(victoryCounts = victoryCounts, includeCurrentWinners = false)
                ?: playState?.toGameResultUiState(victoryCounts = victoryCounts)
        }
            .flowOn(dispatcher)

    private fun GameResult.toGameResultUiState(
        victoryCounts: Map<UserId, Int>,
        includeCurrentWinners: Boolean,
    ): GameResultUiState {
        return GameResultUiState(
            game = game,
            playerResults = playersResults
                .map { it.toGameResultPlayerUiState(victoryCounts, includeCurrentWinners) }
                .sortedWith(
                    compareBy<GameResultPlayerUiState> { it.rank }
                        .thenByDescending { it.score }
                        .thenBy { it.player.userIndex }
                ),
        )
    }

    private fun PlayerResult.toGameResultPlayerUiState(
        victoryCounts: Map<UserId, Int>,
        includeCurrentWinners: Boolean,
    ): GameResultPlayerUiState =
        GameResultPlayerUiState(
            player = player,
            rank = rank,
            score = finalScore,
            numberOfTurns = numberOfTurn,
            isWinner = isWinner,
            numberOfFiveOfAKind = numberOfFiveOfAKind,
            totalVictoryCount = victoryCounts[player.userId].orZero() + if (includeCurrentWinners && isWinner) 1 else 0,
        )

    private fun GamePlayState.toGameResultUiState(
        victoryCounts: Map<UserId, Int>,
    ): GameResultUiState {
        val scoreEntriesByUser = scoreEntries.groupBy(ScoreEntry::userId)
        val scoreByPlayer = players.associate { player ->
            player.userId to GameResultStateLogic.getPlayerScore(
                scoreEntries = scoreEntriesByUser[player.userId].orEmpty(),
                gameSettings = game.settings,
            )
        }
        val ranksByPlayer = GameResultStateLogic.getRanks(scoreByPlayer)

        val playerResults = players
            .map { player ->
                val playerScoreEntries = scoreEntriesByUser[player.userId].orEmpty()
                GameResultPlayerUiState(
                    player = player,
                    rank = ranksByPlayer.getValue(player.userId),
                    score = scoreByPlayer.getValue(player.userId),
                    numberOfTurns = playerScoreEntries.size,
                    isWinner = ranksByPlayer.getValue(player.userId) == 1,
                    numberOfFiveOfAKind = GameResultStateLogic.getFiveOfAKindCount(
                        playerScoreEntries
                    ),
                    totalVictoryCount = victoryCounts[player.userId].orZero() +
                            if (ranksByPlayer.getValue(player.userId) == 1) 1 else 0,
                )
            }
            .sortedWith(
                compareBy<GameResultPlayerUiState> { it.rank }
                    .thenByDescending { it.score }
                    .thenBy { it.player.userIndex }
            )

        return GameResultUiState(
            game = game,
            playerResults = playerResults,
        )
    }

    private fun List<GameResult>.victoryCounts(): Map<UserId, Int> =
        flatMap { it.playersResults }
            .filter { it.isWinner }
            .groupingBy { it.player.userId }
            .eachCount()

    private fun Int?.orZero(): Int = this ?: 0
}

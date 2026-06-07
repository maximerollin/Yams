package io.github.maximerollin.yams.feature.game.result.domain

import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.data.game.model.GamePlayState
import io.github.maximerollin.yams.data.game.model.ScoreEntry
import io.github.maximerollin.yams.feature.game.result.model.GameResultPlayerUiState
import io.github.maximerollin.yams.feature.game.result.model.GameResultUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

internal class GetGameResultPreviewUseCase(
    private val gameRepository: GameRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    operator fun invoke(gameId: GameId): Flow<GameResultUiState?> =
        gameRepository
            .getGamePlayState(gameId)
            .map { state -> state?.toGameResultUiState() }
            .flowOn(dispatcher)

    private fun GamePlayState.toGameResultUiState(): GameResultUiState {
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
                    numberOfFiveOfAKind = GameResultStateLogic.getFiveOfAKindCount(playerScoreEntries),
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
}

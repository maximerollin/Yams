package io.github.maximerollin.yams.feature.game.result.domain

import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.data.game.model.CreateGameResult
import io.github.maximerollin.yams.data.game.model.CreatePlayerResult
import io.github.maximerollin.yams.feature.game.result.model.GameResultUiState
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class FinishGameUseCase(
    private val gameRepository: GameRepository,
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(
        gameId: GameId,
        gameResultUiState: GameResultUiState,
    ) {
        require(gameResultUiState.playerResults.isNotEmpty()) {
            "Cannot finish a game without players"
        }

        val playersResults = gameResultUiState.playerResults.map { playerResult ->
            CreatePlayerResult(
                gameId = gameId,
                userId = playerResult.player.userId,
                rank = playerResult.rank,
                finalScore = playerResult.score,
                numberOfTurn = playerResult.numberOfTurns,
                rawScore = playerResult.score,
                isWinner = playerResult.isWinner,
                numberOfFiveOfAKind = playerResult.numberOfFiveOfAKind,
            )
        }

        gameRepository.finishGame(
            CreateGameResult(
                gameId = gameId,
                playersResults = playersResults,
                finishedAt = Clock.System.now(),
                yamCount = gameResultUiState.totalYamCount,
            )
        )
    }
}

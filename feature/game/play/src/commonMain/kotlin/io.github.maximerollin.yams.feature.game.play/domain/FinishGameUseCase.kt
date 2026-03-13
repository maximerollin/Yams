package io.github.maximerollin.yams.feature.game.play.domain

import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.data.game.model.CreateGameResult
import io.github.maximerollin.yams.data.game.model.CreatePlayerResult
import io.github.maximerollin.yams.feature.game.play.model.GamePlayStateUi
import io.github.maximerollin.yams.feature.game.play.model.PlayerState
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class FinishGameUseCase(
    private val gameRepository: GameRepository
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(
        gameId: GameId,
        gamePlayStateUi: GamePlayStateUi
    ) {
        require(gamePlayStateUi.playerStates.isNotEmpty()) { "Cannot finish a game without players" }

        val winnerIds = gamePlayStateUi.playerStates
            .filter { it.rank == 1 }
            .map { it.player.userId }
            .toSet()

        val playersResults = gamePlayStateUi.playerStates.map { playerState ->
            CreatePlayerResult(
                gameId = gameId,
                userId = playerState.player.userId,
                rank = playerState.rank,
                finalScore = playerState.score,
                numberOfTurn = playerState.numberOfTurns(),
                rawScore = playerState.score,
                isWinner = playerState.player.userId in winnerIds,
                numberOfFiveOfAKind = playerState.fiveOfAKindCount,
            )
        }

        val createGameResult = CreateGameResult(
            gameId = gameId,
            playersResults = playersResults,
            finishedAt = Clock.System.now(),
            yamCount = playersResults.sumOf(CreatePlayerResult::numberOfFiveOfAKind),
        )

        gameRepository.finishGame(createGameResult)
    }
}

private fun PlayerState.numberOfTurns(): Int =
    scoreEntries.values.sumOf { scores ->
        scores.count { it != null }
    }

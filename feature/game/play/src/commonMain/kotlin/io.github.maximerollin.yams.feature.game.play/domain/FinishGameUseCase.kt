package io.github.maximerollin.yams.feature.game.play.domain

import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.ScoreKey
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
            val numberOfTurn = playerState.numberOfTurns()
            val numberOfFiveOfAKind = playerState.numberOfFiveOfAKind()

            CreatePlayerResult(
                gameId = gameId,
                userId = playerState.player.userId,
                rank = playerState.rank,
                finalScore = playerState.score,
                numberOfTurn = numberOfTurn,
                rawScore = playerState.rawScore(),
                isWinner = playerState.player.userId in winnerIds,
                numberOfFiveOfAKind = numberOfFiveOfAKind,
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

private fun PlayerState.rawScore(): Int =
    scoreEntries.values.sumOf { scores ->
        scores.sumOf { it ?: 0 }
    }

private fun PlayerState.numberOfFiveOfAKind(): Int =
    scoreEntries
        .filterKeys { scoreKey ->
            scoreKey.value == ScoreKey.FIVE_OF_A_KIND.value ||
                    scoreKey.value == ScoreKey.EXTRA_FIVE_OF_A_KIND.value
        }
        .values
        .sumOf { scores ->
            scores.count { it != null }
        }

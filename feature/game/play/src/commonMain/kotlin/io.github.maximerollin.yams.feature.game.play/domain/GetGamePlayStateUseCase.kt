package io.github.maximerollin.yams.feature.game.play.domain

import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.ScoreKey
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.data.game.model.GamePlayState
import io.github.maximerollin.yams.data.game.model.ScoreEntry
import io.github.maximerollin.yams.feature.game.play.model.GamePlayStateUi
import io.github.maximerollin.yams.feature.game.play.model.PlayerState
import io.github.maximerollin.yams.feature.game.play.utils.GamePlayStateLogic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

internal class GetGamePlayStateUseCase(
    private val gameRepository: GameRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    operator fun invoke(gameId: GameId): Flow<GamePlayStateUi?> {
        return gameRepository
            .getGamePlayState(gameId)
            .map { it?.toGamePlayStateUi() }
            .flowOn(dispatcher)
    }

    private fun GamePlayState.toGamePlayStateUi(): GamePlayStateUi {
        val scoreEntriesByUser = scoreEntries.groupBy(ScoreEntry::userId)
        val scoreByPlayer = players.associate { player ->
            player.userId to GamePlayStateLogic.getPlayerScore(
                scoreEntries = scoreEntriesByUser[player.userId].orEmpty(),
                gameSettings = game.settings,
            )
        }
        val ranksByPlayer = GamePlayStateLogic.getRanks(scoreByPlayer)
        val playerStates = players.map { player ->
            val playerScoreEntries = scoreEntriesByUser[player.userId].orEmpty()
            PlayerState(
                player = player,
                scoreEntries = playerScoreEntries.toScoreEntriesByKey(
                    columnCount = game.settings.columnCount,
                ),
                extraFiveOfAKindScores = playerScoreEntries.toExtraFiveOfAKindScores(
                    columnCount = game.settings.columnCount,
                    extraFiveOfAKindValue = game.settings.extraFiveOfAKindValue,
                ),
                score = scoreByPlayer.getValue(player.userId),
                rank = ranksByPlayer.getValue(player.userId),
                fiveOfAKindCount = GamePlayStateLogic.getFiveOfAKindCount(playerScoreEntries),
            )
        }

        return GamePlayStateUi(
            game = game,
            status = GamePlayStateLogic.getGameStatus(playerStates, game),
            playerStates = playerStates,
            currentPlayer = GamePlayStateLogic.getCurrentPlayer(players, scoreEntriesByUser),
        )
    }
}

private fun List<ScoreEntry>.toScoreEntriesByKey(
    columnCount: Int,
): Map<ScoreKey, List<Int?>> = groupBy { it.cell.key.value }
    .mapKeys { (keyValue, _) -> keyValue.asScoreKey() }
    .mapValues { (_, entries) ->
        val scoreByColumnIndex = entries.associateBy { it.cell.columnIndex }
        List(columnCount) { columnIndex ->
            scoreByColumnIndex[columnIndex]?.score
        }
    }

private fun List<ScoreEntry>.toExtraFiveOfAKindScores(
    columnCount: Int,
    extraFiveOfAKindValue: Int?,
): List<Int> {
    val bonusScoresByColumnIndex = filter { it.awardsExtraFiveOfAKindBonus }
        .groupBy { it.cell.columnIndex }
        .mapValues { (_, entries) ->
            entries.size * (extraFiveOfAKindValue ?: 0)
        }

    return List(columnCount) { columnIndex ->
        bonusScoresByColumnIndex[columnIndex] ?: 0
    }
}

private fun String.asScoreKey(): ScoreKey = when (this) {
    ScoreKey.ONES.value -> ScoreKey.ONES
    ScoreKey.TWOS.value -> ScoreKey.TWOS
    ScoreKey.THREES.value -> ScoreKey.THREES
    ScoreKey.FOURS.value -> ScoreKey.FOURS
    ScoreKey.FIVES.value -> ScoreKey.FIVES
    ScoreKey.SIXES.value -> ScoreKey.SIXES
    ScoreKey.THREE_OF_A_KIND.value -> ScoreKey.THREE_OF_A_KIND
    ScoreKey.FOUR_OF_A_KIND.value -> ScoreKey.FOUR_OF_A_KIND
    ScoreKey.FULL_HOUSE.value -> ScoreKey.FULL_HOUSE
    ScoreKey.SMALL_STRAIGHT.value -> ScoreKey.SMALL_STRAIGHT
    ScoreKey.LARGE_STRAIGHT.value -> ScoreKey.LARGE_STRAIGHT
    ScoreKey.FIVE_OF_A_KIND.value -> ScoreKey.FIVE_OF_A_KIND
    ScoreKey.CHANCE.value -> ScoreKey.CHANCE
    else -> ScoreKey(this)
}

package io.github.maximerollin.yams.data.game.mapper

import io.github.maximerollin.yams.core.database.entity.PlayerResultEntity
import io.github.maximerollin.yams.core.database.entity.PlayerResultWithPlayerEntity
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.data.game.model.CreatePlayerResult
import io.github.maximerollin.yams.data.game.model.PlayerResult

internal fun CreatePlayerResult.asEntity() = PlayerResultEntity(
    gameId = gameId.value,
    userId = userId.value,
    rank = rank,
    finalScore = finalScore,
    numberOfTurn = numberOfTurn,
    isWinner = isWinner,
    rawScore = rawScore,
    numberOfFiveOfAKind = numberOfFiveOfAKind
)

/**
 * Mapper function to convert from GamePlayerResultEntity to PlayerResult model
 */
internal fun PlayerResultWithPlayerEntity.asExternalModel() = PlayerResult(
    gameId = GameId(value = result.gameId),
    player = player.asExternalModel(),
    rank = result.rank,
    finalScore = result.finalScore,
    rawScore = result.rawScore,
    isWinner = result.isWinner,
    numberOfTurn = result.numberOfTurn,
    numberOfFiveOfAKind = result.numberOfFiveOfAKind
)

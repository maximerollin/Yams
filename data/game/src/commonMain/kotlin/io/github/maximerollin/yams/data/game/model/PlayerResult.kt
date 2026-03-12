package io.github.maximerollin.yams.data.game.model

import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.UserId

public data class PlayerResult(
    val gameId: GameId,
    val player: Player,
    val rank: Int,
    val finalScore: Int,
    val numberOfTurn: Int,
    val rawScore: Int,
    val isWinner: Boolean,
    val numberOfFiveOfAKind: Int,
) {
    public val scorePerTurn: Float = when {
        numberOfTurn > 0 -> rawScore.toFloat() / numberOfTurn
        else -> -1f
    }
}

public data class CreatePlayerResult(
    val gameId: GameId,
    val userId: UserId,
    val rank: Int,
    val finalScore: Int,
    val numberOfTurn: Int,
    val rawScore: Int,
    val isWinner: Boolean,
    val numberOfFiveOfAKind: Int
)

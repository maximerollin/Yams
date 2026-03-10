package io.github.maximerollin.yams.feature.game.play.model

import io.github.maximerollin.yams.core.model.Game
import io.github.maximerollin.yams.core.model.ScoreKey
import io.github.maximerollin.yams.data.game.model.Player

internal data class GamePlayStateUi(
    val game: Game,
    val status: GameStatus,
    val playerStates: List<PlayerState>,
    val currentPlayer: Player,
) {
    val currentPlayerState: PlayerState =
        playerStates.find { it.player.userId === currentPlayer.userId }
            ?: throw IllegalStateException("Current player state not found")
}

internal data class PlayerState(
    val player: Player,
    val scoreEntries: Map<ScoreKey, List<Int?>>,
    val score: Int,
    val rank: Int,
)

internal data class GamePlayColumnSummary(
    val columnIndex: Int,
    val filledCells: Int,
    val totalCells: Int,
    val totalScore: Int,
)

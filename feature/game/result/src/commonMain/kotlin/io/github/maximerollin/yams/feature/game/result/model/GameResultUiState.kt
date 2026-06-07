package io.github.maximerollin.yams.feature.game.result.model

import io.github.maximerollin.yams.core.model.Game
import io.github.maximerollin.yams.data.game.model.Player

internal data class GameResultUiState(
    val game: Game,
    val playerResults: List<GameResultPlayerUiState>,
) {
    val winnerResults: List<GameResultPlayerUiState> =
        playerResults.filter(GameResultPlayerUiState::isWinner)

    val totalYamCount: Int = playerResults.sumOf(GameResultPlayerUiState::numberOfFiveOfAKind)
}

internal data class GameResultPlayerUiState(
    val player: Player,
    val rank: Int,
    val score: Int,
    val numberOfTurns: Int,
    val isWinner: Boolean,
    val numberOfFiveOfAKind: Int,
) {
    val averageScorePerTurn: Float =
        if (numberOfTurns > 0) score.toFloat() / numberOfTurns else 0f
}

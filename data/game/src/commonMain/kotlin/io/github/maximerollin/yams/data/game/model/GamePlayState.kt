package io.github.maximerollin.yams.data.game.model

import io.github.maximerollin.yams.core.model.Game

public data class GamePlayState(
    val game: Game,
    val scoreEntries: List<ScoreEntry>,
    val players: List<Player>,
)

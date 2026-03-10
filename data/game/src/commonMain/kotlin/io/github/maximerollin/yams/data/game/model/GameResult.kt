package io.github.maximerollin.yams.data.game.model

import io.github.maximerollin.yams.core.model.Game
import io.github.maximerollin.yams.core.model.GameId
import kotlin.time.Instant

public data class GameResult(
    val game: Game,
    val playersResults: List<PlayerResult>,
)

public data class CreateGameResult(
    val gameId: GameId,
    val playersResults: List<CreatePlayerResult>,
    val finishedAt: Instant,
    val yamCount: Int,
)
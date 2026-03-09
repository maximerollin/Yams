package io.github.maximerollin.yams.data.game.model

import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.UserId
import kotlin.time.Instant

public data class ThrowId(val value: String)

public data class Throw(
    val id: ThrowId,
    val gameId: GameId,
    val userId: UserId,
    val score: Int,
    val timestamp: Instant
)

public data class CreateThrow(
    val gameId: GameId,
    val userId: UserId,
    val score: Int
)
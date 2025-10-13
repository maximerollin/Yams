package io.github.maximerollin.yams.core.model

import kotlin.time.ExperimentalTime

public data class GameId(val value: String)

@OptIn(ExperimentalTime::class)
public data class Game(
    val id: GameId,
)


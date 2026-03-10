package io.github.maximerollin.yams.core.model

import io.github.vinceglb.filekit.PlatformFile
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

public data class GameId(val value: String)

@OptIn(ExperimentalTime::class)
public sealed class Game {
    public abstract val id: GameId
    public abstract val settings: GameSettings
    public abstract val startedAt: Instant

    public data class GameInProgress(
        override val id: GameId,
        override val settings: GameSettings,
        override val startedAt: Instant,
    ) : Game()

    public data class GameFinished(
        override val id: GameId,
        override val settings: GameSettings,
        override val startedAt: Instant,
        val finishedAt: Instant,
        val photo: PlatformFile?,
        val gameNumber: Int,
        val yamCount: Int,
    ) : Game()
}

package io.github.maximerollin.yams.data.game.model

import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.core.model.UserId
import io.github.vinceglb.filekit.PlatformFile

public data class CreateGame(
    val userIds: List<UserId>,
    val gameSettings: GameSettings,
)

public data class GamePhoto(
    val gameId: GameId,
    val photo: PlatformFile,
)
package io.github.maximerollin.yams.data.game.model

import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.User
import io.github.maximerollin.yams.core.model.UserId
import io.github.vinceglb.filekit.PlatformFile

public data class Player(
    val userId: UserId,
    val name: String,
    val avatar: PlatformFile?,
    val gameId: GameId,
    val userIndex: Int,
) {
    public val user: User = User(
        id = userId,
        name = name,
        avatar = avatar
    )
}
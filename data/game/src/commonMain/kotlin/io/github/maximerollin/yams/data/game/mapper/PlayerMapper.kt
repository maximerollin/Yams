package io.github.maximerollin.yams.data.game.mapper

import io.github.maximerollin.yams.core.database.entity.PlayerWithUserEntity
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.data.game.model.Player

internal fun PlayerWithUserEntity.asExternalModel() = Player(
    userId = UserId(value = user.id),
    name = user.name,
    avatar = user.avatar,
    gameId = GameId(value = player.gameId),
    userIndex = player.userIndex,
)
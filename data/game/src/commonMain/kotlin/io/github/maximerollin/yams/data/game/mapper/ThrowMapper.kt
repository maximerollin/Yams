package io.github.maximerollin.yams.data.game.mapper

import io.github.maximerollin.yams.core.database.entity.ThrowEntity
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.data.game.model.Throw
import io.github.maximerollin.yams.data.game.model.ThrowId

internal fun ThrowEntity.asExternalModel() = Throw(
    id = ThrowId(value = id),
    gameId = GameId(value = gameId),
    userId = UserId(value = userId),
    score = score,
    timestamp = timestamp
)
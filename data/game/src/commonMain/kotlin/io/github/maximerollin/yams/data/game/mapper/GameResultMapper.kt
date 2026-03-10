package io.github.maximerollin.yams.data.game.mapper

import io.github.maximerollin.yams.core.database.entity.GameResultEntity
import io.github.maximerollin.yams.core.database.entity.PlayerResultWithPlayerEntity
import io.github.maximerollin.yams.data.game.model.GameResult

internal fun GameResultEntity.asExternalModel(): GameResult {
    return GameResult(
        game = game.asExternalModel(),
        playersResults = results
            .map(PlayerResultWithPlayerEntity::asExternalModel)
            .sortedBy { it.rank }
    )
}
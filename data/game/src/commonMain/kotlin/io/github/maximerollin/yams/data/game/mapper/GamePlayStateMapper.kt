package io.github.maximerollin.yams.data.game.mapper

import io.github.maximerollin.yams.core.database.entity.GamePlayStateEntity
import io.github.maximerollin.yams.core.database.entity.PlayerWithUserEntity
import io.github.maximerollin.yams.core.database.entity.ScoreEntryEntity
import io.github.maximerollin.yams.data.game.model.GamePlayState

internal fun GamePlayStateEntity.asExternalModel(): GamePlayState = GamePlayState(
    game = game.asExternalModel(),
    scoreEntries = scoreEntries
        .map(ScoreEntryEntity::asExternalModel)
        .sortedBy { it.timestamp },
    players = players
        .map(PlayerWithUserEntity::asExternalModel)
        .sortedBy { it.userIndex },
)
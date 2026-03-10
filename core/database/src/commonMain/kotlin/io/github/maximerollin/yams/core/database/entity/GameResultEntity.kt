package io.github.maximerollin.yams.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

public data class GameResultEntity(
    @Embedded
    val game: GameEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "gameId",
        entity = PlayerResultEntity::class,
    )
    val results: List<PlayerResultWithPlayerEntity>,
)
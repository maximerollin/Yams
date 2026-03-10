package io.github.maximerollin.yams.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

public data class GamePlayStateEntity(
    @Embedded
    val game: GameEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "gameId",
        entity = ScoreEntryEntity::class,
    )
    val scoreEntries: List<ScoreEntryEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "gameId",
        entity = PlayerEntity::class,
    )
    val players: List<PlayerWithUserEntity>,
)
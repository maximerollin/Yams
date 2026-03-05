package io.github.maximerollin.yams.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Relation

@Entity(
    tableName = "PlayerResult",
    primaryKeys = ["gameId", "userId"],
    indices = [Index("gameId"), Index("userId")],
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
public data class PlayerResultEntity(
    val gameId: String,
    val userId: String,
    val rank: Int,
    val finalScore: Int,
    val numberOfTurn: Int,
    val rawScorePerTurn: Int,
    val isWinner: Boolean,
    val numberOfFiveOfAKind: Int,
)

public data class PlayerResultWithPlayerEntity(
    @Embedded
    val result: PlayerResultEntity,

    @Relation(
        parentColumn = "userId",
        entityColumn = "userId",
        entity = PlayerEntity::class
    )
    val player: PlayerWithUserEntity
)
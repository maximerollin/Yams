package io.github.maximerollin.yams.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Relation

@Entity(
    tableName = "Player",
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
        )
    ]
)
public data class PlayerEntity(
    val gameId: String,
    val userId: String,
    // val teamId: String?,
    // val teamIndex: Int?,
    val userIndex: Int,
)

public data class PlayerWithUserEntity(
    @Embedded
    val player: PlayerEntity,

    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: UserEntity
)
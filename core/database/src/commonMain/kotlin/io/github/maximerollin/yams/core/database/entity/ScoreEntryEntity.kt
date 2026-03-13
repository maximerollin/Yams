package io.github.maximerollin.yams.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Instant

@Entity(
    tableName = "ScoreEntry",
    indices = [Index("gameId"), Index("userId"), Index("scoreKey")],
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
public data class ScoreEntryEntity(
    @PrimaryKey
    val id: String,
    val gameId: String,
    val userId: String,
    val scoreKey: String,
    val columnIndex: Int,
    val score: Int,
    @ColumnInfo(defaultValue = "0")
    val awardsExtraFiveOfAKindBonus: Boolean = false,
    val timestamp: Instant,
)

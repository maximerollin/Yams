package io.github.maximerollin.yams.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.maximerollin.yams.core.model.Game
import kotlin.time.Instant

@Entity(tableName = "games")
public data class GameEntity(
    @PrimaryKey
    val id: String,
)

package io.github.maximerollin.yams.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.vinceglb.filekit.PlatformFile
import kotlin.time.Instant

@Entity(tableName = "Game")
public data class GameEntity(
    @PrimaryKey
    val id: String,
    @Embedded("settings")
    val settings: GameSettingsEntity,
    val status: GameStatusEntity,
    val type: GameTypeEntity,
    val createdAt: Instant,
    val updatedAt: Instant,
    val finishedAt: Instant?,
    val photo: PlatformFile?,
    val gameNumber: Int,
    val yamCount: Int?,
)

public data class GameSettingsEntity(
    val id: String,
)

public enum class GameStatusEntity {
    IN_PROGRESS,
    FINISHED,
    CANCELLED,
}

public enum class GameTypeEntity {
    CLASSIC,
    YAM_BONUS,
}

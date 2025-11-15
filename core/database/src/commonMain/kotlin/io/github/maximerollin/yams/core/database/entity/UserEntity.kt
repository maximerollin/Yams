package io.github.maximerollin.yams.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.vinceglb.filekit.PlatformFile
import kotlin.time.Instant

@Entity(tableName = "User")
public data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val avatar: PlatformFile? = null,
    val createdAt: Instant,
    @ColumnInfo(defaultValue = "0")
    val archived: Boolean = false
)

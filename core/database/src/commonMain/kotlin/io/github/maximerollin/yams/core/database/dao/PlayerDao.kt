package io.github.maximerollin.yams.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import io.github.maximerollin.yams.core.database.entity.PlayerEntity

@Dao
internal interface PlayerDao {
    @Insert
    suspend fun insertAll(players: List<PlayerEntity>)
}
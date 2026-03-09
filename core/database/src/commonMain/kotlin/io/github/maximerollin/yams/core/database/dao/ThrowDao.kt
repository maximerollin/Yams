package io.github.maximerollin.yams.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.maximerollin.yams.core.database.entity.ThrowEntity

@Dao
internal interface ThrowDao {
    @Insert
    suspend fun insertThrow(throwEntity: ThrowEntity)

    @Query("DELETE FROM Throw WHERE id = (SELECT id FROM Throw WHERE gameId = :gameId ORDER BY timestamp DESC LIMIT 1)")
    suspend fun deleteLastThrowOfTheGame(gameId: String)
}
package io.github.maximerollin.yams.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.maximerollin.yams.core.database.entity.ScoreEntryEntity

@Dao
internal interface ScoreEntryDao {
    @Insert
    suspend fun insertScoreEntry(scoreEntryEntity: ScoreEntryEntity)

    @Query(
        "DELETE FROM ScoreEntry WHERE id = (" +
            "SELECT id FROM ScoreEntry WHERE gameId = :gameId ORDER BY timestamp DESC LIMIT 1" +
            ")"
    )
    suspend fun deleteLastScoreEntryOfGame(gameId: String)
}

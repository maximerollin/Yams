package io.github.maximerollin.yams.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.maximerollin.yams.core.database.entity.PlayerResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface PlayerResultDao {
    @Upsert
    suspend fun upsertAll(results: List<PlayerResultEntity>)

    @Query("DELETE FROM PlayerResult WHERE gameId = :gameId")
    suspend fun deleteByGameId(gameId: String)

    @Query("SELECT COUNT(*) FROM PlayerResult WHERE userId = :userId AND isWinner = 1")
    fun getVictoryCountByUser(userId: String): Flow<Int>
}
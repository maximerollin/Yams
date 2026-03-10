package io.github.maximerollin.yams.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.github.maximerollin.yams.core.database.entity.GameResultEntity
import io.github.maximerollin.yams.core.database.entity.GameStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface GameResultDao {
    @Transaction
    @Query("SELECT * FROM Game WHERE id = :gameId")
    fun getGameResult(gameId: String): Flow<GameResultEntity?>

    @Transaction
    @Query("SELECT * FROM Game WHERE status = :status ORDER BY createdAt DESC")
    fun getGamesResults(status: GameStatusEntity): Flow<List<GameResultEntity>>

    @Transaction
    @Query("SELECT * FROM Game WHERE status = :status AND IN (SELECT gameId FROM Player WHERE userId = :userId) ORDER BY createdAt DESC")
    fun getGamesResultsByUser(
        status: GameStatusEntity,
        userId: String
    ): Flow<List<GameResultEntity>>
}
package io.github.maximerollin.yams.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.github.maximerollin.yams.core.database.entity.GamePlayStateEntity
import io.github.maximerollin.yams.core.database.entity.GameStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface GamePlayStateDao {
    @Transaction
    @Query("SELECT * FROM Game WHERE id = :gameId")
    fun getGamePlayState(gameId: String): Flow<GamePlayStateEntity?>

    @Transaction
    @Query("SELECT * FROM Game WHERE status = :status ORDER BY createdAt DESC")
    fun getGamesPlayStates(status: GameStatusEntity): Flow<List<GamePlayStateEntity>>
}
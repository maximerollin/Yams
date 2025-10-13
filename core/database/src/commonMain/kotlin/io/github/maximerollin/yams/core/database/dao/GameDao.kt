package io.github.maximerollin.yams.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.github.maximerollin.yams.core.database.entity.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
public interface GameDao {
    
    @Query("SELECT * FROM games ORDER BY createdAt DESC")
    public fun getAllGames(): Flow<List<GameEntity>>
    
    @Query("SELECT * FROM games WHERE id = :gameId")
    public suspend fun getGameById(gameId: String): GameEntity?
    
    @Query("SELECT * FROM games WHERE status = :status ORDER BY createdAt DESC")
    public fun getGamesByStatus(status: String): Flow<List<GameEntity>>
    
    @Query("SELECT * FROM games WHERE playerIds LIKE '%' || :playerId || '%' ORDER BY createdAt DESC")
    public fun getGamesByPlayer(playerId: String): Flow<List<GameEntity>>
    
    @Query("SELECT * FROM games WHERE status = 'FINISHED' ORDER BY finishedAt DESC LIMIT :limit")
    public fun getRecentFinishedGames(limit: Int = 10): Flow<List<GameEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public suspend fun insertGame(game: GameEntity)
    
    @Update
    public suspend fun updateGame(game: GameEntity)
    
    @Delete
    public suspend fun deleteGame(game: GameEntity)
    
    @Query("DELETE FROM games WHERE id = :gameId")
    public suspend fun deleteGameById(gameId: String)
    
    @Query("SELECT COUNT(*) FROM games")
    public suspend fun getGameCount(): Int
    
    @Query("SELECT COUNT(*) FROM games WHERE status = 'FINISHED'")
    public suspend fun getFinishedGameCount(): Int
    
    @Query("SELECT COUNT(*) FROM games WHERE playerIds LIKE '%' || :playerId || '%'")
    public suspend fun getGameCountByPlayer(playerId: String): Int
    
    @Query("SELECT COUNT(*) FROM games WHERE playerIds LIKE '%' || :playerId || '%' AND status = 'FINISHED'")
    public suspend fun getFinishedGameCountByPlayer(playerId: String): Int
}

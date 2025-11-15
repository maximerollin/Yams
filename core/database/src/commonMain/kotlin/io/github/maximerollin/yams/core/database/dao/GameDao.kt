package io.github.maximerollin.yams.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.github.maximerollin.yams.core.database.entity.GameEntity
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow

@Dao
internal interface GameDao {
    @Query("SELECT * FROM Game")
    fun getGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM Game WHERE id = :id")
    fun getGameById(id: String): Flow<GameEntity?>

    @Query("SELECT COUNT(*) FROM Game")
    fun getNumberOfGames(): Flow<Int>

    @Query("SELECT COUNT(*) FROM Game WHERE finishedAt IS NOT NULL")
    fun getNumberOfFinishedGames(): Flow<Int>

    @Insert
    suspend fun insertGame(game: GameEntity)

    @Update
    suspend fun updateGame(game: GameEntity)

    @Query("DELETE FROM Game WHERE id = :id")
    suspend fun deleteGame(id: String)

    @Query("UPDATE Game SET photo = :photo WHERE id = :id")
    suspend fun updateGamePhoto(id: String, photo: PlatformFile?)
}

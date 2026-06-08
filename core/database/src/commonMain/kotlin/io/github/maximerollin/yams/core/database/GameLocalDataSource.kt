package io.github.maximerollin.yams.core.database

import io.github.maximerollin.yams.core.database.dao.GameDao
import io.github.maximerollin.yams.core.database.entity.GameEntity
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow

public interface GameLocalDataSource {
    public fun getGames(): Flow<List<GameEntity>>
    public fun getGameById(id: String): Flow<GameEntity?>
    public fun getNumberOfGames(): Flow<Int>
    public fun getNumberOfFinishedGames(): Flow<Int>
    public suspend fun insertGame(game: GameEntity)
    public suspend fun updateGame(game: GameEntity)
    public suspend fun deleteGame(id: String)
    public suspend fun updateGamePhoto(id: String, photo: PlatformFile?)
}

internal class RoomGameLocalDataSource(
    private val gameDao: GameDao
) : GameLocalDataSource {
    override fun getGames(): Flow<List<GameEntity>> = gameDao.getGames()
    override fun getGameById(id: String): Flow<GameEntity?> = gameDao.getGameById(id)
    override fun getNumberOfGames(): Flow<Int> = gameDao.getNumberOfGames()
    override fun getNumberOfFinishedGames(): Flow<Int> = gameDao.getNumberOfFinishedGames()
    override suspend fun insertGame(game: GameEntity) = gameDao.insertGame(game)
    override suspend fun updateGame(game: GameEntity) = gameDao.updateGame(game)
    override suspend fun deleteGame(id: String) = gameDao.deleteGame(id)
    override suspend fun updateGamePhoto(id: String, photo: PlatformFile?) = gameDao.updateGamePhoto(id, photo)

}

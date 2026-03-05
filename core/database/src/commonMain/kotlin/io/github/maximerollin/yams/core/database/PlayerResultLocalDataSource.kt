package io.github.maximerollin.yams.core.database

import io.github.maximerollin.yams.core.database.dao.PlayerResultDao
import io.github.maximerollin.yams.core.database.entity.PlayerResultEntity
import kotlinx.coroutines.flow.Flow

public interface PlayerResultLocalDataSource {
    public suspend fun upsertAll(results: List<PlayerResultEntity>)
    public suspend fun deleteByGameId(gameId: String)
    public fun getVictoryCountByUser(userId: String): Flow<Int>
}

internal class RoomPlayerResultLocalDataSource(
    private val playerResultDao: PlayerResultDao
) : PlayerResultLocalDataSource {
    override suspend fun upsertAll(results: List<PlayerResultEntity>) =
        playerResultDao.upsertAll(results)

    override suspend fun deleteByGameId(gameId: String) =
        playerResultDao.deleteByGameId(gameId)

    override fun getVictoryCountByUser(userId: String): Flow<Int> =
        playerResultDao.getVictoryCountByUser(userId)

}
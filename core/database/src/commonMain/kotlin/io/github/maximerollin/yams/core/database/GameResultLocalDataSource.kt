package io.github.maximerollin.yams.core.database

import io.github.maximerollin.yams.core.database.dao.GameResultDao
import io.github.maximerollin.yams.core.database.entity.GameResultEntity
import io.github.maximerollin.yams.core.database.entity.GameStatusEntity
import kotlinx.coroutines.flow.Flow

public interface GameResultLocalDataSource {
    public fun getGameResult(gameId: String): Flow<GameResultEntity?>
    public fun getFinishedGamesResults(): Flow<List<GameResultEntity>>
    public fun getFinishedGamesResultsByUser(userId: String): Flow<List<GameResultEntity>>
}

internal class RoomGameResultLocalDataSource(
    private val gameResultDao: GameResultDao,
) : GameResultLocalDataSource {
    override fun getGameResult(gameId: String): Flow<GameResultEntity?> =
        gameResultDao.getGameResult(gameId)

    override fun getFinishedGamesResults(): Flow<List<GameResultEntity>> =
        gameResultDao.getGamesResults(status = GameStatusEntity.FINISHED)

    override fun getFinishedGamesResultsByUser(userId: String): Flow<List<GameResultEntity>> =
        gameResultDao.getGamesResultsByUser(
            status = GameStatusEntity.FINISHED,
            userId = userId
        )
}
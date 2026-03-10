package io.github.maximerollin.yams.core.database

import io.github.maximerollin.yams.core.database.dao.GamePlayStateDao
import io.github.maximerollin.yams.core.database.entity.GamePlayStateEntity
import io.github.maximerollin.yams.core.database.entity.GameStatusEntity
import kotlinx.coroutines.flow.Flow

public interface GamePlayStateLocalDataSource {
    public fun getGamePlayState(gameId: String): Flow<GamePlayStateEntity?>
    public fun getInProgressGamesPlayStates(): Flow<List<GamePlayStateEntity>>
}

internal class RoomGamePlayStateLocalDataSource(
    private val gamePlayStateDao: GamePlayStateDao
) : GamePlayStateLocalDataSource {
    override fun getGamePlayState(gameId: String): Flow<GamePlayStateEntity?> =
        gamePlayStateDao.getGamePlayState(gameId)

    override fun getInProgressGamesPlayStates(): Flow<List<GamePlayStateEntity>> =
        gamePlayStateDao.getGamesPlayStates(status = GameStatusEntity.IN_PROGRESS)
    }
}
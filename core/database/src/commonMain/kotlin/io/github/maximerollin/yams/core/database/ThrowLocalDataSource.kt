package io.github.maximerollin.yams.core.database

import io.github.maximerollin.yams.core.database.dao.ThrowDao
import io.github.maximerollin.yams.core.database.entity.ThrowEntity

public interface ThrowLocalDataSource {
    public suspend fun insertThrow(throwEntity: ThrowEntity)
    public suspend fun deleteLastThrowOfTheGame(gameId: String)
}

internal class RoomThrowLocalDataSource(
    private val throwDao: ThrowDao
) : ThrowLocalDataSource {
    override suspend fun insertThrow(throwEntity: ThrowEntity) = throwDao.insertThrow(throwEntity)
    override suspend fun deleteLastThrowOfTheGame(gameId: String) =
        throwDao.deleteLastThrowOfTheGame(gameId)
}
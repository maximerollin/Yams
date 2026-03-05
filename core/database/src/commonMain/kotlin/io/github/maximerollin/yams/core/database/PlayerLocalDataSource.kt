package io.github.maximerollin.yams.core.database

import io.github.maximerollin.yams.core.database.dao.PlayerDao
import io.github.maximerollin.yams.core.database.entity.PlayerEntity

public interface PlayerLocalDataSource {
    public suspend fun insertAll(players: List<PlayerEntity>)
}

internal class RoomPlayerLocalDataSource(
    private val playerDao: PlayerDao
) : PlayerLocalDataSource {
    override suspend fun insertAll(players: List<PlayerEntity>) =
        playerDao.insertAll(players)
}
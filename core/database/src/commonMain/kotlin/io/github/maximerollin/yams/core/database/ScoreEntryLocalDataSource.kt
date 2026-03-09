package io.github.maximerollin.yams.core.database

import io.github.maximerollin.yams.core.database.dao.ScoreEntryDao
import io.github.maximerollin.yams.core.database.entity.ScoreEntryEntity

public interface ScoreEntryLocalDataSource {
    public suspend fun insertScoreEntry(scoreEntryEntity: ScoreEntryEntity)
    public suspend fun deleteLastScoreEntryOfGame(gameId: String)
}

internal class RoomScoreEntryLocalDataSource(
    private val scoreEntryDao: ScoreEntryDao,
) : ScoreEntryLocalDataSource {
    override suspend fun insertScoreEntry(scoreEntryEntity: ScoreEntryEntity) =
        scoreEntryDao.insertScoreEntry(scoreEntryEntity)

    override suspend fun deleteLastScoreEntryOfGame(gameId: String) =
        scoreEntryDao.deleteLastScoreEntryOfGame(gameId)
}

package io.github.maximerollin.yams.data.game

import io.github.maximerollin.yams.core.database.ScoreEntryLocalDataSource
import io.github.maximerollin.yams.core.database.entity.ScoreEntryEntity
import io.github.maximerollin.yams.data.game.model.CreateScoreEntry
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

public interface ScoreEntryRepository {
    public suspend fun createScoreEntry(value: CreateScoreEntry)
}

internal class DefaultScoreEntryRepository(
    private val scoreEntryLocalDataSource: ScoreEntryLocalDataSource,
) : ScoreEntryRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createScoreEntry(value: CreateScoreEntry) {
        val scoreEntryId = Uuid.random().toString()
        scoreEntryLocalDataSource.insertScoreEntry(
            ScoreEntryEntity(
                id = scoreEntryId,
                gameId = value.gameId.value,
                userId = value.userId.value,
                scoreKey = value.cell.key.value,
                columnIndex = value.cell.columnIndex,
                score = value.score,
                awardsExtraFiveOfAKindBonus = value.awardsExtraFiveOfAKindBonus,
                timestamp = Clock.System.now(),
            )
        )
    }
}

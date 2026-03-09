package io.github.maximerollin.yams.data.game

import io.github.maximerollin.yams.core.database.ThrowLocalDataSource
import io.github.maximerollin.yams.core.database.entity.ThrowEntity
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.data.game.model.CreateThrow
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

public interface ThrowRepository {
    public suspend fun createThrow(value: CreateThrow)
    public suspend fun deleteLastThrowOfTheGame(gameId: GameId)
}

internal class DefaultThrowRepository(
    private val throwLocalDataSource: ThrowLocalDataSource,
) : ThrowRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createThrow(value: CreateThrow) {
        val throwId = Uuid.random().toString()
        throwLocalDataSource.insertThrow(
            ThrowEntity(
                id = throwId,
                gameId = value.gameId.value,
                userId = value.userId.value,
                score = value.score,
                timestamp = Clock.System.now()
            )
        )
    }

    override suspend fun deleteLastThrowOfTheGame(gameId: GameId) {
        throwLocalDataSource.deleteLastThrowOfTheGame(gameId.value)
    }
}
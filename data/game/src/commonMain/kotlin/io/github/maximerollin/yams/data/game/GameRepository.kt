package io.github.maximerollin.yams.data.game

import io.github.maximerollin.yams.core.model.Game
import io.github.maximerollin.yams.core.model.GameId
import kotlinx.coroutines.flow.Flow

public interface GameRepository {
    public fun getNumberOfGames(): Flow<Int>
    public fun getNumberOfFinishedGames(): Flow<Int>
    public fun getGame(id: GameId): Flow<Game?>
    public fun getGames(): Flow<List<Game>>
    public fun getFinishedGames(): Flow<List<Game>>
    public fun getInProgressGames(): Flow<List<Game>>
    public suspend fun finishGame(gameId: GameId)
    public suspend fun deleteGame(gameId: GameId)
}

internal class DefaultGameRepository : GameRepository {
    override fun getNumberOfGames(): Flow<Int> {
        TODO("Not yet implemented")
    }

    override fun getNumberOfFinishedGames(): Flow<Int> {
        TODO("Not yet implemented")
    }

    override fun getGame(id: GameId): Flow<Game?> {
        TODO("Not yet implemented")
    }

    override fun getGames(): Flow<List<Game>> {
        TODO("Not yet implemented")
    }

    override fun getFinishedGames(): Flow<List<Game>> {
        TODO("Not yet implemented")
    }

    override fun getInProgressGames(): Flow<List<Game>> {
        TODO("Not yet implemented")
    }

    override suspend fun finishGame(gameId: GameId) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGame(gameId: GameId) {
        TODO("Not yet implemented")
    }
}


package io.github.maximerollin.yams.data.game

import io.github.maximerollin.yams.core.database.GameLocalDataSource
import io.github.maximerollin.yams.core.database.TransactionRunner
import io.github.maximerollin.yams.core.file.FileEntity
import io.github.maximerollin.yams.core.file.FileLocalDataSource
import io.github.maximerollin.yams.core.model.GameId
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

public interface GameRepository {
    public fun getNumberOfGames(): Flow<Int>
    public fun getNumberOfFinishedGames(): Flow<Int>
    public fun getGamePhoto(gameId: GameId): Flow<PlatformFile?>
    public suspend fun deleteGame(gameId: GameId)
    public suspend fun updateGamePhoto(gameId: GameId, photo: PlatformFile?)
}

internal class DefaultGameRepository(
    private val transactionRunner: TransactionRunner,
    private val gameLocalDataSource: GameLocalDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
    private val coroutineScope: CoroutineScope,
) : GameRepository {

    override fun getNumberOfGames(): Flow<Int> = gameLocalDataSource
        .getNumberOfGames()

    override fun getNumberOfFinishedGames(): Flow<Int> = gameLocalDataSource
        .getNumberOfFinishedGames()

    override fun getGamePhoto(gameId: GameId): Flow<PlatformFile?> {
        return gameLocalDataSource.getGameById(gameId.value).map { game ->
            game?.photo
        }
    }

    override suspend fun deleteGame(gameId: GameId) {
        gameLocalDataSource.deleteGame(gameId.value)
    }

    override suspend fun updateGamePhoto(gameId: GameId, photo: PlatformFile?) {
        coroutineScope.launch {
            // Compress the photo
            val compressedBytes = photo
                ?.takeIf { it.exists() }
                ?.readBytes()
                ?.let { fileLocalDataSource.compressPhoto(it) }

            // Save the photo of the game
            val savedPhoto = fileLocalDataSource.updateEntityFile(
                entity = FileEntity.Game,
                entityId = gameId.value,
                byteArray = compressedBytes,
                extension = "jpg",
            )

            // Update the game to associate the photo
            gameLocalDataSource.updateGamePhoto(gameId.value, savedPhoto)
        }.join()
    }
}

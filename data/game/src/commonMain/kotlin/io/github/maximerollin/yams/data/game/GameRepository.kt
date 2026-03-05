package io.github.maximerollin.yams.data.game

import io.github.maximerollin.yams.core.database.GameLocalDataSource
import io.github.maximerollin.yams.core.database.PlayerLocalDataSource
import io.github.maximerollin.yams.core.database.TransactionRunner
import io.github.maximerollin.yams.core.database.entity.GameEntity
import io.github.maximerollin.yams.core.database.entity.GameStatusEntity
import io.github.maximerollin.yams.core.database.entity.PlayerEntity
import io.github.maximerollin.yams.core.file.FileEntity
import io.github.maximerollin.yams.core.file.FileLocalDataSource
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.data.game.mapper.asEntity
import io.github.maximerollin.yams.data.game.model.CreateGame
import io.github.maximerollin.yams.data.game.model.GamePhoto
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

public interface GameRepository {
    public fun getNumberOfGames(): Flow<Int>
    public fun getNumberOfFinishedGames(): Flow<Int>
    public fun getGamePhoto(gameId: GameId): Flow<PlatformFile?>
    public fun getUserPhotos(userUd: UserId): Flow<List<GamePhoto>>
    public suspend fun createGame(value: CreateGame, isShuffled: Boolean): GameId
    public suspend fun deleteGame(gameId: GameId)
    public suspend fun updateGamePhoto(gameId: GameId, photo: PlatformFile?)
}

internal class DefaultGameRepository(
    private val transactionRunner: TransactionRunner,
    private val gameLocalDataSource: GameLocalDataSource,
    private val playerLocalDataSource: PlayerLocalDataSource,
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

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createGame(value: CreateGame, isShuffled: Boolean): GameId {
        val gameId = GameId(value = Uuid.random().toString())
        val numberOfGames = gameLocalDataSource.getNumberOfGames().first()

        transactionRunner {
            gameLocalDataSource.insertGame(
                game = GameEntity(
                    id = gameId.value,
                    settings = value.gameSettings.asEntity(),
                    status = GameStatusEntity.IN_PROGRESS,
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now(),
                    finishedAt = null,
                    photo = null,
                    gameNumber = numberOfGames + 1,
                )
            )

            playerLocalDataSource.insertAll(
                players = value.userIds.map { userId ->
                    PlayerEntity(
                        gameId = gameId.value,
                        userId = userId.value,
                        userIndex = value.userIds.indexOf(userId) // index of turn order within the game
                    )
                }
            )
        }

        return gameId
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

    override fun getUserPhotos(userUd: UserId): Flow<List<GamePhoto>> {
        return gameLocalDataSource.getGames().map { games ->
            games.mapNotNull { game ->
                when (val photo = game.photo) {
                    null -> null
                    else -> GamePhoto(gameId = GameId(game.id), photo = photo)
                }
            }
        }
    }
}

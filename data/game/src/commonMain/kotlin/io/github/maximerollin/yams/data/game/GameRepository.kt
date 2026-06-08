package io.github.maximerollin.yams.data.game

import io.github.maximerollin.yams.core.database.GameLocalDataSource
import io.github.maximerollin.yams.core.database.GamePlayStateLocalDataSource
import io.github.maximerollin.yams.core.database.GameResultLocalDataSource
import io.github.maximerollin.yams.core.database.PlayerLocalDataSource
import io.github.maximerollin.yams.core.database.PlayerResultLocalDataSource
import io.github.maximerollin.yams.core.database.ScoreEntryLocalDataSource
import io.github.maximerollin.yams.core.database.TransactionRunner
import io.github.maximerollin.yams.core.database.entity.GameEntity
import io.github.maximerollin.yams.core.database.entity.GameStatusEntity
import io.github.maximerollin.yams.core.database.entity.PlayerEntity
import io.github.maximerollin.yams.core.file.FileEntity
import io.github.maximerollin.yams.core.file.FileLocalDataSource
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.data.game.mapper.asEntity
import io.github.maximerollin.yams.data.game.mapper.asExternalModel
import io.github.maximerollin.yams.data.game.model.CreateGame
import io.github.maximerollin.yams.data.game.model.CreateGameResult
import io.github.maximerollin.yams.data.game.model.CreatePlayerResult
import io.github.maximerollin.yams.data.game.model.GamePhoto
import io.github.maximerollin.yams.data.game.model.GamePlayState
import io.github.maximerollin.yams.data.game.model.GameResult
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
    public fun getGamePlayState(gameId: GameId): Flow<GamePlayState?>
    public fun getGameResult(gameId: GameId): Flow<GameResult?>
    public fun getGameResultsByPlayer(userId: UserId): Flow<List<GameResult>>
    public fun getFinishedGamesResults(): Flow<List<GameResult>>
    public fun getInProgressGamesPlayState(): Flow<List<GamePlayState>>
    public fun getGamePhoto(gameId: GameId): Flow<PlatformFile?>
    public fun getUserPhotos(): Flow<List<GamePhoto>>
    public fun getUserVictoryCount(userId: UserId): Flow<Int>
    public suspend fun createGame(value: CreateGame, isShuffled: Boolean): GameId
    public suspend fun finishGame(createGameResult: CreateGameResult)
    public suspend fun abandonGame(gameId: GameId)
    public suspend fun deleteGame(gameId: GameId)
    public suspend fun updateGamePhoto(gameId: GameId, photo: PlatformFile?)
    public suspend fun undoLastMove(gameId: GameId)
}

internal class DefaultGameRepository(
    private val transactionRunner: TransactionRunner,
    private val scoreEntryLocalDataSource: ScoreEntryLocalDataSource,
    private val gameLocalDataSource: GameLocalDataSource,
    private val gamePlayStateLocalDataSource: GamePlayStateLocalDataSource,
    private val gameResultLocalDataSource: GameResultLocalDataSource,
    private val playerLocalDataSource: PlayerLocalDataSource,
    private val playerResultLocalDataSource: PlayerResultLocalDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
    private val coroutineScope: CoroutineScope,
) : GameRepository {

    override fun getNumberOfGames(): Flow<Int> = gameLocalDataSource
        .getNumberOfGames()

    override fun getNumberOfFinishedGames(): Flow<Int> = gameLocalDataSource
        .getNumberOfFinishedGames()

    override fun getGamePlayState(gameId: GameId): Flow<GamePlayState?> =
        gamePlayStateLocalDataSource
            .getGamePlayState(gameId.value)
            .map { it?.asExternalModel() }

    override fun getGameResult(gameId: GameId): Flow<GameResult?> =
        gameResultLocalDataSource
            .getGameResult(gameId.value)
            .map { it?.asExternalModel() }

    override fun getGameResultsByPlayer(userId: UserId): Flow<List<GameResult>> =
        gameResultLocalDataSource
            .getFinishedGamesResultsByUser(userId.value)
            .map { results -> results.map { it.asExternalModel() } }

    override fun getFinishedGamesResults(): Flow<List<GameResult>> =
        gameResultLocalDataSource
            .getFinishedGamesResults()
            .map { results -> results.map { it.asExternalModel() } }

    override fun getInProgressGamesPlayState(): Flow<List<GamePlayState>> =
        gamePlayStateLocalDataSource
            .getInProgressGamesPlayStates()
            .map { states -> states.map { it.asExternalModel() } }

    override fun getGamePhoto(gameId: GameId): Flow<PlatformFile?> {
        return gameLocalDataSource.getGameById(gameId.value).map { game ->
            game?.photo
        }
    }

    override fun getUserVictoryCount(userId: UserId): Flow<Int> {
        return playerResultLocalDataSource.getVictoryCountByUser(userId.value)
    }

    override fun getUserPhotos(): Flow<List<GamePhoto>> {
        return gameLocalDataSource.getGames().map { games ->
            games.mapNotNull { game ->
                when (val photo = game.photo) {
                    null -> null
                    else -> GamePhoto(gameId = GameId(game.id), photo = photo)
                }
            }
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

    override suspend fun abandonGame(gameId: GameId) {
        val gameEntity = gameLocalDataSource.getGameById(gameId.value).first()
            ?: throw IllegalStateException("Game not found")

        if (gameEntity.status != GameStatusEntity.IN_PROGRESS) return

        gameLocalDataSource.updateGame(
            game = gameEntity.copy(
                status = GameStatusEntity.CANCELLED,
                updatedAt = Clock.System.now(),
                finishedAt = null,
            )
        )
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

    override suspend fun undoLastMove(gameId: GameId) {
        transactionRunner {
            scoreEntryLocalDataSource.deleteLastScoreEntryOfGame(gameId.value)

            val gameEntity = gameLocalDataSource.getGameById(gameId.value).first()
                ?: throw IllegalStateException("Game not found")

            if (gameEntity.status != GameStatusEntity.IN_PROGRESS) {
                gameLocalDataSource.updateGame(
                    game = GameEntity(
                        id = gameEntity.id,
                        settings = gameEntity.settings,
                        status = GameStatusEntity.IN_PROGRESS,
                        createdAt = gameEntity.createdAt,
                        updatedAt = Clock.System.now(),
                        finishedAt = null,
                        photo = gameEntity.photo,
                        gameNumber = gameEntity.gameNumber,
                    )
                )
            }
        }
    }

    override suspend fun finishGame(createGameResult: CreateGameResult) {
        val gameEntity = gameLocalDataSource.getGameById(createGameResult.gameId.value).first()
            ?: throw IllegalStateException("Game not found")

        val playerResults = createGameResult.playersResults.map(CreatePlayerResult::asEntity)

        transactionRunner {
            playerResultLocalDataSource.upsertAll(playerResults)

            gameLocalDataSource.updateGame(
                game = GameEntity(
                    id = createGameResult.gameId.value,
                    settings = gameEntity.settings,
                    status = GameStatusEntity.FINISHED,
                    createdAt = gameEntity.createdAt,
                    updatedAt = Clock.System.now(),
                    finishedAt = createGameResult.finishedAt,
                    photo = gameEntity.photo,
                    gameNumber = gameEntity.gameNumber,
                )
            )
        }
    }
}

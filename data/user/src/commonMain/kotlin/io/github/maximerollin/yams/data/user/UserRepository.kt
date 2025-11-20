package io.github.maximerollin.yams.data.user

import io.github.maximerollin.yams.core.database.UserLocalDataSource
import io.github.maximerollin.yams.core.database.entity.UserEntity
import io.github.maximerollin.yams.core.file.FileEntity
import io.github.maximerollin.yams.core.file.FileLocalDataSource
import io.github.maximerollin.yams.core.model.User
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.data.user.mapper.asExternalModel
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

public interface UserRepository {
    public fun getUsers(): Flow<List<User>>
    public fun getUsersByIds(ids: Set<UserId>): Flow<List<User>>
    public fun getUserById(id: UserId): Flow<User?>
    public suspend fun createUser(name: String, avatar: PlatformFile?): UserId
    public suspend fun updateUser(id: UserId, name: String, avatar: PlatformFile?): UserId
    public suspend fun deleteUser(id: UserId)
}

internal class DefaultUserRepository(
    private val userLocalDataSource: UserLocalDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
    private val coroutineScope: CoroutineScope,
) : UserRepository {
    override fun getUsers(): Flow<List<User>> = userLocalDataSource
        .getUsers()
        .map { it.map(UserEntity::asExternalModel) }

    override fun getUsersByIds(ids: Set<UserId>): Flow<List<User>> = userLocalDataSource
        .getUsersByIds(ids.map(UserId::value).toSet())
        .map { it.map(UserEntity::asExternalModel) }

    override fun getUserById(id: UserId): Flow<User?> = userLocalDataSource
        .getUserById(id.value)
        .map { it?.asExternalModel() }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createUser(name: String, avatar: PlatformFile?): UserId =
        createOrUpdateUser(
            id = UserId(Uuid.random().toString()),
            name = name,
            avatar = avatar,
        )

    override suspend fun updateUser(id: UserId, name: String, avatar: PlatformFile?): UserId =
        createOrUpdateUser(
            id = id,
            name = name,
            avatar = avatar,
        )

    override suspend fun deleteUser(id: UserId) {
        createOrUpdateUser(
            id = id,
            name = "☠\uFE0F",
            avatar = null,
        )

        userLocalDataSource.deleteUser(id.value)
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun createOrUpdateUser(
        id: UserId,
        name: String,
        avatar: PlatformFile?,
    ): UserId {
        return coroutineScope.async {
            val bytes = avatar?.exists()
                ?.let { fileLocalDataSource.compressPhoto(byteArray = avatar.readBytes()) }

            val savedPhoto = fileLocalDataSource.updateEntityFile(
                entity = FileEntity.User,
                entityId = id.value,
                byteArray = bytes,
            )

            userLocalDataSource.setUser(
                value = UserEntity(
                    id = id.value,
                    name = name,
                    avatar = savedPhoto,
                    createdAt = Clock.System.now()
                )
            )

            id
        }.await()
    }
}
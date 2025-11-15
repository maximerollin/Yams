package io.github.maximerollin.yams.core.database

import io.github.maximerollin.yams.core.database.dao.UserDao
import io.github.maximerollin.yams.core.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

public interface UserLocalDataSource {
    public fun getUsers(): Flow<List<UserEntity>>
    public fun getUsersByIds(ids: Set<String>): Flow<List<UserEntity>>
    public fun getUserById(id: String): Flow<UserEntity?>
    public suspend fun setUser(value: UserEntity)
    public suspend fun deleteUser(id: String)
}

internal class RoomUserLocalDataSource(
    private val userDao: UserDao
) : UserLocalDataSource {
    override fun getUsers(): Flow<List<UserEntity>> = userDao.getUsers()
    override fun getUsersByIds(ids: Set<String>): Flow<List<UserEntity>> = userDao.getUsersByIds(ids)
    override fun getUserById(id: String): Flow<UserEntity?> = userDao.getUserById(id)
    override suspend fun setUser(value: UserEntity) = userDao.setUser(value)
    override suspend fun deleteUser(id: String) = userDao.deleteUser(id)

}
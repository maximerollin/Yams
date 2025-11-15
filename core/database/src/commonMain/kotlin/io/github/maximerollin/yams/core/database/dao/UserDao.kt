package io.github.maximerollin.yams.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.maximerollin.yams.core.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface UserDao {
    @Query("SELECT * FROM User WHERE archived = 0 ORDER BY name ASC")
    fun getUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM User where id in (:ids) AND archived = 0 ORDER BY name ASC")
    fun getUsersByIds(ids: Set<String>): Flow<List<UserEntity>>

    @Query("SELECT * FROM User WHERE id = :id AND archived = 0")
    fun getUserById(id: String): Flow<UserEntity?>

    @Upsert
    suspend fun setUser(value: UserEntity)

    @Query("UPDATE User SET archived = 1 WHERE id = :id")
    suspend fun deleteUser(id: String)
}
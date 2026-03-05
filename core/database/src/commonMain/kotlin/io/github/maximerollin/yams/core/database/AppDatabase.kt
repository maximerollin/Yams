package io.github.maximerollin.yams.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import io.github.maximerollin.yams.core.database.converter.InstantConverter
import io.github.maximerollin.yams.core.database.converter.PlatformFileConverter
import io.github.maximerollin.yams.core.database.dao.GameDao
import io.github.maximerollin.yams.core.database.dao.PlayerDao
import io.github.maximerollin.yams.core.database.dao.PlayerResultDao
import io.github.maximerollin.yams.core.database.dao.UserDao
import io.github.maximerollin.yams.core.database.entity.GameEntity
import io.github.maximerollin.yams.core.database.entity.PlayerEntity
import io.github.maximerollin.yams.core.database.entity.PlayerResultEntity
import io.github.maximerollin.yams.core.database.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        GameEntity::class,
        PlayerEntity::class,
        PlayerResultEntity::class,
    ],
    version = 1,
)
@TypeConverters(
    PlatformFileConverter::class,
    InstantConverter::class,
)
@ConstructedBy(AppDatabaseConstructor::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun gameDao(): GameDao
    abstract fun playerDao(): PlayerDao
    abstract fun playerResultDao(): PlayerResultDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT", "KotlinNoActualForExpect")
internal expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

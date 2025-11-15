package io.github.maximerollin.yams.core.database

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import io.github.maximerollin.yams.core.database.converter.InstantConverter
import io.github.maximerollin.yams.core.database.converter.PlatformFileConverter
import io.github.maximerollin.yams.core.database.dao.GameDao
import io.github.maximerollin.yams.core.database.dao.UserDao
import io.github.maximerollin.yams.core.database.entity.GameEntity
import io.github.maximerollin.yams.core.database.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        GameEntity::class,
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
}

@Suppress("NO_ACTUAL_FOR_EXPECT", "KotlinNoActualForExpect")
internal expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

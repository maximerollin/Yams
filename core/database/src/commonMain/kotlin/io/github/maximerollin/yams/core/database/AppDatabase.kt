package io.github.maximerollin.yams.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import io.github.maximerollin.yams.core.database.converter.InstantConverter
import io.github.maximerollin.yams.core.database.converter.PlatformFileConverter
import io.github.maximerollin.yams.core.database.dao.GameDao
import io.github.maximerollin.yams.core.database.entity.GameEntity

@Database(
    entities = [
        GameEntity::class,
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    PlatformFileConverter::class,
    InstantConverter::class,
)
@ConstructedBy(AppDatabaseConstructor::class)
public abstract class AppDatabase : RoomDatabase() {
    public abstract fun gameDao(): GameDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT", "KotlinNoActualForExpect")
internal expect object AppDatabaseConstructor : RoomDatabaseConstructor<io.github.maximerollin.yams.core.database.AppDatabase> {
    override fun initialize(): io.github.maximerollin.yams.core.database.AppDatabase
}

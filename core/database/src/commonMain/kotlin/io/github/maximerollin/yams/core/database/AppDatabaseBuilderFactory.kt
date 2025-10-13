package io.github.maximerollin.yams.core.database

import androidx.room.RoomDatabase

internal expect class AppDatabaseBuilderFactory {
    fun createDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
}

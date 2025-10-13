package io.github.maximerollin.yams.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.databasesDir

internal actual class AppDatabaseBuilderFactory {
    actual fun createDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        return Room.databaseBuilder<AppDatabase>(
            name = PlatformFile(FileKit.databasesDir, "yams-room.db").absolutePath(),
        )
    }
}

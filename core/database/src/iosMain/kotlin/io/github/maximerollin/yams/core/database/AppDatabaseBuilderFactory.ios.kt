package io.github.maximerollin.yams.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.databasesDir
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.toKotlinxIoPath
import kotlinx.io.files.SystemFileSystem

internal actual class AppDatabaseBuilderFactory {
    actual fun createDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        if (!FileKit.databasesDir.exists()) {
            SystemFileSystem.createDirectories(FileKit.databasesDir.toKotlinxIoPath())
        }

        return Room.databaseBuilder<AppDatabase>(
            name = PlatformFile(FileKit.databasesDir, "yams-room.db").absolutePath(),
        )
    }
}


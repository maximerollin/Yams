package io.github.maximerollin.yams.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.databasesDir

internal actual class AppDatabaseBuilderFactory(private val context: Context) {
    actual fun createDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        return Room.databaseBuilder<AppDatabase>(
            context = context.applicationContext,
            name = PlatformFile(FileKit.databasesDir, "yams-room.db").absolutePath(),
        )
    }
}

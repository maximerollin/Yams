package io.github.maximerollin.yams.core.database.converter

import androidx.room.TypeConverter
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.resolve

internal class PlatformFileConverter {
    @TypeConverter
    fun fromPlatformFile(value: PlatformFile?): String? {
        // Store only the relative path from filesDir to avoid storing the full path
        val filesDirPath = FileKit.filesDir.toString()
        val filePath = value?.toString() ?: return null
        return if (filePath.startsWith(filesDirPath)) {
            filePath.substring(filesDirPath.length + 1) // +1 to remove the leading slash
        } else {
            filePath
        }
    }

    @TypeConverter
    fun toPlatformFile(value: String?): PlatformFile? {
        return value?.let { filePath ->
            FileKit.filesDir.resolve(filePath).let { file ->
                if (file.exists()) file else null
            }
        }
    }
}

package io.github.maximerollin.yams.core.file

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.list
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

public interface FileLocalDataSource {
    /**
     * Compress a photo.
     *
     * @param byteArray The byte array of the photo to compress.
     * @return The compressed photo byte array.
     */
    public suspend fun compressPhoto(
        byteArray: ByteArray
    ): ByteArray?

    /**
     * Update the file associated with an entity.
     *
     * @param entity The entity to update the file for.
     * @param entityId The ID of the entity to update the file for.
     * @param byteArray The byte array of the file to update.
     * @param extension The extension of the file to update.
     */
    public suspend fun updateEntityFile(
        entity: FileEntity,
        entityId: String,
        byteArray: ByteArray?,
        extension: String = "jpg",
    ): PlatformFile?

    /**
     * Delete all files associated with an entity.
     *
     * @param entity The entity to delete the files for.
     * @param entityId The ID of the entity to delete the files for.
     */
    public suspend fun deleteEntityFiles(entity: FileEntity, entityId: String)
}

internal class InternalFileLocalDataSource : FileLocalDataSource {
    override suspend fun compressPhoto(byteArray: ByteArray): ByteArray =
        FileKit.compressImage(
            bytes = byteArray,
            quality = 80,
            maxWidth = 800,
            maxHeight = 800
        )

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun updateEntityFile(
        entity: FileEntity,
        entityId: String,
        byteArray: ByteArray?,
        extension: String,
    ): PlatformFile? = withContext(Dispatchers.IO) {
        val entityDirectory = FileKit.filesDir / entity.folderName

        // Create the directory if it doesn't exist
        if (!entityDirectory.exists()) {
            entityDirectory.createDirectories()
        }

        // Delete the file if it already exists
        deleteEntityFiles(entity, entityId)

        // Save the file to the directory
        byteArray?.let {
            val fileId = Uuid.random().toString()
            val file = entityDirectory / "${entityId.short()}-${fileId.short()}.$extension"
            file write byteArray
            file
        }
    }

    override suspend fun deleteEntityFiles(entity: FileEntity, entityId: String) {
        withContext(Dispatchers.IO) {
            val entityDirectory = FileKit.filesDir / entity.folderName

            // Delete the file if it already exists
            entityDirectory.list()
                .filter { it.name.startsWith("${entityId.short()}-") }
                .forEach { it.delete() }
        }
    }

    private fun String.short(): String = when {
        this.length <= 8 -> this
        else -> this.substring(0, 8)
    }
}

public enum class FileEntity(public val folderName: String) {
    Game("games"),
    User("users");
}

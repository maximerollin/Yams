package io.github.maximerollin.yams.feature.user.edition.model

import io.github.maximerollin.yams.data.user.model.UserCreateAvatar
import io.github.vinceglb.filekit.PlatformFile
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.getDrawableResourceBytes
import org.jetbrains.compose.resources.getSystemResourceEnvironment

public sealed class Avatar {
    public data class Drawable(val drawable: DrawableResource) : Avatar()
    public data class File(val file: PlatformFile) : Avatar()
}

internal suspend fun Avatar.toUserCreateAvatar(): UserCreateAvatar = when (this) {
    is Avatar.Drawable -> UserCreateAvatar.Drawable(
        bytes = getDrawableResourceBytes(
            environment = getSystemResourceEnvironment(),
            resource = drawable
        )
    )

    is Avatar.File -> UserCreateAvatar.File(file)
}
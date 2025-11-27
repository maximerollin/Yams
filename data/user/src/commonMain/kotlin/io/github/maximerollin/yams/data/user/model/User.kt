package io.github.maximerollin.yams.data.user.model

import io.github.vinceglb.filekit.PlatformFile

public sealed class UserCreateAvatar {
    public class Drawable(public val bytes: ByteArray) : UserCreateAvatar()
    public data class File(val file: PlatformFile) : UserCreateAvatar()
}
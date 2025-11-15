package io.github.maximerollin.yams.core.model

import io.github.vinceglb.filekit.PlatformFile

public data class UserId(val value: String)

public data class User(
    val id: UserId,
    val name: String,
    val avatar: PlatformFile?,
)
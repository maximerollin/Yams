package io.github.maximerollin.yams.data.user.mapper

import io.github.maximerollin.yams.core.database.entity.UserEntity
import io.github.maximerollin.yams.core.model.User
import io.github.maximerollin.yams.core.model.UserId

internal fun UserEntity.asExternalModel(): User = User(
    id = UserId(id),
    name = name,
    avatar = avatar,
)
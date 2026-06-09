package io.github.maximerollin.yams.core.mocks

import io.github.maximerollin.yams.core.model.User
import io.github.maximerollin.yams.core.model.UserId

public object UserMocks {
    public fun generate(name: String): User = User(
        id = UserId(name),
        name = name,
        avatar = null,
    )

    public val users: List<User> = listOf(
        generate("Alpha"),
        generate("Beta"),
        generate("Charlie"),
        generate("Delta"),
        generate("Echo"),
    )
}

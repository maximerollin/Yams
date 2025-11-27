package io.github.maximerollin.yams.core.mocks

import io.github.maximerollin.yams.core.model.User
import io.github.maximerollin.yams.core.model.UserId
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.io.files.Path

public object UserMocks {
    public fun generate(name: String): User = User(
        id = UserId(name),
        name = name,
        avatar = PlatformFile(Path("https://api.dicebear.com/9.x/dylan/svg?scale=90&mood=happy&seed=${name}"))
    )

    public val users: List<User> = listOf(
        generate("Alpha"),
        generate("Beta"),
        generate("Charlie"),
        generate("Delta"),
        generate("Echo"),
    )
}
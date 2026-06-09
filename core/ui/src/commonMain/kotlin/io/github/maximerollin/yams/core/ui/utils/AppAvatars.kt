package io.github.maximerollin.yams.core.ui.utils

import org.jetbrains.compose.resources.DrawableResource
import yams.core.ui.generated.resources.Res
import yams.core.ui.generated.resources.avatar_1
import yams.core.ui.generated.resources.avatar_10
import yams.core.ui.generated.resources.avatar_11
import yams.core.ui.generated.resources.avatar_12
import yams.core.ui.generated.resources.avatar_13
import yams.core.ui.generated.resources.avatar_14
import yams.core.ui.generated.resources.avatar_2
import yams.core.ui.generated.resources.avatar_3
import yams.core.ui.generated.resources.avatar_4
import yams.core.ui.generated.resources.avatar_5
import yams.core.ui.generated.resources.avatar_6
import yams.core.ui.generated.resources.avatar_7
import yams.core.ui.generated.resources.avatar_8
import yams.core.ui.generated.resources.avatar_9

public val AppAvatars: List<DrawableResource> = listOf(
    Res.drawable.avatar_1,
    Res.drawable.avatar_2,
    Res.drawable.avatar_3,
    Res.drawable.avatar_4,
    Res.drawable.avatar_5,
    Res.drawable.avatar_6,
    Res.drawable.avatar_7,
    Res.drawable.avatar_8,
    Res.drawable.avatar_9,
    Res.drawable.avatar_10,
    Res.drawable.avatar_11,
    Res.drawable.avatar_12,
    Res.drawable.avatar_13,
    Res.drawable.avatar_14,
)

public fun appAvatarFor(key: String): DrawableResource =
    AppAvatars[key.stableAvatarIndex()]

private fun String.stableAvatarIndex(): Int {
    val value = fold(0) { accumulator, char ->
        ((accumulator * 31) + char.code) and Int.MAX_VALUE
    }
    return value % AppAvatars.size
}

package io.github.maximerollin.yams.data.game.model

import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.ScoreKey
import io.github.maximerollin.yams.core.model.UserId
import kotlin.time.Instant

public data class ScoreEntryId(val value: String)

public data class ScoreCellRef(
    val key: ScoreKey,
    val columnIndex: Int,
) {
    init {
        require(columnIndex >= 0) { "Column index must be positive or zero" }
    }
}

public data class ScoreEntry(
    val id: ScoreEntryId,
    val gameId: GameId,
    val userId: UserId,
    val cell: ScoreCellRef,
    val score: Int,
    val awardsExtraFiveOfAKindBonus: Boolean,
    val timestamp: Instant,
)

public data class CreateScoreEntry(
    val gameId: GameId,
    val userId: UserId,
    val cell: ScoreCellRef,
    val score: Int,
    val awardsExtraFiveOfAKindBonus: Boolean = false,
)

package io.github.maximerollin.yams.data.game.mapper

import io.github.maximerollin.yams.core.database.entity.ScoreEntryEntity
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.ScoreKey
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.data.game.model.ScoreCellRef
import io.github.maximerollin.yams.data.game.model.ScoreEntry
import io.github.maximerollin.yams.data.game.model.ScoreEntryId

internal fun ScoreEntryEntity.asExternalModel(): ScoreEntry = ScoreEntry(
    id = ScoreEntryId(value = id),
    gameId = GameId(value = gameId),
    userId = UserId(value = userId),
    cell = ScoreCellRef(
        key = ScoreKey(scoreKey),
        columnIndex = columnIndex,
    ),
    score = score,
    awardsExtraFiveOfAKindBonus = awardsExtraFiveOfAKindBonus,
    timestamp = timestamp,
)

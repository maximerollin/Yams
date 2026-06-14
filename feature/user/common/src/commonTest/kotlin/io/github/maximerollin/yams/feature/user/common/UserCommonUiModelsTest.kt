@file:OptIn(kotlin.time.ExperimentalTime::class)

package io.github.maximerollin.yams.feature.user.common

import io.github.maximerollin.yams.core.model.Game
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.data.game.model.GameResult
import io.github.maximerollin.yams.data.game.model.Player
import io.github.maximerollin.yams.data.game.model.PlayerResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class UserCommonUiModelsTest {
    @Test
    fun homeStatsNormalizeTwoColumnScoresForHighestScore() {
        val alpha = playerResult(
            gameId = GameId("one-column"),
            userId = UserId("alpha"),
            name = "Alpha",
            finalScore = 300,
        )
        val beta = playerResult(
            gameId = GameId("two-columns"),
            userId = UserId("beta"),
            name = "Beta",
            finalScore = 520,
        )

        val stats = listOf(
            gameResult(alpha, columnCount = 1),
            gameResult(beta, columnCount = 2),
        ).toHomeStats()

        assertEquals(300, stats.highestScore)
        assertEquals("Alpha", stats.highestScorePlayerName)
    }

    @Test
    fun userStatsNormalizeTwoColumnScoresForAverageAndHighestScore() {
        val userId = UserId("alpha")
        val firstGame = playerResult(
            gameId = GameId("first"),
            userId = userId,
            name = "Alpha",
            finalScore = 200,
            isWinner = false,
        )
        val secondGame = playerResult(
            gameId = GameId("second"),
            userId = userId,
            name = "Alpha",
            finalScore = 600,
            isWinner = true,
        )

        val stats = listOf(
            gameResult(firstGame, columnCount = 1),
            gameResult(secondGame, columnCount = 2),
        ).statsForUser(userId)

        assertEquals(2, stats.gamesPlayed)
        assertEquals(1, stats.victories)
        assertEquals(250f, stats.averageScore)
        assertEquals(300, stats.highestScore)
    }

    private fun gameResult(
        playerResult: PlayerResult,
        columnCount: Int,
    ): GameResult =
        GameResult(
            game = Game.GameFinished(
                id = playerResult.gameId,
                settings = GameSettings.YamsSettings(columnCount = columnCount),
                startedAt = Instant.parse("2026-01-01T00:00:00Z"),
                finishedAt = Instant.parse("2026-01-01T01:00:00Z"),
                photo = null,
                gameNumber = 1,
                yamCount = playerResult.numberOfFiveOfAKind,
            ),
            playersResults = listOf(playerResult),
        )

    private fun playerResult(
        gameId: GameId,
        userId: UserId,
        name: String,
        finalScore: Int,
        isWinner: Boolean = true,
    ): PlayerResult =
        PlayerResult(
            gameId = gameId,
            player = Player(
                userId = userId,
                name = name,
                avatar = null,
                gameId = gameId,
                userIndex = 0,
            ),
            rank = if (isWinner) 1 else 2,
            finalScore = finalScore,
            numberOfTurn = 13,
            rawScore = finalScore,
            isWinner = isWinner,
            numberOfFiveOfAKind = 0,
        )
}

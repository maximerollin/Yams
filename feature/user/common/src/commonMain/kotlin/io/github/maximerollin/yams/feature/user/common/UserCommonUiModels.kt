@file:OptIn(kotlin.time.ExperimentalTime::class)

package io.github.maximerollin.yams.feature.user.common

import io.github.maximerollin.yams.core.model.Game
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.User
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.data.game.model.GameResult
import io.github.vinceglb.filekit.PlatformFile
import kotlin.math.round
import kotlin.time.Instant

public data class HomeStatsUiState(
    val gamesPlayed: Int,
    val totalYams: Int,
    val averageYamsPerGame: Float,
    val highestScore: Int,
    val highestScorePlayerName: String?,
)

public data class UserCardUiState(
    val user: User,
    val stats: UserStatsUiState,
)

public data class UserStatsUiState(
    val gamesPlayed: Int,
    val victories: Int,
    val totalYams: Int,
    val averageScore: Float,
    val highestScore: Int,
)

public data class GameSummaryUiState(
    val gameId: GameId,
    val gameNumber: Int?,
    val finishedAt: Instant,
    val photo: PlatformFile?,
    val players: List<PlayerSummaryUiState>,
    val topPlayers: List<PlayerSummaryUiState>,
    val totalPlayers: Int,
    val totalYams: Int,
    val highestScore: Int,
) {
    public val winner: PlayerSummaryUiState? = topPlayers.firstOrNull { it.isWinner }
        ?: topPlayers.firstOrNull()
}

public data class PlayerSummaryUiState(
    val userId: UserId,
    val name: String,
    val avatar: PlatformFile?,
    val rank: Int,
    val score: Int,
    val yams: Int,
    val isWinner: Boolean,
)

public fun List<GameResult>.toGameSummaries(): List<GameSummaryUiState> =
    mapNotNull(GameResult::toGameSummary)

public fun List<GameResult>.toHomeStats(): HomeStatsUiState {
    val playerResults = flatMap { it.playersResults }
    val totalYams = playerResults.sumOf { it.numberOfFiveOfAKind }
    val highestScoreResult = playerResults.maxByOrNull { it.finalScore }

    return HomeStatsUiState(
        gamesPlayed = size,
        totalYams = totalYams,
        averageYamsPerGame = if (isNotEmpty()) totalYams.toFloat() / size else 0f,
        highestScore = highestScoreResult?.finalScore ?: 0,
        highestScorePlayerName = highestScoreResult?.player?.name,
    )
}

public fun List<GameResult>.statsForUser(userId: UserId): UserStatsUiState {
    val playerResults = flatMap { result ->
        result.playersResults.filter { playerResult ->
            playerResult.player.userId == userId
        }
    }
    val totalScore = playerResults.sumOf { it.finalScore }

    return UserStatsUiState(
        gamesPlayed = playerResults.size,
        victories = playerResults.count { it.isWinner },
        totalYams = playerResults.sumOf { it.numberOfFiveOfAKind },
        averageScore = if (playerResults.isNotEmpty()) {
            totalScore.toFloat() / playerResults.size
        } else {
            0f
        },
        highestScore = playerResults.maxOfOrNull { it.finalScore } ?: 0,
    )
}

public fun Float.formatOneDecimal(): String {
    val rounded = round(this * 10f) / 10f
    return if (rounded % 1f == 0f) {
        rounded.toInt().toString()
    } else {
        rounded.toString()
    }
}

private fun GameResult.toGameSummary(): GameSummaryUiState? {
    val finishedGame = game as? Game.GameFinished ?: return null
    val sortedPlayers = playersResults.sortedWith(
        compareBy<io.github.maximerollin.yams.data.game.model.PlayerResult> { it.rank }
            .thenByDescending { it.finalScore }
            .thenBy { it.player.userIndex }
    )

    return GameSummaryUiState(
        gameId = finishedGame.id,
        gameNumber = finishedGame.gameNumber,
        finishedAt = finishedGame.finishedAt,
        photo = finishedGame.photo,
        players = sortedPlayers.map { playerResult ->
            PlayerSummaryUiState(
                userId = playerResult.player.userId,
                name = playerResult.player.name,
                avatar = playerResult.player.avatar,
                rank = playerResult.rank,
                score = playerResult.finalScore,
                yams = playerResult.numberOfFiveOfAKind,
                isWinner = playerResult.isWinner,
            )
        },
        topPlayers = sortedPlayers.take(3).map { playerResult ->
            PlayerSummaryUiState(
                userId = playerResult.player.userId,
                name = playerResult.player.name,
                avatar = playerResult.player.avatar,
                rank = playerResult.rank,
                score = playerResult.finalScore,
                yams = playerResult.numberOfFiveOfAKind,
                isWinner = playerResult.isWinner,
            )
        },
        totalPlayers = playersResults.size,
        totalYams = playersResults.sumOf { it.numberOfFiveOfAKind },
        highestScore = sortedPlayers.firstOrNull()?.finalScore ?: 0,
    )
}

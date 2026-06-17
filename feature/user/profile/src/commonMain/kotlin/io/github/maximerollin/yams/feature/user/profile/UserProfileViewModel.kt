package io.github.maximerollin.yams.feature.user.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.maximerollin.yams.core.analytics.AnalyticsTracker
import io.github.maximerollin.yams.core.model.Game
import io.github.maximerollin.yams.core.model.User
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.data.billing.BillingRepository
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.data.game.model.GameResult
import io.github.maximerollin.yams.data.game.model.PlayerResult
import io.github.maximerollin.yams.data.user.UserRepository
import io.github.maximerollin.yams.feature.user.common.UserStatsUiState
import io.github.maximerollin.yams.feature.user.common.statsForUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import kotlin.math.abs
import kotlin.math.roundToInt

internal class UserProfileViewModel(
    @InjectedParam private val userId: UserId,
    private val userRepository: UserRepository,
    gameRepository: GameRepository,
    billingRepository: BillingRepository,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {
    val uiState: StateFlow<UserProfileUiState> = combine(
        userRepository.getUserById(userId),
        gameRepository.getGameResultsByPlayer(userId),
        billingRepository.getYamsPlusStatus(),
    ) { user, results, isPremium ->
        when (user) {
            null -> UserProfileUiState.NotFound
            else -> UserProfileUiState.Success(
                user = user,
                stats = results.statsForUser(user.id),
                detailedStats = results.detailedStatsForUser(user.id),
                scoreTrend = results.scoreTrendForUser(user.id),
                isPremium = isPremium,
            )
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserProfileUiState.Loading,
        )

    private val _navigationTarget = MutableStateFlow<UserProfileNavigationTarget?>(null)
    val navigationTarget: StateFlow<UserProfileNavigationTarget?> = _navigationTarget

    fun deleteUser() {
        viewModelScope.launch {
            userRepository.deleteUser(userId)
            analyticsTracker.capture(event = "player deleted")
            _navigationTarget.value = UserProfileNavigationTarget.USERS
        }
    }

    fun onNavigationHandled() {
        _navigationTarget.value = null
    }
}

internal enum class UserProfileNavigationTarget {
    USERS,
}

internal sealed interface UserProfileUiState {
    data object Loading : UserProfileUiState
    data object NotFound : UserProfileUiState
    data class Success(
        val user: User,
        val stats: UserStatsUiState,
        val detailedStats: UserProfileDetailedStatsUiState,
        val scoreTrend: List<UserProfileScoreTrendPoint>,
        val isPremium: Boolean,
    ) : UserProfileUiState
}

internal data class UserProfileDetailedStatsUiState(
    val averagePointsPerTurn: Float,
    val averageRank: Float,
    val averageYamsPerGame: Float,
    val recentAverageScore: Float,
    val recentWinRatePercent: Int,
    val currentWinStreak: Int,
    val bestWinStreak: Int,
    val yamsGameRatePercent: Int,
    val podiumRatePercent: Int,
    val averageTurnsPerGame: Float,
    val averageScoreDeviation: Float,
)

internal data class UserProfileScoreTrendPoint(
    val rank: Int,
    val averagePointsPerTurn: Float,
)

private const val SCORE_TREND_GAME_LIMIT = 7
private const val RECENT_STATS_GAME_LIMIT = 5

private fun List<GameResult>.detailedStatsForUser(userId: UserId): UserProfileDetailedStatsUiState {
    val playerStatResults = playerStatResultsForUser(userId)
    val playerResults = playerStatResults.map(ProfilePlayerStatResult::playerResult)
    val totalTurns = playerResults.sumOf { it.numberOfTurn }
    val totalRawScore = playerResults.sumOf { it.rawScore }
    val totalRank = playerResults.sumOf { it.rank }
    val totalYams = playerResults.sumOf { it.numberOfFiveOfAKind }
    val gamesPlayed = playerResults.size
    val recentResults = playerStatResults.take(RECENT_STATS_GAME_LIMIT)
    val averageScore = playerStatResults.averageScore()

    return UserProfileDetailedStatsUiState(
        averagePointsPerTurn = if (totalTurns > 0) {
            totalRawScore.toFloat() / totalTurns
        } else {
            0f
        },
        averageRank = if (gamesPlayed > 0) {
            totalRank.toFloat() / gamesPlayed
        } else {
            0f
        },
        averageYamsPerGame = if (gamesPlayed > 0) {
            totalYams.toFloat() / gamesPlayed
        } else {
            0f
        },
        recentAverageScore = recentResults.averageScore(),
        recentWinRatePercent = recentResults.count { it.playerResult.isWinner }
            .percentOf(recentResults.size),
        currentWinStreak = playerResults.takeWhile { it.isWinner }.size,
        bestWinStreak = playerResults.bestWinStreak(),
        yamsGameRatePercent = playerResults.count { it.numberOfFiveOfAKind > 0 }
            .percentOf(gamesPlayed),
        podiumRatePercent = playerStatResults.count { it.playerResult.rank <= 3 }
            .percentOf(gamesPlayed),
        averageTurnsPerGame = if (gamesPlayed > 0) {
            totalTurns.toFloat() / gamesPlayed
        } else {
            0f
        },
        averageScoreDeviation = if (playerStatResults.isNotEmpty()) {
            playerStatResults
                .map { abs(it.score - averageScore) }
                .average()
                .toFloat()
        } else {
            0f
        },
    )
}

private fun List<GameResult>.scoreTrendForUser(userId: UserId): List<UserProfileScoreTrendPoint> =
    asReversed()
        .mapNotNull { result ->
            if (result.game !is Game.GameFinished) return@mapNotNull null
            val playerResult = result.playerResultForUser(userId) ?: return@mapNotNull null
            if (playerResult.scorePerTurn < 0f) return@mapNotNull null

            UserProfileScoreTrendPoint(
                rank = playerResult.rank,
                averagePointsPerTurn = playerResult.scorePerTurn,
            )
        }
        .takeLast(SCORE_TREND_GAME_LIMIT)

private fun GameResult.playerResultForUser(userId: UserId): PlayerResult? =
    playersResults.firstOrNull { it.player.userId == userId }

private data class ProfilePlayerStatResult(
    val playerResult: PlayerResult,
    val score: Float,
)

private fun List<GameResult>.playerStatResultsForUser(userId: UserId): List<ProfilePlayerStatResult> =
    mapNotNull { result ->
        val columnCount = result.game.settings.columnCount.coerceAtLeast(1)
        val playerResult = result.playerResultForUser(userId) ?: return@mapNotNull null
        ProfilePlayerStatResult(
            playerResult = playerResult,
            score = playerResult.finalScore.toFloat() / columnCount,
        )
    }

private fun List<ProfilePlayerStatResult>.averageScore(): Float =
    if (isNotEmpty()) {
        map { it.score }.average().toFloat()
    } else {
        0f
    }

private fun List<PlayerResult>.bestWinStreak(): Int {
    var bestStreak = 0
    var currentStreak = 0
    forEach { result ->
        if (result.isWinner) {
            currentStreak += 1
            bestStreak = maxOf(bestStreak, currentStreak)
        } else {
            currentStreak = 0
        }
    }
    return bestStreak
}

private fun Int.percentOf(total: Int): Int =
    if (total > 0) {
        (toFloat() / total * 100f).roundToInt()
    } else {
        0
    }

package io.github.maximerollin.yams.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.maximerollin.yams.core.analytics.AnalyticsTracker
import io.github.maximerollin.yams.core.analytics.toAnalyticsCountBucket
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.review.InAppReview
import io.github.maximerollin.yams.data.billing.BillingRepository
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.data.game.model.GamePlayState
import io.github.maximerollin.yams.data.preference.GamePlayUiDensity
import io.github.maximerollin.yams.data.preference.PreferenceRepository
import io.github.maximerollin.yams.feature.user.common.toGameSummaries
import io.github.maximerollin.yams.feature.user.common.toHomeStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

internal class HomeViewModel(
    private val gameRepository: GameRepository,
    billingRepository: BillingRepository,
    private val preferenceRepository: PreferenceRepository,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = combine(
        gameRepository.getFinishedGamesResults(),
        gameRepository.getInProgressGamesPlayState(),
        billingRepository.getYamsPlusStatus(),
        preferenceRepository.getIsInAppReviewPending(),
        preferenceRepository.getInAppReviewShownDate(),
    ) { results, inProgressGames, isPremium, isInAppReviewPending, lastInAppReviewShownDate ->
        HomeUiState.Success(
            stats = results.toHomeStats(),
            recentGames = results.toGameSummaries().take(8),
            activeGame = inProgressGames.firstOrNull()?.toActiveGameUiState(),
            isPremium = isPremium,
            canRequestInAppReview = shouldRequestInAppReview(
                isInAppReviewPending = isInAppReviewPending,
                numberOfFinishedGames = results.size,
                lastInAppReviewShownDate = lastInAppReviewShownDate,
            ),
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading,
        )

    val isHapticFeedbackEnabled: StateFlow<Boolean> =
        preferenceRepository.getIsHapticFeedbackEnabled()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = true,
            )

    val gamePlayUiDensity: StateFlow<GamePlayUiDensity> =
        preferenceRepository.getGamePlayUiDensity()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = GamePlayUiDensity.NORMAL,
            )

    val hasSeenInAppReviewDiscovery: StateFlow<Boolean?> =
        preferenceRepository.getHasSeenInAppReviewDiscovery()
            .map<Boolean, Boolean?> { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )

    fun setIsHapticFeedbackEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            preferenceRepository.setIsHapticFeedbackEnabled(isEnabled)
        }
    }

    fun setGamePlayUiDensity(density: GamePlayUiDensity) {
        viewModelScope.launch {
            preferenceRepository.setGamePlayUiDensity(density)
        }
    }

    fun onInAppReviewDiscoverySeen() {
        viewModelScope.launch {
            preferenceRepository.setHasSeenInAppReviewDiscovery()
        }
    }

    fun onInAppReviewDiscoveryAcknowledged() {
        viewModelScope.launch {
            preferenceRepository.setHasSeenInAppReviewDiscovery()
            val numberOfFinishedGames = gameRepository.getNumberOfFinishedGames().first()
            analyticsTracker.capture(
                event = "review discovery acknowledged",
                properties = mapOf(
                    "finished_games_bucket" to numberOfFinishedGames.toAnalyticsCountBucket(),
                ),
            )
        }
    }

    fun openInAppReviewDialog() {
        viewModelScope.launch {
            val numberOfFinishedGames = gameRepository.getNumberOfFinishedGames().first()
            analyticsTracker.capture(
                event = "review dialog opened",
                properties = mapOf(
                    "finished_games_bucket" to numberOfFinishedGames.toAnalyticsCountBucket(),
                ),
            )
        }
    }

    fun requestInAppReview() {
        viewModelScope.launch {
            requestInAppReviewIfEligible()
        }
    }

    fun onInAppReviewPostponed() {
        viewModelScope.launch {
            val numberOfFinishedGames = gameRepository.getNumberOfFinishedGames().first()
            analyticsTracker.capture(
                event = "review postponed",
                properties = mapOf(
                    "finished_games_bucket" to numberOfFinishedGames.toAnalyticsCountBucket(),
                ),
            )
        }
    }

    fun abandonGame(gameId: GameId) {
        viewModelScope.launch {
            val state = gameRepository.getGamePlayState(gameId).first()
            gameRepository.abandonGame(gameId)
            analyticsTracker.capture(
                event = "game abandoned",
                properties = mapOf(
                    "rule_set" to state?.game?.settings?.ruleSet?.name?.lowercase(),
                    "player_count_bucket" to state?.players?.size?.toAnalyticsCountBucket(),
                    "score_entries_bucket" to state?.scoreEntries?.size?.toAnalyticsCountBucket(),
                ),
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun requestInAppReviewIfEligible() {
        val numberOfFinishedGames = gameRepository.getNumberOfFinishedGames().first()
        val isInAppReviewPending = preferenceRepository.getIsInAppReviewPending().first()
        val lastInAppReviewShownDate = preferenceRepository.getInAppReviewShownDate().first()
        val shouldRequestInAppReview = shouldRequestInAppReview(
            isInAppReviewPending = isInAppReviewPending,
            numberOfFinishedGames = numberOfFinishedGames,
            lastInAppReviewShownDate = lastInAppReviewShownDate,
        )

        if (!shouldRequestInAppReview) {
            preferenceRepository.setIsInAppReviewPending(false)
            return
        }

        InAppReview.requestReview()
        preferenceRepository.inAppReviewShown()
        preferenceRepository.setIsInAppReviewPending(false)
        analyticsTracker.capture(
            event = "review prompt shown",
            properties = mapOf(
                "finished_games_bucket" to numberOfFinishedGames.toAnalyticsCountBucket(),
            ),
        )
    }
}

private const val IN_APP_REVIEW_MIN_FINISHED_GAMES = 3
private const val IN_APP_REVIEW_COOLDOWN_DAYS = 30 * 6

@OptIn(ExperimentalTime::class)
internal fun shouldRequestInAppReview(
    isInAppReviewPending: Boolean,
    numberOfFinishedGames: Int,
    lastInAppReviewShownDate: Instant?,
    now: Instant = Clock.System.now(),
): Boolean {
    if (!isInAppReviewPending || numberOfFinishedGames < IN_APP_REVIEW_MIN_FINISHED_GAMES) {
        return false
    }

    return lastInAppReviewShownDate?.let { shownDate ->
        now > shownDate + IN_APP_REVIEW_COOLDOWN_DAYS.days
    } ?: true
}

private fun GamePlayState.toActiveGameUiState(): ActiveGameUiState =
    ActiveGameUiState(
        gameId = game.id,
        playerCount = players.size,
        players = players
            .sortedBy { it.userIndex }
            .map { player ->
                ActiveGamePlayerUiState(
                    name = player.name,
                    avatar = player.avatar,
                )
            },
    )

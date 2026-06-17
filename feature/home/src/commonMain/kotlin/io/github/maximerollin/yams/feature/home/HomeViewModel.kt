package io.github.maximerollin.yams.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.maximerollin.yams.core.analytics.AnalyticsTracker
import io.github.maximerollin.yams.core.analytics.toAnalyticsCountBucket
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.data.billing.BillingRepository
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.data.game.model.GamePlayState
import io.github.maximerollin.yams.feature.user.common.toGameSummaries
import io.github.maximerollin.yams.feature.user.common.toHomeStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class HomeViewModel(
    private val gameRepository: GameRepository,
    billingRepository: BillingRepository,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = combine(
        gameRepository.getFinishedGamesResults(),
        gameRepository.getInProgressGamesPlayState(),
        billingRepository.getYamsPlusStatus(),
    ) { results, inProgressGames, isPremium ->
        HomeUiState.Success(
            stats = results.toHomeStats(),
            recentGames = results.toGameSummaries().take(8),
            activeGame = inProgressGames.firstOrNull()?.toActiveGameUiState(),
            isPremium = isPremium,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading,
        )

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

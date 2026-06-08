package io.github.maximerollin.yams.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.feature.user.common.toGameSummaries
import io.github.maximerollin.yams.feature.user.common.toHomeStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class HomeViewModel(
    gameRepository: GameRepository,
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = gameRepository
        .getFinishedGamesResults()
        .map { results ->
            HomeUiState.Success(
                stats = results.toHomeStats(),
                recentGames = results.toGameSummaries().take(8),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading,
        )
}

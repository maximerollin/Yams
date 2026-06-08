package io.github.maximerollin.yams.feature.home

import io.github.maximerollin.yams.feature.user.common.GameSummaryUiState
import io.github.maximerollin.yams.feature.user.common.HomeStatsUiState

internal sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val stats: HomeStatsUiState,
        val recentGames: List<GameSummaryUiState>,
    ) : HomeUiState
}

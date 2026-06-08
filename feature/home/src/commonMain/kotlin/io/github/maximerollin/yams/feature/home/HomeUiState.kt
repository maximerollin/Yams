package io.github.maximerollin.yams.feature.home

import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.feature.user.common.GameSummaryUiState
import io.github.maximerollin.yams.feature.user.common.HomeStatsUiState
import io.github.vinceglb.filekit.PlatformFile

internal sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val stats: HomeStatsUiState,
        val recentGames: List<GameSummaryUiState>,
        val activeGame: ActiveGameUiState?,
    ) : HomeUiState
}

internal data class ActiveGameUiState(
    val gameId: GameId,
    val playerCount: Int,
    val players: List<ActiveGamePlayerUiState>,
)

internal data class ActiveGamePlayerUiState(
    val name: String,
    val avatar: PlatformFile?,
)

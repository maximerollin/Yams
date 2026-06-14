package io.github.maximerollin.yams.feature.user.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.maximerollin.yams.core.model.User
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.data.billing.BillingRepository
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.data.user.UserRepository
import io.github.maximerollin.yams.feature.user.common.GameSummaryUiState
import io.github.maximerollin.yams.feature.user.common.toGameSummaries
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.koin.core.annotation.InjectedParam

internal class UserHistoryViewModel(
    @InjectedParam private val userId: UserId,
    userRepository: UserRepository,
    gameRepository: GameRepository,
    billingRepository: BillingRepository,
) : ViewModel() {
    val uiState: StateFlow<UserHistoryUiState> = combine(
        userRepository.getUserById(userId),
        gameRepository.getGameResultsByPlayer(userId),
        billingRepository.getYamsPlusStatus(),
    ) { user, results, isPremium ->
        when (user) {
            null -> UserHistoryUiState.NotFound
            else -> UserHistoryUiState.Success(
                user = user,
                games = results.toGameSummaries(),
                isPremium = isPremium,
            )
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserHistoryUiState.Loading,
        )
}

internal sealed interface UserHistoryUiState {
    data object Loading : UserHistoryUiState
    data object NotFound : UserHistoryUiState
    data class Success(
        val user: User,
        val games: List<GameSummaryUiState>,
        val isPremium: Boolean,
    ) : UserHistoryUiState
}

package io.github.maximerollin.yams.feature.user.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.maximerollin.yams.core.model.User
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.data.user.UserRepository
import io.github.maximerollin.yams.feature.user.common.GameSummaryUiState
import io.github.maximerollin.yams.feature.user.common.UserStatsUiState
import io.github.maximerollin.yams.feature.user.common.statsForUser
import io.github.maximerollin.yams.feature.user.common.toGameSummaries
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam

internal class UserProfileViewModel(
    @InjectedParam private val userId: UserId,
    private val userRepository: UserRepository,
    gameRepository: GameRepository,
) : ViewModel() {
    val uiState: StateFlow<UserProfileUiState> = combine(
        userRepository.getUserById(userId),
        gameRepository.getGameResultsByPlayer(userId),
    ) { user, results ->
        when (user) {
            null -> UserProfileUiState.NotFound
            else -> UserProfileUiState.Success(
                user = user,
                stats = results.statsForUser(user.id),
                recentGames = results.toGameSummaries().take(5),
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
        val recentGames: List<GameSummaryUiState>,
    ) : UserProfileUiState
}

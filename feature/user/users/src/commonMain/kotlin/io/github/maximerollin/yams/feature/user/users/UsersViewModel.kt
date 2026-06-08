package io.github.maximerollin.yams.feature.user.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.data.user.UserRepository
import io.github.maximerollin.yams.feature.user.common.UserCardUiState
import io.github.maximerollin.yams.feature.user.common.statsForUser
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

internal class UsersViewModel(
    userRepository: UserRepository,
    gameRepository: GameRepository,
) : ViewModel() {
    val uiState: StateFlow<UsersUiState> = combine(
        userRepository.getUsers(),
        gameRepository.getFinishedGamesResults(),
    ) { users, results ->
        UsersUiState.Success(
            users = users
                .sortedBy { it.name.lowercase() }
                .map { user ->
                    UserCardUiState(
                        user = user,
                        stats = results.statsForUser(user.id),
                    )
                }
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UsersUiState.Loading,
        )
}

internal sealed interface UsersUiState {
    data object Loading : UsersUiState
    data class Success(val users: List<UserCardUiState>) : UsersUiState
}

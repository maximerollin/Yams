package io.github.maximerollin.yams.feature.game.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.maximerollin.yams.core.model.User
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.data.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class GameCreationViewModel(
    gameRepository: GameRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    val usersUiState: StateFlow<UserUiState> = userRepository.getUsers()
        .map { UserUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserUiState.Loading,
        )

    val gamesNumber: StateFlow<Int> = gameRepository
        .getNumberOfGames()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0,
        )

    private val _gameCreationUiState = MutableStateFlow(GameCreationUiState())
    val gameCreationUiState = _gameCreationUiState.asStateFlow()

    fun selectUser(id: UserId) {
        _gameCreationUiState.update { currentState ->
            val newList = when (!currentState.selectedUsersIds.contains(id)) {
                true -> currentState.selectedUsersIds + id
                false -> currentState.selectedUsersIds
            }
            currentState.copy(selectedUsersIds = newList)
        }
    }

    fun toggleUser(id: UserId) {
        _gameCreationUiState.update { currentState ->
            val newList = when (currentState.selectedUsersIds.contains(id)) {
                true -> currentState.selectedUsersIds - id
                else -> currentState.selectedUsersIds + id
            }
            currentState.copy(selectedUsersIds = newList)
        }
    }

    fun deleteUser(id: UserId) {
        viewModelScope.launch {
            _gameCreationUiState.update { currentState ->
                val newList = currentState.selectedUsersIds - id
                currentState.copy(selectedUsersIds = newList)
            }

            userRepository.deleteUser(id)
        }
    }
}

internal sealed interface UserUiState {
    data object Loading : UserUiState
    data class Success(val users: List<User>) : UserUiState
}

internal data class GameCreationUiState(
    val selectedUsersIds: Set<UserId> = emptySet()
)
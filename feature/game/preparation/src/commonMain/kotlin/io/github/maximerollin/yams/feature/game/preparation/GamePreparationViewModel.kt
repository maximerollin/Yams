package io.github.maximerollin.yams.feature.game.preparation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.core.model.User
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.data.preference.PreferenceRepository
import io.github.maximerollin.yams.data.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam

internal class GamePreparationViewModel(
    @InjectedParam private val usersIds: Set<UserId>,
    private val gameRepository: GameRepository,
    private val preferenceRepository: PreferenceRepository,
    userRepository: UserRepository,
) : ViewModel() {
    private val _gamePreparationUiState = MutableStateFlow(GamePreparationUiState())
    val gamePreparationUiState = _gamePreparationUiState.asStateFlow()

    private val orderedUsers: MutableStateFlow<List<UserId>> = MutableStateFlow(usersIds.shuffled())

    val usersState: StateFlow<List<User>> = combine(
        userRepository.getUsersByIds(usersIds),
        orderedUsers,
    ) { allUsers, orderedIds ->
        orderedIds.mapNotNull { userId -> allUsers.find { it.id == userId } }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    init {
        initStateFromPreferences()
    }

    private fun initStateFromPreferences() {
        viewModelScope.launch {
            val isUserOrderRandomized = preferenceRepository.getIsUserOrderRandomized().first()
            val gameSettings = preferenceRepository.getGameSettings().first()
            _gamePreparationUiState.update {
                it.copy(
                    isUserOrderRandomized = isUserOrderRandomized,
                    gameSettings = gameSettings,
                )
            }
        }
    }

    fun createGame() {
        val gameSettings = gamePreparationUiState.value.gameSettings

        if (gamePreparationUiState.value.createGameLoading) {
            return
        }

        _gamePreparationUiState.update { it.copy(createGameLoading = true) }

        viewModelScope.launch {
            // Save game settings preferences
            preferenceRepository.setGameSettings(gameSettings)
            preferenceRepository.setIsUserOrderRandomized(gamePreparationUiState.value.isUserOrderRandomized)

            // TODO create game

            // TODO update navigatetogame id
            // _gamePreparationUiState.update { it.copy(navigateToGame = ) }
        }
    }

    fun onToggleIsUserOrderRandomized(isUserOrderRandomized: Boolean) {
        _gamePreparationUiState.update { it.copy(isUserOrderRandomized = isUserOrderRandomized) }
    }

    fun orderUser(from: Int, to: Int) {
        orderedUsers.update { state ->
            state.toMutableList().apply {
                add(to, removeAt(from))
            }
        }
    }

    fun onToggleGameSettings(gameSettings: GameSettings.RuleSet) {
        val updatedSettings =
            when (gameSettings) {
                GameSettings.RuleSet.YAHTZEE -> GameSettings.YahtzeeSettings()
                GameSettings.RuleSet.YAMS -> GameSettings.YamsSettings()
                GameSettings.RuleSet.CUSTOM -> GameSettings.CustomSettings()
            }
        _gamePreparationUiState.update { state ->
            state.copy(gameSettings = updatedSettings)
        }
    }

    fun onUpdateGameSettings(gameSettings: GameSettings) {
        _gamePreparationUiState.update { state ->
            state.copy(
                gameSettings = gameSettings,
            )
        }
    }
}

internal data class GamePreparationUiState(
    val navigateToGame: GameId? = null,
    val createGameLoading: Boolean = false,
    val isUserOrderRandomized: Boolean = true,
    val gameSettings: GameSettings = GameSettings.YamsSettings(),
)

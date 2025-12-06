package io.github.maximerollin.yams.feature.game.preparation

import androidx.lifecycle.ViewModel
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.data.preference.PreferenceRepository
import io.github.maximerollin.yams.data.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.InjectedParam

internal class GamePreparationViewModel(
    @InjectedParam private val usersIds: Set<UserId>,
    private val gameRepository: GameRepository,
    private val preferenceRepository: PreferenceRepository,
    userRepository: UserRepository,
) : ViewModel() {
    private val _gamePreparationUiState = MutableStateFlow(GamePreparationUiState())
    val gamePreparationUiState = _gamePreparationUiState.asStateFlow()
}

internal data class GamePreparationUiState(
    val navigateToGame: GameId? = null,
    val createGameLoading: Boolean = false,
)
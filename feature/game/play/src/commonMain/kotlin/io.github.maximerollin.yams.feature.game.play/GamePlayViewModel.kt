package io.github.maximerollin.yams.feature.game.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.data.billing.BillingRepository
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.data.game.ScoreEntryRepository
import io.github.maximerollin.yams.data.game.model.CreateScoreEntry
import io.github.maximerollin.yams.data.game.model.ScoreCellRef
import io.github.maximerollin.yams.data.preference.GamePlayUiDensity
import io.github.maximerollin.yams.data.preference.PreferenceRepository
import io.github.maximerollin.yams.feature.game.play.domain.GetGamePlayStateUseCase
import io.github.maximerollin.yams.feature.game.play.model.GamePlayStateUi
import io.github.maximerollin.yams.feature.game.play.model.GameStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam

internal class GamePlayViewModel(
    @InjectedParam private val gameId: GameId,
    private val gameRepository: GameRepository,
    private val scoreEntryRepository: ScoreEntryRepository,
    private val getGetGamePlayStateUseCase: GetGamePlayStateUseCase,
    private val preferenceRepository: PreferenceRepository,
    billingRepository: BillingRepository,
) : ViewModel() {

    val gamePlayStateUi: StateFlow<GamePlayStateUi?> =
        getGetGamePlayStateUseCase(gameId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )

    val isHapticFeedbackEnabled: StateFlow<Boolean> =
        preferenceRepository.getIsHapticFeedbackEnabled()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = true,
            )

    val gamePlayUiDensity: StateFlow<GamePlayUiDensity> =
        preferenceRepository.getGamePlayUiDensity()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = GamePlayUiDensity.NORMAL,
            )

    val hasSeenGamePlayDensityDiscovery: StateFlow<Boolean?> =
        preferenceRepository.getHasSeenGamePlayDensityDiscovery()
            .map<Boolean, Boolean?> { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )

    val hasSeenDiceAssistantDiscovery: StateFlow<Boolean?> =
        preferenceRepository.getHasSeenDiceAssistantDiscovery()
            .map<Boolean, Boolean?> { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )

    val hasSeenDiceAssistantGuide: StateFlow<Boolean?> =
        preferenceRepository.getHasSeenDiceAssistantGuide()
            .map<Boolean, Boolean?> { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )

    val isYamsPlus: StateFlow<Boolean> =
        billingRepository.getYamsPlusStatus()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false,
            )

    private val _navigateToGameResult: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val navigateToGameResult: StateFlow<Boolean> = _navigateToGameResult

    fun onGamePlayUiDensityChange(density: GamePlayUiDensity) {
        viewModelScope.launch {
            preferenceRepository.setGamePlayUiDensity(density)
        }
    }

    fun onGamePlayDensityDiscoverySeen() {
        viewModelScope.launch {
            preferenceRepository.setHasSeenGamePlayDensityDiscovery()
        }
    }

    fun onDiceAssistantDiscoverySeen() {
        viewModelScope.launch {
            preferenceRepository.setHasSeenDiceAssistantDiscovery()
        }
    }

    fun onDiceAssistantGuideSeen() {
        viewModelScope.launch {
            preferenceRepository.setHasSeenDiceAssistantGuide()
        }
    }

    fun onScore(
        score: Int,
        cell: ScoreCellRef,
        awardExtraFiveOfAKindBonus: Boolean,
    ) {
        if (gamePlayStateUi.value?.status != GameStatus.ONGOING) return

        viewModelScope.launch {
            gamePlayStateUi.value?.let { state ->
                scoreEntryRepository.createScoreEntry(
                    CreateScoreEntry(
                        gameId = gameId,
                        userId = state.currentPlayer.userId,
                        cell = cell,
                        score = score,
                        awardsExtraFiveOfAKindBonus = awardExtraFiveOfAKindBonus,
                    )
                )
            }
        }
    }

    fun onUndo() {
        viewModelScope.launch {
            gameRepository.undoLastMove(gameId)
        }
    }

    fun onGoToResults() {
        if (gamePlayStateUi.value?.status != GameStatus.ENDED) return
        _navigateToGameResult.value = true
    }

    fun onGameResultNavigationHandled() {
        _navigateToGameResult.value = false
    }
}

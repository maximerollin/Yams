package io.github.maximerollin.yams.feature.game.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.data.game.ScoreEntryRepository
import io.github.maximerollin.yams.data.game.model.CreateScoreEntry
import io.github.maximerollin.yams.data.game.model.ScoreCellRef
import io.github.maximerollin.yams.feature.game.play.domain.FinishGameUseCase
import io.github.maximerollin.yams.feature.game.play.domain.GetGamePlayStateUseCase
import io.github.maximerollin.yams.feature.game.play.model.GamePlayStateUi
import io.github.maximerollin.yams.feature.game.play.model.GameStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam

internal class GamePlayViewModel(
    @InjectedParam private val gameId: GameId,
    private val gameRepository: GameRepository,
    private val scoreEntryRepository: ScoreEntryRepository,
    private val finishGameUseCase: FinishGameUseCase,
    private val getGetGamePlayStateUseCase: GetGamePlayStateUseCase,
) : ViewModel() {

    val gamePlayStateUi: StateFlow<GamePlayStateUi?> =
        getGetGamePlayStateUseCase(gameId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )

    private val _navigateToGameResult: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val navigateToGameResult: StateFlow<Boolean> = _navigateToGameResult

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

    fun onFinishGame() {
        viewModelScope.launch {
            gamePlayStateUi.value?.let { state ->
                finishGameUseCase(gameId, state)
                _navigateToGameResult.value = true
            }
        }
    }
}

package io.github.maximerollin.yams.feature.game.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.maximerollin.yams.core.analytics.AnalyticsTracker
import io.github.maximerollin.yams.core.analytics.toAnalyticsAverageBucket
import io.github.maximerollin.yams.core.analytics.toAnalyticsCountBucket
import io.github.maximerollin.yams.core.model.Game
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.review.InAppReview
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.data.preference.PreferenceRepository
import io.github.maximerollin.yams.feature.game.result.domain.FinishGameUseCase
import io.github.maximerollin.yams.feature.game.result.domain.GetGameResultPreviewUseCase
import io.github.maximerollin.yams.feature.game.result.model.GameResultUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

internal class GameResultViewModel(
    @InjectedParam private val gameId: GameId,
    private val gameRepository: GameRepository,
    private val preferenceRepository: PreferenceRepository,
    private val getGameResultPreviewUseCase: GetGameResultPreviewUseCase,
    private val finishGameUseCase: FinishGameUseCase,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {

    val gameResultUiState: StateFlow<GameResultUiState?> =
        getGameResultPreviewUseCase(gameId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )

    private val _navigationTarget: MutableStateFlow<GameResultNavigationTarget?> =
        MutableStateFlow(null)
    val navigationTarget: StateFlow<GameResultNavigationTarget?> = _navigationTarget

    private val _isActionInProgress: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isActionInProgress: StateFlow<Boolean> = _isActionInProgress

    fun onUndoLastMove() {
        val state = gameResultUiState.value ?: return
        if (state.game !is Game.GameInProgress || _isActionInProgress.value) return

        viewModelScope.launch {
            _isActionInProgress.value = true
            try {
                gameRepository.undoLastMove(gameId)
                analyticsTracker.capture(
                    event = "move undone",
                    properties = mapOf(
                        "source" to "game_result",
                        "rule_set" to state.game.settings.ruleSet.name.lowercase(),
                    ),
                )
                _navigationTarget.value = GameResultNavigationTarget.GAME_PLAY
            } finally {
                if (_navigationTarget.value == null) {
                    _isActionInProgress.value = false
                }
            }
        }
    }

    fun onFinishGame() {
        val state = gameResultUiState.value ?: return
        if (state.game !is Game.GameInProgress || _isActionInProgress.value) return

        viewModelScope.launch {
            _isActionInProgress.value = true
            try {
                finishGameUseCase(gameId, state)
                analyticsTracker.capture(
                    event = "game completed",
                    properties = mapOf(
                        "rule_set" to state.game.settings.ruleSet.name.lowercase(),
                        "player_count_bucket" to state.playerResults.size.toAnalyticsCountBucket(),
                        "winner_count_bucket" to state.winnerResults.size.toAnalyticsCountBucket(),
                        "total_turns_bucket" to state.totalTurns.toAnalyticsCountBucket(),
                        "total_yams_bucket" to state.totalYamCount.toAnalyticsCountBucket(),
                        "average_score_per_turn_bucket" to state.averageScorePerTurn.toAnalyticsAverageBucket(),
                        "column_count_bucket" to state.game.settings.columnCount.toAnalyticsCountBucket(),
                    ),
                )
                _navigationTarget.value = GameResultNavigationTarget.HOME
            } finally {
                if (_navigationTarget.value == null) {
                    _isActionInProgress.value = false
                }
            }
        }
    }

    fun onNavigationHandled() {
        _navigationTarget.value = null
        _isActionInProgress.value = false
    }

    @OptIn(ExperimentalTime::class)
    fun checkInAppReview() {
        viewModelScope.launch {
            val numberOfFinishedGames = gameRepository.getNumberOfFinishedGames().first()
            val lastInAppReviewShownDate = preferenceRepository.getInAppReviewShownDate().first()
            val isSixMonthsPassed = lastInAppReviewShownDate?.let {
                val now = Clock.System.now()
                val sixMonthsLater = it + (30 * 6).days
                now > sixMonthsLater
            } ?: false
            val shouldShowInAppReview =
                numberOfFinishedGames >= 3 && (lastInAppReviewShownDate == null || isSixMonthsPassed)

            if (shouldShowInAppReview) {
                delay(1200.milliseconds)
                InAppReview.requestReview()
                preferenceRepository.inAppReviewShown()
                analyticsTracker.capture(
                    event = "review prompt shown",
                    properties = mapOf(
                        "finished_games_bucket" to numberOfFinishedGames.toAnalyticsCountBucket(),
                    ),
                )
            }
        }
    }
}

internal enum class GameResultNavigationTarget {
    GAME_PLAY,
    HOME,
}

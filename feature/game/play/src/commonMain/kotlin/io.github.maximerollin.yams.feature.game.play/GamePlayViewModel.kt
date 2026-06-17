package io.github.maximerollin.yams.feature.game.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.maximerollin.yams.core.analytics.AnalyticsTracker
import io.github.maximerollin.yams.core.analytics.toAnalyticsCountBucket
import io.github.maximerollin.yams.core.analytics.toAnalyticsScoreBucket
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.ScoreKey
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.data.game.ScoreEntryRepository
import io.github.maximerollin.yams.data.game.model.CreateScoreEntry
import io.github.maximerollin.yams.data.game.model.ScoreCellRef
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
    private val getGetGamePlayStateUseCase: GetGamePlayStateUseCase,
    private val analyticsTracker: AnalyticsTracker,
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
                analyticsTracker.capture(
                    event = "score entered",
                    properties = mapOf(
                        "rule_set" to state.game.settings.ruleSet.name.lowercase(),
                        "score_key" to cell.key.toAnalyticsScoreKey(),
                        "score_bucket" to score.toAnalyticsScoreBucket(),
                        "column_count_bucket" to state.game.settings.columnCount.toAnalyticsCountBucket(),
                        "awards_extra_five_of_a_kind_bonus" to awardExtraFiveOfAKindBonus,
                    ),
                )
            }
        }
    }

    fun onUndo() {
        val state = gamePlayStateUi.value
        viewModelScope.launch {
            gameRepository.undoLastMove(gameId)
            analyticsTracker.capture(
                event = "move undone",
                properties = mapOf(
                    "source" to "game_play",
                    "rule_set" to state?.game?.settings?.ruleSet?.name?.lowercase(),
                ),
            )
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

private fun ScoreKey.toAnalyticsScoreKey(): String =
    if (value.startsWith("custom_")) "custom" else value

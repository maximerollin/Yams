package io.github.maximerollin.yams.feature.game.result.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.feature.game.result.GameResultRoute
import kotlinx.serialization.Serializable

@Serializable
public data class GameResultScreen(val gameId: String)

public fun NavController.navigateToGameResult(
    gameId: GameId,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(route = GameResultScreen(gameId.value), builder = builder)
}

public fun NavGraphBuilder.gameResultScreen(
    onNavigateHome: () -> Unit,
    onNavigateToGame: (GameId) -> Unit,
) {
    composable<GameResultScreen> {
        GameResultRoute(
            gameId = GameId(it.toRoute<GameResultScreen>().gameId),
            onNavigateHome = onNavigateHome,
            onNavigateToGame = onNavigateToGame,
        )
    }
}

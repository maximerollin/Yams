package io.github.maximerollin.yams.feature.game.play.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.feature.game.play.GamePlayRoute
import kotlinx.serialization.Serializable


@Serializable
public data class GamePlayRoute(val gameId: String)

public fun NavController.navigateToGamePlay(
    gameId: GameId,
    builder: NavOptionsBuilder.() -> Unit,
) {
    navigate(route = GamePlayRoute(gameId.value), builder = builder)
}

public fun NavGraphBuilder.gamePlayScreen(
    onNavigateHome: () -> Unit,
    onNavigateToResults: (gameId: GameId) -> Unit,
) {
    composable<GamePlayRoute> {
        GamePlayRoute(
            gameId = GameId(it.toRoute<GamePlayRoute>().gameId),
            onNavigateHome = onNavigateHome,
            onNavigateToResults = onNavigateToResults,
        )
    }
}

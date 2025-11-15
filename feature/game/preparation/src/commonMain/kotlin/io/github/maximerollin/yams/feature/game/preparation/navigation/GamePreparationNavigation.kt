package io.github.maximerollin.yams.feature.game.preparation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.maximerollin.yams.feature.game.preparation.GamePreparationRoute
import kotlinx.serialization.Serializable

@Serializable
public data object GamePreparationRoute

public fun NavController.navigateToGamePreparation() {
    navigate(GamePreparationRoute)
}

public fun NavGraphBuilder.gamePreparationScreen(
    onStartGame: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable<GamePreparationRoute> {
        GamePreparationRoute(
            onStartGame = onStartGame,
            onNavigateBack = onNavigateBack,
        )
    }
}



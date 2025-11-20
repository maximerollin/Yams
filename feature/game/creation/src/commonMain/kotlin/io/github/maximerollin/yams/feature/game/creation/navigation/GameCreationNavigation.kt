package io.github.maximerollin.yams.feature.game.creation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.feature.game.creation.GameCreationRoute
import kotlinx.serialization.Serializable

@Serializable
public object GameCreationRoute

public fun NavController.navigateToGameCreation(builder: NavOptionsBuilder.() -> Unit) {
    navigate(GameCreationRoute, builder = builder)
}

public fun NavGraphBuilder.gameCreationScreen(
    onNavigateHome: () -> Unit,
    onNavigateWelcome: () -> Unit,
    onNavigateGamePreparation: (Set<UserId>) -> Unit
) {
    composable<GameCreationRoute> {
        GameCreationRoute(
            onNavigateHome = onNavigateHome,
            onNavigateWelcome = onNavigateWelcome,
            onNavigateGamePreparation = onNavigateGamePreparation,
        )
    }
}



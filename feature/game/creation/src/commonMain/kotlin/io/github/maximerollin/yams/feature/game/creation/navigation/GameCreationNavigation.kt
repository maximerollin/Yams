package io.github.maximerollin.yams.feature.game.creation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.maximerollin.yams.feature.game.creation.GameCreationRoute
import kotlinx.serialization.Serializable

@Serializable
public data object GameCreationRoute

public fun NavController.navigateToGameCreation() {
    navigate(GameCreationRoute)
}

public fun NavGraphBuilder.gameCreationScreen(
    onNavigateToPreparation: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable<GameCreationRoute> {
        GameCreationRoute(
            onNavigateToPreparation = onNavigateToPreparation,
            onNavigateBack = onNavigateBack,
        )
    }
}



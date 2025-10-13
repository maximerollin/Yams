package io.github.maximerollin.yams.feature.welcome.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import io.github.maximerollin.yams.feature.welcome.WelcomeRoute
import kotlinx.serialization.Serializable

@Serializable
public object WelcomeRoute

public fun NavController.navigateToWelcome(builder: NavOptionsBuilder.() -> Unit) {
    navigate(WelcomeRoute, builder = builder)
}

public fun NavGraphBuilder.welcomeScreen(
    onNavigateToNewGame: () -> Unit,
    onNavigateToPlayers: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToStatistics: () -> Unit,
) {
    composable<WelcomeRoute> {
        WelcomeRoute(
            onNavigateToNewGame = onNavigateToNewGame,
            onNavigateToPlayers = onNavigateToPlayers,
            onNavigateToHistory = onNavigateToHistory,
            onNavigateToStatistics = onNavigateToStatistics
        )
    }
}

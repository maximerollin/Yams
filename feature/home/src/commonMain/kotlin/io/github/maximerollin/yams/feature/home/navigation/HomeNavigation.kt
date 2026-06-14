package io.github.maximerollin.yams.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.feature.home.HomeRoute
import kotlinx.serialization.Serializable

@Serializable
public object HomeRoute

public fun NavController.navigateToHome(builder: NavOptionsBuilder.() -> Unit) {
    navigate(HomeRoute, builder = builder)
}

public fun NavGraphBuilder.homeScreen(
    onNavigateToGameCreation: () -> Unit,
    onNavigateToGamePlay: (GameId) -> Unit,
    onNavigateToGameResult: (GameId) -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToPaywall: () -> Unit,
) {
    composable<HomeRoute> {
        HomeRoute(
            onNavigateToGameCreation = onNavigateToGameCreation,
            onNavigateToGamePlay = onNavigateToGamePlay,
            onNavigateToGameResult = onNavigateToGameResult,
            onNavigateToUsers = onNavigateToUsers,
            onNavigateToPaywall = onNavigateToPaywall,
        )
    }
}

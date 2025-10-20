package io.github.maximerollin.yams.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import io.github.maximerollin.yams.feature.home.HomeRoute
import kotlinx.serialization.Serializable

@Serializable
public object HomeRoute

public fun NavController.navigateToHome(builder: NavOptionsBuilder.() -> Unit) {
    navigate(HomeRoute, builder = builder)
}

public fun NavGraphBuilder.homeScreen() {
    composable<HomeRoute> {
        HomeRoute(
        )
    }
}

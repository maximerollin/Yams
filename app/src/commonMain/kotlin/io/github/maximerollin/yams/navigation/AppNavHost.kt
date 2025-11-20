package io.github.maximerollin.yams.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import io.github.maximerollin.yams.AppViewModel
import io.github.maximerollin.yams.feature.game.creation.navigation.GameCreationRoute
import io.github.maximerollin.yams.feature.game.creation.navigation.gameCreationScreen
import io.github.maximerollin.yams.feature.game.creation.navigation.navigateToGameCreation
import io.github.maximerollin.yams.feature.game.preparation.navigation.gamePreparationScreen
import io.github.maximerollin.yams.feature.game.preparation.navigation.navigateToGamePreparation
import io.github.maximerollin.yams.feature.home.navigation.HomeRoute
import io.github.maximerollin.yams.feature.home.navigation.homeScreen
import io.github.maximerollin.yams.feature.home.navigation.navigateToHome
import io.github.maximerollin.yams.feature.welcome.navigation.WelcomeRoute
import io.github.maximerollin.yams.feature.welcome.navigation.navigateToWelcome
import io.github.maximerollin.yams.feature.welcome.navigation.welcomeScreen
import org.koin.compose.koinInject
import kotlin.jvm.JvmSuppressWildcards

@Composable
internal fun AppNavHost(
    predictiveBackGestureAnimations: PredictiveBackGestureAnimations?,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    appViewModel: AppViewModel = koinInject()
) {
    if (predictiveBackGestureAnimations != null) {
        NavHost(
            navController = navController,
            startDestination = appViewModel.startDestination,
            modifier = modifier,
            enterTransition = predictiveBackGestureAnimations.enterTransition,
            exitTransition = predictiveBackGestureAnimations.exitTransition,
            popEnterTransition = predictiveBackGestureAnimations.popEnterTransition,
            popExitTransition = predictiveBackGestureAnimations.popExitTransition,
        ) {
            screens(navController)
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = appViewModel.startDestination,
            modifier = modifier,
        ) {
            screens(navController)
        }
    }
}

class PredictiveBackGestureAnimations(
    val enterTransition: EnterAnimation,
    val exitTransition: ExitAnimation,
    val popEnterTransition: EnterAnimation,
    val popExitTransition: ExitAnimation,
)

typealias EnterAnimation = @JvmSuppressWildcards (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition)
typealias ExitAnimation = @JvmSuppressWildcards (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition)

private fun NavGraphBuilder.screens(navController: NavHostController) {
    welcomeScreen(
        onNavigateToNewGame = {
            navController.navigateToGameCreation {
                launchSingleTop = true
                popUpTo(navController.graph.id) { inclusive = true }
                navController.graph.setStartDestination(GameCreationRoute)
            }
        },
    )

    homeScreen(

    )

    gameCreationScreen(
        onNavigateHome = {
            navController.navigateToHome {
                launchSingleTop = true
                popUpTo(navController.graph.id) { inclusive = true }
                navController.graph.setStartDestination(HomeRoute)
            }
        },
        onNavigateWelcome = {
            navController.navigateToWelcome {
                launchSingleTop = true
                popUpTo(navController.graph.id) { inclusive = true }
                navController.graph.setStartDestination(WelcomeRoute)
            }
        },
        onNavigateGamePreparation = { users ->
            navController.navigateToGamePreparation(users)
        }
    )

    gamePreparationScreen(
    )
}

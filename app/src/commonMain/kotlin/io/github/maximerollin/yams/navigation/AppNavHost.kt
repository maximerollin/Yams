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
        },
        onNavigateToPlayers = {
        },
        onNavigateToHistory = {
        },
        onNavigateToStatistics = {
        }
    )
}

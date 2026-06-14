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
import io.github.maximerollin.yams.feature.game.play.navigation.gamePlayScreen
import io.github.maximerollin.yams.feature.game.play.navigation.navigateToGamePlay
import io.github.maximerollin.yams.feature.game.preparation.navigation.gamePreparationScreen
import io.github.maximerollin.yams.feature.game.preparation.navigation.navigateToGamePreparation
import io.github.maximerollin.yams.feature.game.result.navigation.gameResultScreen
import io.github.maximerollin.yams.feature.game.result.navigation.navigateToGameResult
import io.github.maximerollin.yams.feature.home.navigation.HomeRoute
import io.github.maximerollin.yams.feature.home.navigation.homeScreen
import io.github.maximerollin.yams.feature.home.navigation.navigateToHome
import io.github.maximerollin.yams.feature.paywall.navigation.navigateToPaywall
import io.github.maximerollin.yams.feature.paywall.navigation.paywallScreen
import io.github.maximerollin.yams.feature.user.edition.navigation.navigateToUserEdition
import io.github.maximerollin.yams.feature.user.edition.navigation.userEditionScreen
import io.github.maximerollin.yams.feature.user.history.navigation.navigateToUserHistory
import io.github.maximerollin.yams.feature.user.history.navigation.userHistoryScreen
import io.github.maximerollin.yams.feature.user.profile.navigation.navigateToUserProfile
import io.github.maximerollin.yams.feature.user.profile.navigation.userProfileScreen
import io.github.maximerollin.yams.feature.user.users.navigation.navigateToUsers
import io.github.maximerollin.yams.feature.user.users.navigation.usersScreen
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
        onNavigateToGameCreation = {
            navController.navigateToGameCreation {
                launchSingleTop = true
            }
        },
        onNavigateToGamePlay = { gameId ->
            navController.navigateToGamePlay(gameId) {
                launchSingleTop = true
            }
        },
        onNavigateToGameResult = { gameId ->
            navController.navigateToGameResult(gameId)
        },
        onNavigateToUsers = {
            navController.navigateToUsers()
        },
        onNavigateToPaywall = {
            navController.navigateToPaywall(fromScreen = "home")
        },
    )

    usersScreen(
        onNavigateBack = navController::navigateUp,
        onNavigateToUserProfile = { userId ->
            navController.navigateToUserProfile(userId)
        },
    )

    userProfileScreen(
        onNavigateBack = navController::navigateUp,
        onNavigateToUsers = {
            navController.navigateToUsers {
                launchSingleTop = true
                popUpTo(HomeRoute)
            }
        },
        onNavigateToUserEdition = { userId ->
            navController.navigateToUserEdition(userId)
        },
        onNavigateToUserHistory = { userId ->
            navController.navigateToUserHistory(userId)
        },
        onNavigateToPaywall = {
            navController.navigateToPaywall(fromScreen = "profile")
        },
    )

    userHistoryScreen(
        onNavigateBack = navController::navigateUp,
        onNavigateToGameResult = { gameId ->
            navController.navigateToGameResult(gameId)
        },
        onNavigateToPaywall = {
            navController.navigateToPaywall(fromScreen = "history")
        },
    )

    userEditionScreen(
        onNavigateBack = navController::navigateUp,
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
        navController = navController,
        onNavigateBack = navController::navigateUp,
        onNavigateGame = { gameId ->
            navController.navigateToGamePlay(gameId) {
                launchSingleTop = true
                popUpTo(GameCreationRoute) { inclusive = true }
            }
        }
    )

    gamePlayScreen(
        onNavigateHome = {
            navController.navigateToHome {
                launchSingleTop = true
                popUpTo(navController.graph.id) { inclusive = true }
                navController.graph.setStartDestination(HomeRoute)
            }
        },
        onNavigateToResults = { gameId ->
            navController.navigateToGameResult(gameId) {
                launchSingleTop = true
            }
        },
    )

    gameResultScreen(
        onNavigateBack = {
            if (!navController.popBackStack()) {
                navController.navigateToHome {
                    launchSingleTop = true
                    popUpTo(navController.graph.id) { inclusive = true }
                    navController.graph.setStartDestination(HomeRoute)
                }
            }
        },
        onNavigateHome = {
            navController.navigateToHome {
                launchSingleTop = true
                popUpTo(navController.graph.id) { inclusive = true }
                navController.graph.setStartDestination(HomeRoute)
            }
        },
        onNavigateToGame = { gameId ->
            navController.navigateToGamePlay(gameId) {
                navController.currentDestination?.id?.let {
                    popUpTo(it) { inclusive = true }
                }
                launchSingleTop = true
            }
        },
        onNavigateToUserProfile = { userId ->
            navController.navigateToUserProfile(userId)
        },
    )

    paywallScreen(
        onNavigateBack = navController::navigateUp,
        onNavigateHome = {
            navController.navigateToHome {
                launchSingleTop = true
                popUpTo(navController.graph.id) { inclusive = true }
                navController.graph.setStartDestination(HomeRoute)
            }
        },
    )
}

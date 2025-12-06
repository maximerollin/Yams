package io.github.maximerollin.yams.feature.game.preparation.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.feature.game.preparation.GamePreparationRoute
import io.github.maximerollin.yams.feature.game.preparation.GamePreparationViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
private data class GamePreparationRoute(private val usersStr: String) {
    constructor(users: Set<UserId>) : this(users.joinToString(",") { it.value })

    val users: Set<UserId>
        get() = usersStr.split(",").map(::UserId).toSet()
}

@Serializable
public object GamePreparationScreen

public fun NavController.navigateToGamePreparation(
    usersIds: Set<UserId>,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(GamePreparationRoute(usersIds), builder = builder)
}

public fun NavGraphBuilder.gamePreparationScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onNavigateGame: (GameId) -> Unit
) {
    navigation<GamePreparationRoute>(startDestination = GamePreparationScreen) {
        composable<GamePreparationScreen> {
            val parentEntry = remember(it) {
                navController.getBackStackEntry<GamePreparationRoute>()
            }

            val usersIds = parentEntry.toRoute<GamePreparationRoute>().users
            val viewModel: GamePreparationViewModel = koinViewModel(viewModelStoreOwner = parentEntry) { parametersOf(usersIds) }
            GamePreparationRoute(
                viewModel = viewModel,
                usersIds = parentEntry.toRoute<GamePreparationRoute>().users,
                onNavigateBack = onNavigateBack,
                onNavigateGame = onNavigateGame,
            )
        }
    }
}



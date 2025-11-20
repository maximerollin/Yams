package io.github.maximerollin.yams.feature.game.preparation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.feature.game.preparation.GamePreparationRoute
import kotlinx.serialization.Serializable

@Serializable
public data object GamePreparationRoute

public fun NavController.navigateToGamePreparation(
    usersIds: Set<UserId>,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(GamePreparationRoute)
}

public fun NavGraphBuilder.gamePreparationScreen(

) {
    composable<GamePreparationRoute> {
        GamePreparationRoute()
    }
}



package io.github.maximerollin.yams.feature.user.history.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.feature.user.history.UserHistoryRoute as UserHistoryScreenRoute
import kotlinx.serialization.Serializable

@Serializable
public data class UserHistoryRoute(val userId: String)

public fun NavController.navigateToUserHistory(
    userId: UserId,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(UserHistoryRoute(userId.value), builder = builder)
}

public fun NavGraphBuilder.userHistoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToGameResult: (GameId) -> Unit,
    onNavigateToPaywall: () -> Unit,
) {
    composable<UserHistoryRoute> {
        UserHistoryScreenRoute(
            userId = UserId(it.toRoute<UserHistoryRoute>().userId),
            onNavigateBack = onNavigateBack,
            onNavigateToGameResult = onNavigateToGameResult,
            onNavigateToPaywall = onNavigateToPaywall,
        )
    }
}

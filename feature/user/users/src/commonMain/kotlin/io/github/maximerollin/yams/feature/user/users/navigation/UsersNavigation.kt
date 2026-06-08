package io.github.maximerollin.yams.feature.user.users.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.feature.user.users.UsersRoute as UsersScreenRoute
import kotlinx.serialization.Serializable

@Serializable
public object UsersRoute

public fun NavController.navigateToUsers(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(UsersRoute, builder = builder)
}

public fun NavGraphBuilder.usersScreen(
    onNavigateBack: () -> Unit,
    onNavigateToUserProfile: (UserId) -> Unit,
) {
    composable<UsersRoute> {
        UsersScreenRoute(
            onNavigateBack = onNavigateBack,
            onNavigateToUserProfile = onNavigateToUserProfile,
        )
    }
}
